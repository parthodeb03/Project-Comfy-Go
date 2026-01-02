import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TouristSpotService {

    private final Connection conn;

    public TouristSpotService(Connection conn) {
        this.conn = conn;
    }

    public List<TouristSpot> getAllSpots() {
        List<TouristSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM touristspots ORDER BY rating DESC LIMIT 50";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                spots.add(mapSpot(rs));
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch spots: " + e.getMessage());
        }

        return spots;
    }

    public List<TouristSpot> searchSpotsByDivision(String division) {
        List<TouristSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM touristspots WHERE division = ? ORDER BY rating DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, division == null ? "" : division.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) spots.add(mapSpot(rs));
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }

        return spots;
    }

    public List<TouristSpot> searchSpotsByDistrict(String district) {
        List<TouristSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM touristspots WHERE district = ? ORDER BY rating DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, district == null ? "" : district.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) spots.add(mapSpot(rs));
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }

        return spots;
    }

    public TouristSpot getSpotById(String spotId) {
        if (spotId == null || spotId.trim().isEmpty()) return null;

        String sql = "SELECT * FROM touristspots WHERE spotid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spotId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSpot(rs);
            }
        } catch (SQLException e) {
            System.out.println("Spot fetch failed: " + e.getMessage());
        }

        return null;
    }

    public void displaySpotDetails(TouristSpot spot) {
        if (spot == null) {
            System.out.println("Spot not found!");
            return;
        }
        System.out.println("=".repeat(70));
        System.out.println("TOURIST SPOT DETAILS");
        System.out.println("=".repeat(70));
        System.out.println("Name: " + spot.getSpotName());
        System.out.println("Location: " + spot.getDistrict() + ", " + spot.getDivision());
        System.out.println("Address: " + spot.getSpotAddress());
        System.out.println("Description: " + spot.getDescription());
        System.out.println("Entry Fee: BDT " + spot.getEntryFee());
        System.out.println("Rating: " + spot.getRating() + " / 5");
        System.out.println("Total Visitors: " + spot.getTotalVisitors());
        System.out.println("Best Season: " + spot.getBestSeason());
        System.out.println("Visiting Hours: " + spot.getVisitingHours());
        System.out.println("=".repeat(70));
    }

    private TouristSpot mapSpot(ResultSet rs) throws SQLException {
        TouristSpot s = new TouristSpot();
        s.setSpotId(rs.getString("spotid"));
        s.setSpotName(rs.getString("spotname"));
        s.setDivision(rs.getString("division"));
        s.setDistrict(rs.getString("district"));
        s.setSpotAddress(rs.getString("spotaddress"));
        s.setDescription(rs.getString("description"));
        s.setEntryFee(rs.getDouble("entryfee"));
        s.setRating(rs.getDouble("rating"));
        s.setTotalVisitors(rs.getInt("totalvisitors"));
        s.setBestSeason(rs.getString("bestseason"));
        s.setVisitingHours(rs.getString("visitinghours"));
        return s;
    }
}