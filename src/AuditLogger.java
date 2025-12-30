import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogger {

    private final Connection conn;
    private static final int BRUTE_FORCE_THRESHOLD = 5;
    private static final int BRUTE_FORCE_WINDOW_MINUTES = 15;

    public AuditLogger(Connection conn) {
        this.conn = conn;
    }

    public void logAuthEvent(String eventType, String userId, String email, String details) {
        logEvent(eventType, userId, email, "AUTH", details);
    }

    public void logTransactionEvent(String eventType, String userId, String details) {
        logEvent(eventType, userId, null, "TRANSACTION", details);
    }

    public void logActivityEvent(String eventType, String userId, String details) {
        logEvent(eventType, userId, null, "ACTIVITY", details);
    }

    public void logSecurityEvent(String eventType, String userId, String details) {
        logEvent(eventType, userId, null, "SECURITY", details);
    }

    private void logEvent(String eventType, String userId, String email, String category, String details) {
        if (conn == null) return;

        String sql = "INSERT INTO audit_logs (eventtype, userid, email, category, details, ipaddress) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eventType);
            ps.setString(2, userId);
            ps.setString(3, email);
            ps.setString(4, category);
            ps.setString(5, sanitizeDetails(details));
            ps.setString(6, "127.0.0.1");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("✗ Failed to log event: " + e.getMessage());
        }
    }

    public boolean isBruteForceRisk(String email) {
        if (conn == null || email == null || email.isBlank()) return false;

        String sql = "SELECT COUNT(*) AS cnt " +
                     "FROM audit_logs " +
                     "WHERE eventtype = 'LOGIN_FAILED' AND email = ? " +
                     "AND timestamp >= DATE_SUB(NOW(), INTERVAL ? MINUTE)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, BRUTE_FORCE_WINDOW_MINUTES);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt") >= BRUTE_FORCE_THRESHOLD;
            }
        } catch (SQLException e) {
            System.err.println("✗ Brute force check failed: " + e.getMessage());
        }
        return false;
    }

    public List<String> getRecentLogs(int limit) {
        List<String> out = new ArrayList<>();
        if (conn == null) return out;

        String sql = "SELECT logid, eventtype, userid, email, category, details, ipaddress, timestamp " +
                     "FROM audit_logs ORDER BY timestamp DESC LIMIT ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(rs.getInt("logid") + " | " +
                            rs.getString("timestamp") + " | " +
                            rs.getString("category") + " | " +
                            rs.getString("eventtype") + " | " +
                            rs.getString("userid") + " | " +
                            rs.getString("email") + " | " +
                            rs.getString("details"));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to read logs: " + e.getMessage());
        }
        return out;
    }

    public boolean exportLogsToCSV(String filename) {
        if (conn == null) return false;

        String sql = "SELECT logid, eventtype, userid, email, category, details, ipaddress, timestamp " +
                     "FROM audit_logs ORDER BY timestamp DESC";

        try (PrintWriter w = new PrintWriter(filename);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            w.println("LOG_ID,EVENT_TYPE,USER_ID,EMAIL,CATEGORY,DETAILS,IP_ADDRESS,TIMESTAMP");
            while (rs.next()) {
                w.println(
                        rs.getInt("logid") + "," +
                        csv(rs.getString("eventtype")) + "," +
                        csv(rs.getString("userid")) + "," +
                        csv(rs.getString("email")) + "," +
                        csv(rs.getString("category")) + "," +
                        csv(rs.getString("details")) + "," +
                        csv(rs.getString("ipaddress")) + "," +
                        rs.getTimestamp("timestamp")
                );
            }
            System.out.println("✓ Logs exported to " + filename);
            return true;

        } catch (Exception e) {
            System.err.println("✗ Export failed: " + e.getMessage());
            return false;
        }
    }

    private String sanitizeDetails(String details) {
        if (details == null) return null;
        String cleaned = details.replaceAll("[<>\"'%;()&+]", "");
        return cleaned.substring(0, Math.min(cleaned.length(), 500));
    }

    private String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}