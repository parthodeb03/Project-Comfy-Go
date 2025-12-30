import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Booking {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUSCANCELLED = null;
    public static final String STATUSCONFIRMED = null;

    private String bookingId;
    private String userId;
    private String checkInDate;   // YYYY-MM-DD
    private String checkOutDate;  // YYYY-MM-DD
    private double totalPrice;
    private String bookingStatus = STATUS_PENDING;
    private String paymentId;

    private String hotelName;
    private String hotelLocation;

    // kept for future/optional use
    private String guideName;
    private String guideId;

    private int numberOfRooms;

    public Booking() {}

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

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

    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }

    public int getNumberOfRooms() { return numberOfRooms; }
    public void setNumberOfRooms(int numberOfRooms) { this.numberOfRooms = numberOfRooms; }

    public boolean createBooking(Connection conn) {
        if (conn == null) return false;

        try {
            if (bookingId == null || bookingId.trim().isEmpty()) {
                bookingId = IdGenerator.uniqueNumericId(conn, "booking", "bookingid", 12, 60);
            }
            if (bookingStatus == null || bookingStatus.trim().isEmpty()) bookingStatus = STATUS_PENDING;

            String sql =
                    "INSERT INTO booking " +
                    "(bookingid, userid, checkindate, checkoutdate, totalprice, bookingstatus, paymentid, " +
                    "hotelname, hotellocation, guidename, guideid, numberofrooms) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, bookingId);
                ps.setString(2, userId);
                ps.setDate(3, Date.valueOf(checkInDate));
                ps.setDate(4, Date.valueOf(checkOutDate));
                ps.setDouble(5, totalPrice);
                ps.setString(6, bookingStatus);
                ps.setString(7, paymentId);
                ps.setString(8, hotelName);
                ps.setString(9, hotelLocation);
                ps.setString(10, guideName);
                ps.setString(11, guideId);
                ps.setInt(12, numberOfRooms);

                return ps.executeUpdate() > 0;
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format (use YYYY-MM-DD).");
            return false;
        } catch (SQLException e) {
            System.out.println("Booking create failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBookingStatus(Connection conn, String newStatus) {
        if (conn == null) return false;
        if (bookingId == null || bookingId.trim().isEmpty()) return false;
        if (newStatus == null || newStatus.trim().isEmpty()) return false;

        String sql = "UPDATE booking SET bookingstatus = ? WHERE bookingid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.trim());
            ps.setString(2, bookingId);

            boolean ok = ps.executeUpdate() > 0;
            if (ok) bookingStatus = newStatus.trim();
            return ok;

        } catch (SQLException e) {
            System.out.println("Booking status update failed: " + e.getMessage());
            return false;
        }
    }

    public static List<Booking> getBookingsByUser(String userId, Connection conn) {
        List<Booking> list = new ArrayList<>();
        if (conn == null) return list;
        if (userId == null || userId.trim().isEmpty()) return list;

        String sql =
                "SELECT bookingid, userid, checkindate, checkoutdate, totalprice, bookingstatus, paymentid, " +
                "hotelname, hotellocation, guidename, guideid, numberofrooms " +
                "FROM booking WHERE userid = ? ORDER BY bookingdate DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.bookingId = rs.getString("bookingid");
                    b.userId = rs.getString("userid");

                    Date ci = rs.getDate("checkindate");
                    Date co = rs.getDate("checkoutdate");
                    b.checkInDate = (ci == null) ? null : ci.toString();
                    b.checkOutDate = (co == null) ? null : co.toString();

                    b.totalPrice = rs.getDouble("totalprice");
                    b.bookingStatus = rs.getString("bookingstatus");
                    b.paymentId = rs.getString("paymentid");
                    b.hotelName = rs.getString("hotelname");
                    b.hotelLocation = rs.getString("hotellocation");
                    b.guideName = rs.getString("guidename");
                    b.guideId = rs.getString("guideid");
                    b.numberOfRooms = rs.getInt("numberofrooms");

                    list.add(b);
                }
            }

        } catch (SQLException e) {
            System.out.println("Fetch bookings failed: " + e.getMessage());
        }

        return list;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", userId='" + userId + '\'' +
                ", hotelName='" + hotelName + '\'' +
                ", checkInDate='" + checkInDate + '\'' +
                ", checkOutDate='" + checkOutDate + '\'' +
                ", totalPrice=" + totalPrice +
                ", bookingStatus='" + bookingStatus + '\'' +
                '}';
    }
}
