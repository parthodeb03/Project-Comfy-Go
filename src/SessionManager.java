import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private final Connection conn;

    // key = userId
    private final Map<String, SessionData> activeSessions =
            Collections.synchronizedMap(new HashMap<>());

    private static final long SESSION_TIMEOUT_MS = 30L * 60L * 1000L; // 30 minutes

    public SessionManager(Connection conn) {
        this.conn = conn;
    }

    public static class SessionData {
        private final String userId;
        private final String userRole;
        private final String userName;

        private final long createdAt;
        private long lastActivityAt;

        private final String sessionToken;

        public SessionData(String userId, String userRole, String userName) {
            this.userId = userId;
            this.userRole = userRole;
            this.userName = userName;
            this.createdAt = System.currentTimeMillis();
            this.lastActivityAt = this.createdAt;
            this.sessionToken = UUID.randomUUID().toString();
        }

        public String getUserId() { return userId; }
        public String getUserRole() { return userRole; }
        public String getUserName() { return userName; }

        public long getCreatedAt() { return createdAt; }
        public long getLastActivityAt() { return lastActivityAt; }

        public String getSessionToken() { return sessionToken; }

        public void updateActivity() { lastActivityAt = System.currentTimeMillis(); }

        public boolean isExpired() {
            return (System.currentTimeMillis() - lastActivityAt) > SESSION_TIMEOUT_MS;
        }

        public long getDurationMinutes() {
            return (System.currentTimeMillis() - createdAt) / (60L * 1000L);
        }
    }

    public synchronized String createSession(String userId, String userRole, String userName) {
        if (userId == null || userId.isBlank()) return null;

        SessionData existing = activeSessions.get(userId);
        if (existing != null) destroySession(userId);

        SessionData s = new SessionData(userId, userRole, userName);
        activeSessions.put(userId, s);

        logSessionEvent("SESSION_CREATED", userId, "Token: " + s.getSessionToken());
        return s.getSessionToken();
    }

    public synchronized SessionData getSession(String userId) {
        if (userId == null || userId.isBlank()) return null;

        SessionData s = activeSessions.get(userId);
        if (s == null) return null;

        if (s.isExpired()) {
            destroySession(userId);
            return null;
        }

        s.updateActivity();
        return s;
    }

    public synchronized boolean verifySession(String userId, String token) {
        if (token == null) return false;
        SessionData s = getSession(userId);
        return s != null && token.equals(s.getSessionToken());
    }

    public synchronized void destroySession(String userId) {
        if (userId == null || userId.isBlank()) return;

        SessionData removed = activeSessions.remove(userId);
        if (removed != null) {
            logSessionEvent("SESSION_DESTROYED", userId, "Duration(min): " + removed.getDurationMinutes());
        }
    }

    public synchronized void clearExpiredSessions() {
        activeSessions.entrySet().removeIf(e -> e.getValue() == null || e.getValue().isExpired());
    }

    private void logSessionEvent(String eventType, String userId, String details) {
        if (conn == null) return;

        String sql = "INSERT INTO session_logs (eventtype, userid, details) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventType);
            ps.setString(2, userId);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to log session event: " + e.getMessage());
        }
    }
}