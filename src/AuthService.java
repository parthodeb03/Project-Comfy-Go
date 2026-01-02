import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.regex.Pattern;

public class AuthService {

    private final Connection conn;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(?:\\+?88)?01[3-9]\\d{8}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^.{6,}$");

    private static final SecureRandom RAND = new SecureRandom();

    public AuthService(Connection conn) {
        this.conn = conn;
    }

    // -------------------- Tourist --------------------

    public boolean registerTourist(String name, String email, String phone, String nid,
                                   String passport, String dob, String country,
                                   String address, String password) {

        name = safeTrim(name);
        email = safeTrim(email);
        phone = safeTrim(phone);
        nid = safeTrim(nid);
        passport = safeTrim(passport);
        dob = safeTrim(dob);
        country = safeTrim(country);
        address = safeTrim(address);

        if (!validateName(name) || !validateEmail(email) || !validatePhone(phone) || !validatePassword(password)) {
            System.out.println("Invalid input format!");
            return false;
        }

        try {
            if (emailExistsInUsers(email)) {
                System.out.println("Email already registered!");
                return false;
            }
            if (phoneExistsInUsers(phone)) {
                System.out.println("Phone already registered!");
                return false;
            }

            String userId = IdGenerator.uniqueNumericId(conn, "users", "userid", 12, 60);
            String hashed = hashPassword(password);

            String sql =
                    "INSERT INTO users " +
                    "(userid, username, useremail, userpassword, userphone, usernid, passportno, dateofbirth, country, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userId);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, hashed);
                ps.setString(5, phone);
                ps.setString(6, nid);
                ps.setString(7, passport);

                if (dob == null) ps.setNull(8, Types.DATE);
                else ps.setDate(8, Date.valueOf(dob));

                ps.setString(9, country);
                ps.setString(10, address);
                ps.executeUpdate();
            }

            System.out.println("Tourist registration successful! User ID: " + userId);
            return true;

        } catch (Exception e) {
            System.out.println("Tourist registration failed: " + e.getMessage());
            return false;
        }
    }

    public static class LoginResult {
    private final String id, name, role;
    public LoginResult(String id, String name, String role){ this.id=id; this.name=name; this.role=role; }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getRole(){ return role; }
}

public LoginResult login(String email, String password) {
    User u = loginTourist(email, password);
    if (u != null) return new LoginResult(u.getUserID(), u.getUserName(), "Tourist");
    Guide g = loginGuide(email, password);
    if (g != null) return new LoginResult(g.getGuideId(), g.getGuideName(), "Tour Guide");
    Manager m = loginManager(email, password);
    if (m != null) return new LoginResult(m.getManagerId(), m.getManagerName(), "Hotel Manager");
    return null;
}


    public User loginTourist(String email, String password) {
        email = safeTrim(email);
        if (email == null || password == null || password.isBlank()) return null;

        String sql = "SELECT userid, username, useremail, userpassword FROM users WHERE useremail = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String stored = rs.getString("userpassword");
                if (!verifyPassword(password, stored)) return null;

                return new User(rs.getString("userid"), rs.getString("username"), rs.getString("useremail"), "TOURIST");
            }
        } catch (SQLException e) {
            System.out.println("Tourist login error: " + e.getMessage());
            return null;
        }
    }

    // -------------------- Guide --------------------

    public boolean registerGuide(String name, String email, String phone,
                                 String division, String district, String languages,
                                 String specialization, int experience, String password) {

        name = safeTrim(name);
        email = safeTrim(email);
        phone = safeTrim(phone);

        if (!validateName(name) || !validateEmail(email) || !validatePhone(phone) || !validatePassword(password)) {
            System.out.println("Invalid input format!");
            return false;
        }

        if (experience < 0) experience = 0;

        try {
            if (emailExistsInUsers(email) || emailExistsInGuides(email)) {
                System.out.println("Email already registered!");
                return false;
            }

            String guideId = IdGenerator.uniqueNumericId(conn, "guides", "guideid", 12, 60);
            String hashed = hashPassword(password);

            String sql =
                    "INSERT INTO guides " +
                    "(guideid, guidename, guideemail, guidephone, guidepassword, guidedivision, guidedistrict, guidelanguage, " +
                    " specialization, rating, totalratings, isavailable, yearexperience, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 5.0, 0, TRUE, ?, 'ACTIVE')";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, guideId);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setString(5, hashed);
                ps.setString(6, safeTrim(division));
                ps.setString(7, safeTrim(district));
                ps.setString(8, safeTrim(languages));
                ps.setString(9, safeTrim(specialization));
                ps.setInt(10, experience);
                ps.executeUpdate();
            }

            System.out.println("Guide registration successful! Guide ID: " + guideId);
            return true;

        } catch (Exception e) {
            System.out.println("Guide registration failed: " + e.getMessage());
            return false;
        }
    }

    public Guide loginGuide(String email, String password) {
        email = safeTrim(email);
        if (email == null || password == null || password.isBlank()) return null;

        String sql = "SELECT guideid, guidename, guideemail, guidepassword, status FROM guides WHERE guideemail = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                if ("INACTIVE".equalsIgnoreCase(rs.getString("status"))) return null;
                if (!verifyPassword(password, rs.getString("guidepassword"))) return null;

                return new Guide(rs.getString("guideid"), rs.getString("guidename"), rs.getString("guideemail"));
            }
        } catch (SQLException e) {
            System.out.println("Guide login error: " + e.getMessage());
            return null;
        }
    }

    // -------------------- Manager --------------------

    public boolean registerManager(String name, String email, String phone,
                                   String managerNid,
                                   String hotelName, String hotelNid,
                                   String registrationNumber, String password) {

        name = safeTrim(name);
        email = safeTrim(email);
        phone = safeTrim(phone);
        managerNid = safeTrim(managerNid);

        if (!validateName(name) || !validateEmail(email) || !validatePhone(phone) || !validatePassword(password)) {
            System.out.println("Invalid input format!");
            return false;
        }

        try {
            if (emailExistsInUsers(email) || emailExistsInManagers(email)) {
                System.out.println("Email already registered!");
                return false;
            }

            String managerId = IdGenerator.uniqueNumericId(conn, "managers", "managerid", 12, 60);
            String hashed = hashPassword(password);

            String sql =
                    "INSERT INTO managers " +
                    "(managerid, managername, manageremail, managerphone, managernid, managerpassword, " +
                    " hotelname, hotelnid, registrationnumber, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, managerId);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, phone);
                ps.setString(5, managerNid);
                ps.setString(6, hashed);
                ps.setString(7, safeTrim(hotelName));
                ps.setString(8, safeTrim(hotelNid));
                ps.setString(9, safeTrim(registrationNumber));
                ps.executeUpdate();
            }

            System.out.println("Manager registration successful! Manager ID: " + managerId);
            return true;

        } catch (Exception e) {
            System.out.println("Manager registration failed: " + e.getMessage());
            return false;
        }
    }

    public Manager loginManager(String email, String password) {
        email = safeTrim(email);
        if (email == null || password == null || password.isBlank()) return null;

        String sql = "SELECT managerid, managername, manageremail, managerpassword, status FROM managers WHERE manageremail = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                if ("INACTIVE".equalsIgnoreCase(rs.getString("status"))) return null;
                if (!verifyPassword(password, rs.getString("managerpassword"))) return null;

                Manager m = new Manager();
                m.setManagerId(rs.getString("managerid"));
                m.setManagerName(rs.getString("managername"));
                m.setManagerEmail(rs.getString("manageremail"));
                m.setStatus(rs.getString("status"));
                return m;
            }
        } catch (SQLException e) {
            System.out.println("Manager login error: " + e.getMessage());
            return null;
        }
    }

    // -------------------- DB checks --------------------

    private boolean emailExistsInUsers(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE useremail = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean phoneExistsInUsers(String phone) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE userphone = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean emailExistsInGuides(String email) throws SQLException {
        String sql = "SELECT 1 FROM guides WHERE guideemail = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean emailExistsInManagers(String email) throws SQLException {
        String sql = "SELECT 1 FROM managers WHERE manageremail = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // -------------------- Validation --------------------

    private boolean validateName(String name) {
        return name != null && name.length() >= 2;
    }

    private boolean validateEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean validatePhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    private boolean validatePassword(String pw) {
        return pw != null && PASSWORD_PATTERN.matcher(pw).matches();
    }

    private String safeTrim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // -------------------- Password hashing --------------------

    private String hashPassword(String password) throws Exception {
        byte[] salt = new byte[16];
        RAND.nextBytes(salt);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    private boolean verifyPassword(String password, String stored) {
        try {
            if (stored == null || !stored.contains(":")) return false;
            String[] parts = stored.split(":", 2);
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] actual = md.digest(password.getBytes(StandardCharsets.UTF_8));

            if (actual.length != expected.length) return false;
            for (int i = 0; i < actual.length; i++) {
                if (actual[i] != expected[i]) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}