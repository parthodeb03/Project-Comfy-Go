import java.sql.*;
import java.util.UUID;

/**
 * TouristSpot Entity - Represents a Tourist Spot/Attraction
 * Handles tourist spot information and management
 */
public class TouristSpot {
    private String spotId;
    private String spotName;
    private String division;
    private String district;
    private String spotAddress;
    private String description;
    private double entryFee;
    private double rating;
    private int totalVisitors;
    private String bestSeason;
    private String visitingHours;
    
    // Constructor
    public TouristSpot() {
        this.spotId = UUID.randomUUID().toString().substring(0, 12);
    }
    
    // Getters and Setters
    public String getSpotId() { return spotId; }
    public void setSpotId(String spotId) { this.spotId = spotId; }
    
    public String getSpotName() { return spotName; }
    public void setSpotName(String spotName) { this.spotName = spotName; }
    
    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }
    
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    
    public String getSpotAddress() { return spotAddress; }
    public void setSpotAddress(String spotAddress) { this.spotAddress = spotAddress; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getEntryFee() { return entryFee; }
    public void setEntryFee(double entryFee) { this.entryFee = entryFee; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public int getTotalVisitors() { return totalVisitors; }
    public void setTotalVisitors(int totalVisitors) { this.totalVisitors = totalVisitors; }
    
    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }
    
    public String getVisitingHours() { return visitingHours; }
    public void setVisitingHours(String visitingHours) { this.visitingHours = visitingHours; }
    
    // CRUD Operations
    public boolean createSpot(Connection conn) {
        String sql = "INSERT INTO tourist_spots (spotId, spotName, division, district, spotAddress, description, entryFee, rating, bestSeason, visitingHours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.spotId);
            pstmt.setString(2, this.spotName);
            pstmt.setString(3, this.division);
            pstmt.setString(4, this.district);
            pstmt.setString(5, this.spotAddress);
            pstmt.setString(6, this.description);
            pstmt.setDouble(7, this.entryFee);
            pstmt.setDouble(8, this.rating);
            pstmt.setString(9, this.bestSeason);
            pstmt.setString(10, this.visitingHours);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating spot: " + e.getMessage());
            return false;
        }
    }
    
    public static TouristSpot getSpotById(String spotId, Connection conn) {
        String sql = "SELECT * FROM tourist_spots WHERE spotId = ?";
        TouristSpot spot = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    spot = new TouristSpot();
                    spot.setSpotId(rs.getString("spotId"));
                    spot.setSpotName(rs.getString("spotName"));
                    spot.setDivision(rs.getString("division"));
                    spot.setDistrict(rs.getString("district"));
                    spot.setSpotAddress(rs.getString("spotAddress"));
                    spot.setDescription(rs.getString("description"));
                    spot.setEntryFee(rs.getDouble("entryFee"));
                    spot.setRating(rs.getDouble("rating"));
                    spot.setTotalVisitors(rs.getInt("totalVisitors"));
                    spot.setBestSeason(rs.getString("bestSeason"));
                    spot.setVisitingHours(rs.getString("visitingHours"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching spot: " + e.getMessage());
        }
        
        return spot;
    }
    
    public boolean updateSpot(Connection conn) {
        String sql = "UPDATE tourist_spots SET description = ?, entryFee = ?, rating = ?, bestSeason = ?, visitingHours = ? WHERE spotId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.description);
            pstmt.setDouble(2, this.entryFee);
            pstmt.setDouble(3, this.rating);
            pstmt.setString(4, this.bestSeason);
            pstmt.setString(5, this.visitingHours);
            pstmt.setString(6, this.spotId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating spot: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteSpot(Connection conn) {
        String sql = "DELETE FROM tourist_spots WHERE spotId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.spotId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting spot: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateRating(Connection conn, double newRating) {
        String sql = "UPDATE tourist_spots SET rating = ? WHERE spotId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newRating);
            pstmt.setString(2, this.spotId);
            
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
        return "TouristSpot{" +
                "spotId='" + spotId + '\'' +
                ", spotName='" + spotName + '\'' +
                ", division='" + division + '\'' +
                ", district='" + district + '\'' +
                ", rating=" + rating +
                ", entryFee=" + entryFee +
                '}';
    }
}