import java.sql.*;
import java.util.UUID;

/**
 * Hotel Entity - Represents a Hotel
 * Handles hotel management, room availability, pricing, and amenities
 */
public class Hotel {
    private String hotelId;
    private String hotelName;
    private String hotelLocation;
    private double pricePerNight;
    private double rating;
    private int roomAvailability;
    private String roomCategory;
    private int totalRooms;
    private String features;
    private String managerId;
    
    // Constructor
    public Hotel() {
        this.hotelId = UUID.randomUUID().toString().substring(0, 12);
    }
    
    // Getters and Setters
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }
    
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    
    public String getHotelLocation() { return hotelLocation; }
    public void setHotelLocation(String hotelLocation) { this.hotelLocation = hotelLocation; }
    
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    
    public int getRoomAvailability() { return roomAvailability; }
    public void setRoomAvailability(int roomAvailability) { this.roomAvailability = roomAvailability; }
    
    public String getRoomCategory() { return roomCategory; }
    public void setRoomCategory(String roomCategory) { this.roomCategory = roomCategory; }
    
    public int getTotalRooms() { return totalRooms; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    
    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    
    // Hotel CRUD Operations
    public boolean createHotel(Connection conn) {
        String sql = "INSERT INTO hotels (hotel_Id, hotel_Name, hotel_Location, hotel_Price_per_Night, hotel_rating, room_availability, room_category, totalRooms, hotelFeatures, managerId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.hotelId);
            pstmt.setString(2, this.hotelName);
            pstmt.setString(3, this.hotelLocation);
            pstmt.setDouble(4, this.pricePerNight);
            pstmt.setDouble(5, this.rating);
            pstmt.setInt(6, this.roomAvailability);
            pstmt.setString(7, this.roomCategory);
            pstmt.setInt(8, this.totalRooms);
            pstmt.setString(9, this.features);
            pstmt.setString(10, this.managerId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating hotel: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateHotel(Connection conn) {
        String sql = "UPDATE hotels SET hotel_Price_per_Night = ?, hotel_rating = ?, room_availability = ?, hotelFeatures = ? WHERE hotel_Id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, this.pricePerNight);
            pstmt.setDouble(2, this.rating);
            pstmt.setInt(3, this.roomAvailability);
            pstmt.setString(4, this.features);
            pstmt.setString(5, this.hotelId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating hotel: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteHotel(Connection conn) {
        String sql = "DELETE FROM hotels WHERE hotel_Id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.hotelId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting hotel: " + e.getMessage());
            return false;
        }
    }
    
    public static Hotel getHotelById(String hotelId, Connection conn) {
        String sql = "SELECT * FROM hotels WHERE hotel_Id = ?";
        Hotel hotel = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hotelId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    hotel = new Hotel();
                    hotel.setHotelId(rs.getString("hotel_Id"));
                    hotel.setHotelName(rs.getString("hotel_Name"));
                    hotel.setHotelLocation(rs.getString("hotel_Location"));
                    hotel.setPricePerNight(rs.getDouble("hotel_Price_per_Night"));
                    hotel.setRating(rs.getDouble("hotel_rating"));
                    hotel.setRoomAvailability(rs.getInt("room_availability"));
                    hotel.setRoomCategory(rs.getString("room_category"));
                    hotel.setTotalRooms(rs.getInt("totalRooms"));
                    hotel.setFeatures(rs.getString("hotelFeatures"));
                    hotel.setManagerId(rs.getString("managerId"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hotel: " + e.getMessage());
        }
        
        return hotel;
    }
    
    public boolean updateRoomAvailability(Connection conn, int newAvailability) {
        String sql = "UPDATE hotels SET room_availability = ? WHERE hotel_Id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newAvailability);
            pstmt.setString(2, this.hotelId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating room availability: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updatePrice(Connection conn, double newPrice) {
        String sql = "UPDATE hotels SET hotel_Price_per_Night = ? WHERE hotel_Id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setString(2, this.hotelId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating price: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId='" + hotelId + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", hotelLocation='" + hotelLocation + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", rating=" + rating +
                ", roomAvailability=" + roomAvailability +
                ", totalRooms=" + totalRooms +
                '}';
    }
}