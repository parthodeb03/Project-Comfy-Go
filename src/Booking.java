import java.sql.*;
import java.util.UUID;

/**
 * Booking Entity - Represents a Hotel/Guide Booking
 * Handles booking creation, management, and status tracking
 */
public class Booking {
    private String bookingId;
    private String userId;
    private String bookingDate;
    private String checkInDate;
    private String checkOutDate;
    private double totalPrice;
    private String bookingStatus;
    private String paymentId;
    private String hotelName;
    private String hotelLocation;
    private String guideName;
    private int numberOfRooms;
    private String notes;
    
    // Constructor
    public Booking() {
        this.bookingId = UUID.randomUUID().toString().substring(0, 12);
    }
    
    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    
    public String getHotelLocation() { return hotelLocation; }
    public void setHotelLocation(String hotelLocation) { this.hotelLocation = hotelLocation; }
    
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    
    public int getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(int numberOfRooms) { this.numberOfRooms = numberOfRooms; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Booking CRUD Operations
    public boolean createBooking(Connection conn) {
        String sql = "INSERT INTO booking (booking_id, userId, checkInDate, checkOutDate, total_price, booking_status, payment_id, hotel_name, hotel_location, guide_name, numberOfRooms, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.bookingId);
            pstmt.setString(2, this.userId);
            pstmt.setString(3, this.checkInDate);
            pstmt.setString(4, this.checkOutDate);
            pstmt.setDouble(5, this.totalPrice);
            pstmt.setString(6, this.bookingStatus);
            pstmt.setString(7, this.paymentId);
            pstmt.setString(8, this.hotelName);
            pstmt.setString(9, this.hotelLocation);
            pstmt.setString(10, this.guideName);
            pstmt.setInt(11, this.numberOfRooms);
            pstmt.setString(12, this.notes);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating booking: " + e.getMessage());
            return false;
        }
    }
    
    public static Booking getBookingById(String bookingId, Connection conn) {
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        Booking booking = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    booking = new Booking();
                    booking.setBookingId(rs.getString("booking_id"));
                    booking.setUserId(rs.getString("userId"));
                    booking.setCheckInDate(rs.getString("checkInDate"));
                    booking.setCheckOutDate(rs.getString("checkOutDate"));
                    booking.setTotalPrice(rs.getDouble("total_price"));
                    booking.setBookingStatus(rs.getString("booking_status"));
                    booking.setPaymentId(rs.getString("payment_id"));
                    booking.setHotelName(rs.getString("hotel_name"));
                    booking.setHotelLocation(rs.getString("hotel_location"));
                    booking.setGuideName(rs.getString("guide_name"));
                    booking.setNumberOfRooms(rs.getInt("numberOfRooms"));
                    booking.setNotes(rs.getString("notes"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching booking: " + e.getMessage());
        }
        
        return booking;
    }
    
    public boolean updateBookingStatus(Connection conn, String newStatus) {
        String sql = "UPDATE booking SET booking_status = ? WHERE booking_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, this.bookingId);
            
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                this.bookingStatus = newStatus;
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Error updating booking status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cancelBooking(Connection conn) {
        return updateBookingStatus(conn, "CANCELLED");
    }
    
    public boolean confirmBooking(Connection conn) {
        return updateBookingStatus(conn, "CONFIRMED");
    }
    
    public boolean updateBooking(Connection conn) {
        String sql = "UPDATE booking SET notes = ? WHERE booking_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.notes);
            pstmt.setString(2, this.bookingId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating booking: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteBooking(Connection conn) {
        String sql = "DELETE FROM booking WHERE booking_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", totalPrice=" + totalPrice +
                ", hotelName='" + hotelName + '\'' +
                ", checkInDate='" + checkInDate + '\'' +
                ", checkOutDate='" + checkOutDate + '\'' +
                '}';
    }
}