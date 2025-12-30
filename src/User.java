import java.sql.*;
import java.util.UUID;

/**
 * User Entity - Represents a Tourist/Normal User
 * Handles user registration, login, and profile management
 */
public class User {
    private String userID;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userNID;
    private String passportNo;
    private String dateOfBirth;
    private String userPassword;
    private String country;
    private String address;
    private String registrationDate;
    
    // Constructor
    public User() {
        this.userID = UUID.randomUUID().toString().substring(0, 12);
    }
    
    // Getters and Setters
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
    
    // User CRUD Operations
    public boolean registerUser(Connection conn) {
        String sql = "INSERT INTO users (userID, userName, userEmail, userPhone, userNID, passportNo, dateOfBirth, userPassword, country, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.userID);
            pstmt.setString(2, this.userName);
            pstmt.setString(3, this.userEmail);
            pstmt.setString(4, this.userPhone);
            pstmt.setString(5, this.userNID);
            pstmt.setString(6, this.passportNo);
            pstmt.setString(7, this.dateOfBirth);
            pstmt.setString(8, this.userPassword);
            pstmt.setString(9, this.country);
            pstmt.setString(10, this.address);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    public static User getUserByEmail(String email, Connection conn) {
        String sql = "SELECT * FROM users WHERE userEmail = ?";
        User user = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserID(rs.getString("userID"));
                    user.setUserName(rs.getString("userName"));
                    user.setUserEmail(rs.getString("userEmail"));
                    user.setUserPhone(rs.getString("userPhone"));
                    user.setUserNID(rs.getString("userNID"));
                    user.setPassportNo(rs.getString("passportNo"));
                    user.setDateOfBirth(rs.getString("dateOfBirth"));
                    user.setCountry(rs.getString("country"));
                    user.setAddress(rs.getString("address"));
                    user.setRegistrationDate(rs.getString("registrationDate"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        
        return user;
    }
    
    public boolean updateUser(Connection conn) {
        String sql = "UPDATE users SET userName = ?, userPhone = ?, address = ? WHERE userID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.userName);
            pstmt.setString(2, this.userPhone);
            pstmt.setString(3, this.address);
            pstmt.setString(4, this.userID);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteUser(Connection conn) {
        String sql = "DELETE FROM users WHERE userID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.userID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", country='" + country + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}