import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * User Entity - Represents a Tourist/Normal User.
 *
 * DB table (expected):
 * users(userid, username, useremail, userpassword, userphone, usernid, passportno,
 * dateofbirth, country, address, registrationdate)
 */
public class User {

    private String userID;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userNID;
    private String passportNo;
    private String dateOfBirth; // YYYY-MM-DD
    private String userPassword; // hashed in DB via AuthService (recommended)
    private String country;
    private String address;
    private String registrationDate; // optional read-only
    private String userType = "TOURIST"; // runtime only

    public User() {}

    public User(String userID, String userName, String userEmail, String userType) {
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userType = (userType == null || userType.isBlank()) ? "TOURIST" : userType.trim().toUpperCase();
    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserNID() { return userNID; }
    public void setUserNID(String userNID) { this.userNID = userNID; }

    public String getPassportNo() { return passportNo; }
    public void setPassportNo(String passportNo) { this.passportNo = passportNo; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) {
        this.userType = (userType == null || userType.isBlank()) ? "TOURIST" : userType.trim().toUpperCase();
    }

    /**
     * Optional direct registration (kept for compatibility).
     * In your app, registration is primarily done in AuthService (recommended).
     */
    public boolean registerUser(Connection conn) {
        if (conn == null) return false;

        try {
            if (userID == null || userID.trim().isEmpty()) {
                userID = IdGenerator.uniqueNumericId(conn, "users", "userid", 12, 60);
            }

            String sql =
                    "INSERT INTO users " +
                    "(userid, username, useremail, userpassword, userphone, usernid, passportno, dateofbirth, country, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, trimToNull(userID));
                ps.setString(2, trimToNull(userName));
                ps.setString(3, trimToNull(userEmail));
                ps.setString(4, userPassword);
                ps.setString(5, trimToNull(userPhone));
                ps.setString(6, trimToNull(userNID));
                ps.setString(7, trimToNull(passportNo));

                String dob = trimToNull(dateOfBirth);
                if (dob == null) ps.setNull(8, Types.DATE);
                else ps.setDate(8, Date.valueOf(dob));

                ps.setString(9, trimToNull(country));
                ps.setString(10, trimToNull(address));
                return ps.executeUpdate() > 0;
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format (use YYYY-MM-DD).");
            return false;
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public static User getUserByEmail(String email, Connection conn) {
        if (conn == null) return null;
        if (email == null || email.trim().isEmpty()) return null;

        String sql = "SELECT userid, username, useremail FROM users WHERE useremail = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(
                        rs.getString("userid"),
                        rs.getString("username"),
                        rs.getString("useremail"),
                        "TOURIST"
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
            return null;
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @Override
    public String toString() {
        return "User{userID='" + userID + "', userName='" + userName + "', userEmail='" + userEmail +
                "', userType='" + userType + "'}";
    }
}