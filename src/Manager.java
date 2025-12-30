import java.sql.*;
import java.util.UUID;

/**
 * Manager Entity - Represents a Hotel Manager
 * Handles manager registration, authentication, and profile management
 */
public class Manager {
    private String managerId;
    private String managerName;
    private String managerEmail;
    private String managerPhone;
    private String managerPassword;
    private String hotelName;
    private String hotelNID;
    private String registrationNumber;
    private String registrationDate;
    private String status;
    
    // Constructor
    public Manager() {
        this.managerId = UUID.randomUUID().toString().substring(0, 12);
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    
    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
    
    public String getManagerPhone() { return managerPhone; }
    public void setManagerPhone(String managerPhone) { this.managerPhone = managerPhone; }
    
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
    
    // Manager CRUD Operations
    public boolean registerManager(Connection conn) {
        String sql = "INSERT INTO managers (managerId, managerName, managerEmail, managerPhone, managerPassword, hotelName, hotelNID, registrationNumber, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.managerId);
            pstmt.setString(2, this.managerName);
            pstmt.setString(3, this.managerEmail);
            pstmt.setString(4, this.managerPhone);
            pstmt.setString(5, this.managerPassword);
            pstmt.setString(6, this.hotelName);
            pstmt.setString(7, this.hotelNID);
            pstmt.setString(8, this.registrationNumber);
            pstmt.setString(9, this.status);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error registering manager: " + e.getMessage());
            return false;
        }
    }
    
    public static Manager getManagerByEmail(String email, Connection conn) {
        String sql = "SELECT * FROM managers WHERE managerEmail = ?";
        Manager manager = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    manager = new Manager();
                    manager.setManagerId(rs.getString("managerId"));
                    manager.setManagerName(rs.getString("managerName"));
                    manager.setManagerEmail(rs.getString("managerEmail"));
                    manager.setManagerPhone(rs.getString("managerPhone"));
                    manager.setHotelName(rs.getString("hotelName"));
                    manager.setRegistrationDate(rs.getString("registrationDate"));
                    manager.setStatus(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching manager: " + e.getMessage());
        }
        
        return manager;
    }
    
    public boolean updateManager(Connection conn) {
        String sql = "UPDATE managers SET managerName = ?, managerPhone = ?, hotelName = ? WHERE managerId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.managerName);
            pstmt.setString(2, this.managerPhone);
            pstmt.setString(3, this.hotelName);
            pstmt.setString(4, this.managerId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating manager: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteManager(Connection conn) {
        String sql = "DELETE FROM managers WHERE managerId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.managerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting manager: " + e.getMessage());
            return false;
        }
    }
    
    public boolean changeStatus(Connection conn, String newStatus) {
        String sql = "UPDATE managers SET status = ? WHERE managerId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, this.managerId);
            
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                this.status = newStatus;
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Error changing status: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Manager{" +
                "managerId='" + managerId + '\'' +
                ", managerName='" + managerName + '\'' +
                ", managerEmail='" + managerEmail + '\'' +
                ", managerPhone='" + managerPhone + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}