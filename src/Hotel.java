import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Hotel Entity - matches `hotels` table columns used by HotelService.
 *
 * Expected columns:
 * hotels(hotelid, hotelname, hotellocation, hotelpricepernight, hotelrating,
 * roomavailability, roomcategory, totalrooms, hotelfeatures, hoteldescription, managerid, ...)
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

    // NEW
    private String description;

    private String managerId;

    public Hotel() {}

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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    // -------------------- Optional DB helpers --------------------
    public static Hotel getHotelById(String hotelId, Connection conn) {
        if (conn == null) return null;
        if (hotelId == null || hotelId.trim().isEmpty()) return null;

        String sql = "SELECT * FROM hotels WHERE hotelid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Hotel h = new Hotel();
                h.hotelId = rs.getString("hotelid");
                h.hotelName = rs.getString("hotelname");
                h.hotelLocation = rs.getString("hotellocation");
                h.pricePerNight = rs.getDouble("hotelpricepernight");
                h.rating = rs.getDouble("hotelrating");
                h.roomAvailability = rs.getInt("roomavailability");
                h.roomCategory = rs.getString("roomcategory");
                h.totalRooms = rs.getInt("totalrooms");
                h.features = rs.getString("hotelfeatures");
                h.description = rs.getString("hoteldescription");
                h.managerId = rs.getString("managerid");
                return h;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hotel: " + e.getMessage());
            return null;
        }
    }

    public boolean updateRoomAvailability(Connection conn, int newAvailability) {
        if (conn == null) return false;
        if (hotelId == null || hotelId.trim().isEmpty()) return false;

        String sql = "UPDATE hotels SET roomavailability = ? WHERE hotelid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newAvailability);
            ps.setString(2, hotelId);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) this.roomAvailability = newAvailability;
            return ok;
        } catch (SQLException e) {
            System.out.println("Error updating room availability: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePrice(Connection conn, double newPrice) {
        if (conn == null) return false;
        if (hotelId == null || hotelId.trim().isEmpty()) return false;

        String sql = "UPDATE hotels SET hotelpricepernight = ? WHERE hotelid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, hotelId);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) this.pricePerNight = newPrice;
            return ok;
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
                '}';
    }
}
