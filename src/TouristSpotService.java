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

            while (rs.next()) spots.add(mapSpot(rs));

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
        String sql = "SELECT * FROM touristspots WHERE spotid = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spotId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapSpot(rs);
            }

        } catch (SQLException e) {
            System.out.println("Spot fetch failed: " + e.getMessage());
            return null;
        }
    }

    public void displaySpotDetails(TouristSpot spot) {
        if (spot == null) {
            System.out.println("Spot not found!");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("TOURIST SPOT DETAILS");
        System.out.println("=".repeat(70));
        System.out.println("Name: " + spot.getSpotName());
        System.out.println("Location: " + spot.getDistrict() + ", " + spot.getDivision());
        System.out.println("Address: " + spot.getSpotAddress());
        System.out.println("Description: " + spot.getDescription());
        System.out.println("Entry Fee: BDT " + spot.getEntryFee());
        System.out.println("Rating: " + spot.getRating() + "/5");
        System.out.println("Total Visitors: " + spot.getTotalVisitors());
        System.out.println("Best Season: " + spot.getBestSeason());
        System.out.println("Visiting Hours: " + spot.getVisitingHours());
        System.out.println("=".repeat(70));
    }

    public List<String> getAllDivisions() {
        List<String> divisions = new ArrayList<>();
        String sql = "SELECT DISTINCT division FROM touristspots ORDER BY division";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) divisions.add(rs.getString("division"));

        } catch (SQLException e) {
            System.out.println("Failed to fetch divisions: " + e.getMessage());
        }

        return divisions;
    }

    public void listSpotsByDivision(String division) {
        List<TouristSpot> spots = searchSpotsByDivision(division);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TOURIST SPOTS - " + (division == null ? "" : division.toUpperCase()));
        System.out.println("=".repeat(80));

        if (spots.isEmpty()) {
            System.out.println("No spots found!");
            System.out.println("=".repeat(80));
            return;
        }

        for (int i = 0; i < spots.size(); i++) {
            TouristSpot s = spots.get(i);
            System.out.println((i + 1) + ". " + s.getSpotName() + " | " + s.getDistrict() + " | Rating: " + s.getRating());
        }

        System.out.println("=".repeat(80));
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