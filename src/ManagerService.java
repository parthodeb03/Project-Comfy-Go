import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerService {

    private final Connection conn;

    public ManagerService(Connection conn) {
        this.conn = conn;
    }

    // Backward-compatible: your ComfyGo currently calls this signature
    public boolean addHotel(String managerId, String hotelName, String location, double pricePerNight,
                            int totalRooms, String roomCategory, String features) {
        return addHotel(managerId, hotelName, location, pricePerNight, totalRooms, roomCategory, features, "");
    }

    // New: supports hoteldescription
    public boolean addHotel(String managerId, String hotelName, String location, double pricePerNight,
                            int totalRooms, String roomCategory, String features, String description) {

        if (conn == null) return false;
        if (managerId == null || managerId.trim().isEmpty()) return false;
        if (hotelName == null || hotelName.trim().isEmpty()) return false;

        if (location == null) location = "";
        if (features == null) features = "";
        if (roomCategory == null) roomCategory = "";
        if (description == null) description = "";

        if (pricePerNight <= 0) {
            System.out.println("Price per night must be > 0!");
            return false;
        }

        if (totalRooms < 0) totalRooms = 0;

        try {
            // If you added UNIQUE(hotels.managerid), this prevents duplicate insert errors
            if (hasHotelForManager(managerId)) {
                System.out.println("You already have a hotel added. One manager can add only one hotel.");
                return false;
            }

            String hotelId = IdGenerator.uniqueNumericId(conn, "hotels", "hotelid", 12, 60);

            String sql = "INSERT INTO hotels " +
                    "(hotelid, hotelname, hotellocation, hotelpricepernight, totalrooms, roomcategory, " +
                    " hotelfeatures, hoteldescription, roomavailability, managerid) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, hotelId);
                ps.setString(2, hotelName.trim());
                ps.setString(3, location.trim());
                ps.setDouble(4, pricePerNight);
                ps.setInt(5, totalRooms);
                ps.setString(6, roomCategory.trim());
                ps.setString(7, features.trim());
                ps.setString(8, description.trim());
                ps.setInt(9, totalRooms); // initially all rooms available
                ps.setString(10, managerId.trim());
                ps.executeUpdate();
            }

            System.out.println("Hotel added successfully!");
            System.out.println("Hotel ID: " + hotelId);
            return true;

        } catch (SQLException e) {
            System.out.println("Failed to add hotel: " + e.getMessage());
            return false;
        }
    }

    public Hotel getManagerHotel(String managerId) {
        if (conn == null) return null;
        if (managerId == null || managerId.trim().isEmpty()) return null;

        String sql = "SELECT * FROM hotels WHERE managerid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, managerId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Hotel h = new Hotel();
                h.setHotelId(rs.getString("hotelid"));
                h.setHotelName(rs.getString("hotelname"));
                h.setHotelLocation(rs.getString("hotellocation"));
                h.setPricePerNight(rs.getDouble("hotelpricepernight"));
                h.setRating(rs.getDouble("hotelrating"));
                h.setRoomAvailability(rs.getInt("roomavailability"));
                h.setRoomCategory(rs.getString("roomcategory"));
                h.setTotalRooms(rs.getInt("totalrooms"));
                h.setFeatures(rs.getString("hotelfeatures"));
                h.setDescription(rs.getString("hoteldescription")); // NEW
                h.setManagerId(rs.getString("managerid"));
                return h;
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch hotel: " + e.getMessage());
            return null;
        }
    }

    public List<String> getHotelBookings(String hotelId) {
        List<String> bookings = new ArrayList<>();
        if (conn == null) return bookings;
        if (hotelId == null || hotelId.trim().isEmpty()) return bookings;

        // booking table stores hotelname + hotellocation, so filter using both
        String sql =
                "SELECT bookingid, hotelname, hotellocation, checkindate, checkoutdate, numberofrooms, bookingstatus, totalprice, paymentid " +
                "FROM booking " +
                "WHERE hotelname = (SELECT hotelname FROM hotels WHERE hotelid = ? LIMIT 1) " +
                "AND hotellocation = (SELECT hotellocation FROM hotels WHERE hotelid = ? LIMIT 1) " +
                "ORDER BY checkindate DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId.trim());
            ps.setString(2, hotelId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String row =
                            "[" + rs.getString("bookingid") + "] " +
                            rs.getDate("checkindate") + " to " + rs.getDate("checkoutdate") + " | " +
                            rs.getInt("numberofrooms") + " rooms | " +
                            rs.getString("bookingstatus") + " | BDT " +
                            rs.getDouble("totalprice") + " | PayID: " +
                            rs.getString("paymentid");

                    bookings.add(row);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch bookings: " + e.getMessage());
        }

        return bookings;
    }

    // NOTE: For cancellations, use HotelService.cancelHotelBookingForManager(managerId, bookingId)
    // because it also sets payment -> CANCELLED and restores rooms (your new rules).
    public boolean cancelBookingForManager(String managerId, String bookingId) {
        if (conn == null) return false;
        if (managerId == null || managerId.trim().isEmpty()) return false;
        if (bookingId == null || bookingId.trim().isEmpty()) return false;

        HotelService hs = new HotelService(conn);
        return hs.cancelHotelBookingForManager(managerId.trim(), bookingId.trim());
    }

    public void displayHotelStats(String hotelId) {
        if (conn == null) return;
        if (hotelId == null || hotelId.trim().isEmpty()) return;

        String sql =
                "SELECT " +
                " (SELECT COUNT(*) FROM booking WHERE hotelname = h.hotelname AND hotellocation = h.hotellocation) AS total_bookings, " +
                " (SELECT COUNT(*) FROM booking WHERE hotelname = h.hotelname AND hotellocation = h.hotellocation AND bookingstatus = 'CONFIRMED') AS confirmed_bookings, " +
                " (SELECT AVG(rating) FROM ratings WHERE ratingtype = 'HOTEL' AND targetname = h.hotelname) AS avg_rating, " +
                " h.roomavailability, h.totalrooms " +
                "FROM hotels h WHERE h.hotelid = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;

                System.out.println("\n" + "=".repeat(70));
                System.out.println("HOTEL STATISTICS");
                System.out.println("=".repeat(70));
                System.out.println("Total Bookings: " + rs.getInt("total_bookings"));
                System.out.println("Confirmed Bookings: " + rs.getInt("confirmed_bookings"));
                System.out.println("Average Rating: " + rs.getDouble("avg_rating"));
                System.out.println("Available Rooms: " + rs.getInt("roomavailability") + "/" + rs.getInt("totalrooms"));
                System.out.println("=".repeat(70));
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch statistics: " + e.getMessage());
        }
    }

    public boolean updateHotelFeatures(String hotelId, String newFeatures) {
        if (conn == null) return false;
        if (hotelId == null || hotelId.trim().isEmpty()) return false;
        if (newFeatures == null) newFeatures = "";

        String sql = "UPDATE hotels SET hotelfeatures = ? WHERE hotelid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newFeatures.trim());
            ps.setString(2, hotelId.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Hotel features updated!");
            return ok;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateHotelDescription(String hotelId, String newDescription) {
        if (conn == null) return false;
        if (hotelId == null || hotelId.trim().isEmpty()) return false;
        if (newDescription == null) newDescription = "";

        String sql = "UPDATE hotels SET hoteldescription = ? WHERE hotelid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newDescription.trim());
            ps.setString(2, hotelId.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Hotel description updated!");
            return ok;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    private boolean hasHotelForManager(String managerId) throws SQLException {
        String sql = "SELECT 1 FROM hotels WHERE managerid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, managerId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}