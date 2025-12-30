import java.sql.*;
import java.util.UUID;

/**
 * Guide Entity - Represents a Tour Guide
 * Handles guide registration, availability, specialization, and ratings
 */
public class Guide {
    private String guideId;
    private String guideName;
    private String guideEmail;
    private String guidePhone;
    private String guidePassword;
    private String guideDivision;
    private String guideDistrict;
    private String guideLanguage;
    private String specialization;
    private double rating;
    private int totalRatings;
    private boolean isAvailable;
    private int yearExperience;
    private String status;
    
    // Constructor
    public Guide() {
        this.guideId = UUID.randomUUID().toString().substring(0, 12);
        this.rating = 5.0;
        this.isAvailable = true;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }
    
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    
    public String getGuideEmail() { return guideEmail; }
    public void setGuideEmail(String guideEmail) { this.guideEmail = guideEmail; }
    
    public String getGuidePhone() { return guidePhone; }
    public void setGuidePhone(String guidePhone) { this.guidePhone = guidePhone; }
    
    public String getGuidePassword() { return guidePassword; }
    public void setGuidePassword(String guidePassword) { this.guidePassword = guidePassword; }
    
    public String getGuideDivision() { return guideDivision; }
    public void setGuideDivision(String guideDivision) { this.guideDivision = guideDivision; }
    
    public String getGuideDistrict() { return guideDistrict; }
    public void setGuideDistrict(String guideDistrict) { this.guideDistrict = guideDistrict; }
    
    public String getGuideLanguage() { return guideLanguage; }
    public void setGuideLanguage(String guideLanguage) { this.guideLanguage = guideLanguage; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public int getYearExperience() { return yearExperience; }
    public void setYearExperience(int yearExperience) { this.yearExperience = yearExperience; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Guide CRUD Operations
    public boolean registerGuide(Connection conn) {
        String sql = "INSERT INTO guides (guideId, guideName, guideEmail, guidePhone, guidePassword, guideDivision, guideDistrict, guideLanguage, specialization, yearExperience, isAvailable, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.guideId);
            pstmt.setString(2, this.guideName);
            pstmt.setString(3, this.guideEmail);
            pstmt.setString(4, this.guidePhone);
            pstmt.setString(5, this.guidePassword);
            pstmt.setString(6, this.guideDivision);
            pstmt.setString(7, this.guideDistrict);
            pstmt.setString(8, this.guideLanguage);
            pstmt.setString(9, this.specialization);
            pstmt.setInt(10, this.yearExperience);
            pstmt.setBoolean(11, this.isAvailable);
            pstmt.setString(12, this.status);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error registering guide: " + e.getMessage());
            return false;
        }
    }
    
    public static Guide getGuideByEmail(String email, Connection conn) {
        String sql = "SELECT * FROM guides WHERE guideEmail = ?";
        Guide guide = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    guide = new Guide();
                    guide.setGuideId(rs.getString("guideId"));
                    guide.setGuideName(rs.getString("guideName"));
                    guide.setGuideEmail(rs.getString("guideEmail"));
                    guide.setGuidePhone(rs.getString("guidePhone"));
                    guide.setGuideDivision(rs.getString("guideDivision"));
                    guide.setGuideDistrict(rs.getString("guideDistrict"));
                    guide.setGuideLanguage(rs.getString("guideLanguage"));
                    guide.setSpecialization(rs.getString("specialization"));
                    guide.setRating(rs.getDouble("rating"));
                    guide.setTotalRatings(rs.getInt("totalRatings"));
                    guide.setAvailable(rs.getBoolean("isAvailable"));
                    guide.setYearExperience(rs.getInt("yearExperience"));
                    guide.setStatus(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching guide: " + e.getMessage());
        }
        
        return guide;
    }
    
    public boolean updateGuide(Connection conn) {
        String sql = "UPDATE guides SET guidePhone = ?, guideLanguage = ?, specialization = ?, yearExperience = ? WHERE guideId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.guidePhone);
            pstmt.setString(2, this.guideLanguage);
            pstmt.setString(3, this.specialization);
            pstmt.setInt(4, this.yearExperience);
            pstmt.setString(5, this.guideId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating guide: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteGuide(Connection conn) {
        String sql = "DELETE FROM guides WHERE guideId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.guideId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting guide: " + e.getMessage());
            return false;
        }
    }
    
    public boolean setAvailability(Connection conn, boolean available) {
        String sql = "UPDATE guides SET isAvailable = ? WHERE guideId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, available);
            pstmt.setString(2, this.guideId);
            
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                this.isAvailable = available;
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Error setting availability: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateRating(Connection conn, double newRating) {
        String sql = "UPDATE guides SET rating = ? WHERE guideId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newRating);
            pstmt.setString(2, this.guideId);
            
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                this.rating = newRating;
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Error updating rating: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Guide{" +
                "guideId='" + guideId + '\'' +
                ", guideName='" + guideName + '\'' +
                ", guideEmail='" + guideEmail + '\'' +
                ", specialization='" + specialization + '\'' +
                ", rating=" + rating +
                ", isAvailable=" + isAvailable +
                ", yearExperience=" + yearExperience +
                '}';
    }
}