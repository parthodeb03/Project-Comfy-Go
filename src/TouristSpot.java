import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public TouristSpot() {}

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

    public boolean createSpot(Connection conn) {
        if (conn == null) return false;

        try {
            if (spotId == null || spotId.trim().isEmpty()) {
                spotId = IdGenerator.uniqueNumericId(conn, "touristspots", "spotid", 12, 60);
            }

            String sql =
                    "INSERT INTO touristspots " +
                    "(spotid, spotname, division, district, spotaddress, description, entryfee, rating, bestseason, visitinghours) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, spotId);
                ps.setString(2, spotName);
                ps.setString(3, division);
                ps.setString(4, district);
                ps.setString(5, spotAddress);
                ps.setString(6, description);
                ps.setDouble(7, entryFee);
                ps.setDouble(8, rating);
                ps.setString(9, bestSeason);
                ps.setString(10, visitingHours);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error creating spot: " + e.getMessage());
            return false;
        }
    }

    public static TouristSpot getSpotById(String spotId, Connection conn) {
        if (conn == null) return null;
        if (spotId == null || spotId.trim().isEmpty()) return null;

        String sql = "SELECT * FROM touristspots WHERE spotid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spotId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                TouristSpot s = new TouristSpot();
                s.spotId = rs.getString("spotid");
                s.spotName = rs.getString("spotname");
                s.division = rs.getString("division");
                s.district = rs.getString("district");
                s.spotAddress = rs.getString("spotaddress");
                s.description = rs.getString("description");
                s.entryFee = rs.getDouble("entryfee");
                s.rating = rs.getDouble("rating");
                s.totalVisitors = rs.getInt("totalvisitors");
                s.bestSeason = rs.getString("bestseason");
                s.visitingHours = rs.getString("visitinghours");
                return s;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching spot: " + e.getMessage());
            return null;
        }
    }

    public boolean updateSpot(Connection conn) {
        if (conn == null) return false;
        if (spotId == null || spotId.trim().isEmpty()) return false;

        String sql =
                "UPDATE touristspots SET " +
                "spotname = ?, division = ?, district = ?, spotaddress = ?, description = ?, entryfee = ?, rating = ?, " +
                "bestseason = ?, visitinghours = ? WHERE spotid = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spotName);
            ps.setString(2, division);
            ps.setString(3, district);
            ps.setString(4, spotAddress);
            ps.setString(5, description);
            ps.setDouble(6, entryFee);
            ps.setDouble(7, rating);
            ps.setString(8, bestSeason);
            ps.setString(9, visitingHours);
            ps.setString(10, spotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating spot: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSpot(Connection conn) {
        if (conn == null) return false;
        if (spotId == null || spotId.trim().isEmpty()) return false;

        String sql = "DELETE FROM touristspots WHERE spotid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, spotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting spot: " + e.getMessage());
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
                '}';
    }
}