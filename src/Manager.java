import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Manager Entity - Represents a Hotel Manager.
 */
public class Manager {

    private String managerId;
    private String managerName;
    private String managerEmail;
    private String managerPhone;
    private String managerNid;
    private String managerPassword;
    private String hotelName;
    private String hotelNID;
    private String registrationNumber;
    private String registrationDate;
    private String status = "ACTIVE";

    public Manager() {}

    public Manager(String managerId, String managerName, String managerEmail) {
        this.managerId = managerId;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
    }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }

    public String getManagerPhone() { return managerPhone; }
    public void setManagerPhone(String managerPhone) { this.managerPhone = managerPhone; }

    public String getManagerNid() { return managerNid; }
    public void setManagerNid(String managerNid) { this.managerNid = managerNid; }

    public String getManagerPassword() { return managerPassword; }
    public void setManagerPassword(String managerPassword) { this.managerPassword = managerPassword; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getHotelNID() { return hotelNID; }
    public void setHotelNID(String hotelNID) { this.hotelNID = hotelNID; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // -------------------- Optional DB helpers --------------------

    public boolean registerManager(Connection conn) {
        if (conn == null) return false;

        try {
            if (managerId == null || managerId.trim().isEmpty()) {
                managerId = IdGenerator.uniqueNumericId(conn, "managers", "managerid", 12, 60);
            }

            String sql =
                    "INSERT INTO managers " +
                    "(managerid, managername, manageremail, managerphone, managernid, managerpassword, " +
                    " hotelname, hotelnid, registrationnumber, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, managerId);
                ps.setString(2, managerName);
                ps.setString(3, managerEmail);
                ps.setString(4, managerPhone);
                ps.setString(5, managerNid);
                ps.setString(6, managerPassword);
                ps.setString(7, hotelName);
                ps.setString(8, hotelNID);
                ps.setString(9, registrationNumber);
                ps.setString(10, status);
                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.out.println("Error registering manager: " + e.getMessage());
            return false;
        }
    }

    public static Manager getManagerByEmail(String email, Connection conn) {
        if (conn == null) return null;
        if (email == null || email.trim().isEmpty()) return null;

        String sql =
                "SELECT managerid, managername, manageremail, managerphone, managernid, hotelname, status " +
                "FROM managers WHERE manageremail = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Manager m = new Manager();
                m.setManagerId(rs.getString("managerid"));
                m.setManagerName(rs.getString("managername"));
                m.setManagerEmail(rs.getString("manageremail"));
                m.setManagerPhone(rs.getString("managerphone"));
                m.setManagerNid(rs.getString("managernid"));
                m.setHotelName(rs.getString("hotelname"));
                m.setStatus(rs.getString("status"));
                return m;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching manager: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Manager{" +
                "managerId='" + managerId + '\'' +
                ", managerName='" + managerName + '\'' +
                ", managerEmail='" + managerEmail + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
