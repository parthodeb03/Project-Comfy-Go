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

    private String bookingId;
    private String userId;
    private String checkInDate;   // YYYY-MM-DD
    private String checkOutDate;  // YYYY-MM-DD
    private double totalPrice;
    private String bookingStatus = STATUS_PENDING;
    private String paymentId;

    private String hotelName;
    private String hotelLocation;

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
        if (isBlank(userId)) { System.out.println("Booking create failed: userid is required."); return false; }
        if (totalPrice <= 0) { System.out.println("Booking create failed: totalprice must be > 0."); return false; }

        if (isBlank(bookingStatus)) bookingStatus = STATUS_PENDING;
        bookingStatus = bookingStatus.trim().toUpperCase();

        boolean isHotelBooking = !isBlank(hotelName) && !isBlank(hotelLocation);
        if (isHotelBooking) {
            if (isBlank(checkInDate) || isBlank(checkOutDate)) {
                System.out.println("Booking create failed: check-in and check-out dates are required for hotel booking.");
                return false;
            }
            try {
                Date ci = Date.valueOf(checkInDate.trim());
                Date co = Date.valueOf(checkOutDate.trim());
                if (!co.after(ci)) {
                    System.out.println("Booking create failed: check-out date must be after check-in date.");
                    return false;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format (use YYYY-MM-DD).");
                return false;
            }
        }

        try {
            if (isBlank(bookingId)) {
                bookingId = IdGenerator.uniqueNumericId(conn, "booking", "bookingid", 12, 60);
            }

            String sql =
                    "INSERT INTO booking " +
                    "(bookingid, userid, checkindate, checkoutdate, totalprice, bookingstatus, paymentid, " +
                    "hotelname, hotellocation, guidename, guideid, numberofrooms) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, bookingId);
                ps.setString(2, userId.trim());

                if (isBlank(checkInDate)) ps.setNull(3, java.sql.Types.DATE);
                else ps.setDate(3, Date.valueOf(checkInDate.trim()));

                if (isBlank(checkOutDate)) ps.setNull(4, java.sql.Types.DATE);
                else ps.setDate(4, Date.valueOf(checkOutDate.trim()));

                ps.setDouble(5, totalPrice);
                ps.setString(6, bookingStatus);

                if (isBlank(paymentId)) ps.setNull(7, java.sql.Types.VARCHAR);
                else ps.setString(7, paymentId.trim());

                if (isBlank(hotelName)) ps.setNull(8, java.sql.Types.VARCHAR);
                else ps.setString(8, hotelName.trim());

                if (isBlank(hotelLocation)) ps.setNull(9, java.sql.Types.VARCHAR);
                else ps.setString(9, hotelLocation.trim());

                if (isBlank(guideName)) ps.setNull(10, java.sql.Types.VARCHAR);
                else ps.setString(10, guideName.trim());

                if (isBlank(guideId)) ps.setNull(11, java.sql.Types.VARCHAR);
                else ps.setString(11, guideId.trim());

                ps.setInt(12, Math.max(0, numberOfRooms));
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Booking create failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBookingStatus(Connection conn, String newStatus) {
        if (conn == null) return false;
        if (isBlank(bookingId)) return false;
        if (isBlank(newStatus)) return false;

        String status = newStatus.trim().toUpperCase();
        if (!isAllowedStatus(status)) {
            System.out.println("Invalid booking status: " + status);
            return false;
        }

        String sql = "UPDATE booking SET bookingstatus = ? WHERE bookingid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, bookingId.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) bookingStatus = status;
            return ok;
        } catch (SQLException e) {
            System.out.println("Booking status update failed: " + e.getMessage());
            return false;
        }
    }

    public static List<Booking> getBookingsByUser(String userId, Connection conn) {
        List<Booking> list = new ArrayList<>();
        if (conn == null) return list;
        if (isBlank(userId)) return list;

        String sql =
                "SELECT bookingid, userid, checkindate, checkoutdate, totalprice, bookingstatus, paymentid, " +
                "hotelname, hotellocation, guidename, guideid, numberofrooms " +
                "FROM booking WHERE userid = ? ORDER BY bookingdate DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.out.println("Fetch bookings failed: " + e.getMessage());
        }

        return list;
    }

    public static Booking getBookingById(String bookingId, Connection conn) {
        if (conn == null) return null;
        if (isBlank(bookingId)) return null;

        String sql =
                "SELECT bookingid, userid, checkindate, checkoutdate, totalprice, bookingstatus, paymentid, " +
                "hotelname, hotellocation, guidename, guideid, numberofrooms " +
                "FROM booking WHERE bookingid = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.out.println("Fetch booking failed: " + e.getMessage());
        }

        return null;
    }

    private static Booking map(ResultSet rs) throws SQLException {
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
        return b;
    }

    private static boolean isAllowedStatus(String s) {
        return STATUS_PENDING.equals(s) ||
               STATUS_CONFIRMED.equals(s) ||
               STATUS_COMPLETED.equals(s) ||
               STATUS_FAILED.equals(s) ||
               STATUS_CANCELLED.equals(s);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}