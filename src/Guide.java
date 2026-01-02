import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Guide Entity - matches `guides` table used by AuthService/GuideService.
 */
public class Guide {

    private String guideId;
    private String guideName;
    private String guideEmail;
    private String guidePhone;
    private String guidePassword;
    private String guideDivision;
    private String guideDistrict;
    private String guideLanguage;
    private String specialization;
    private double rating;
    private int totalRatings;
    private boolean isAvailable;
    private int yearExperience;
    private String status;

    public Guide() {
        this.rating = 5.0;
        this.totalRatings = 0;
        this.isAvailable = true;
        this.status = "ACTIVE";
    }

    public Guide(String guideId, String guideName, String guideEmail) {
        this();
        this.guideId = guideId;
        this.guideName = guideName;
        this.guideEmail = guideEmail;
    }

    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public String getGuideEmail() { return guideEmail; }
    public void setGuideEmail(String guideEmail) { this.guideEmail = guideEmail; }

    public String getGuidePhone() { return guidePhone; }
    public void setGuidePhone(String guidePhone) { this.guidePhone = guidePhone; }

    public String getGuidePassword() { return guidePassword; }
    public void setGuidePassword(String guidePassword) { this.guidePassword = guidePassword; }

    public String getGuideDivision() { return guideDivision; }
    public void setGuideDivision(String guideDivision) { this.guideDivision = guideDivision; }

    public String getGuideDistrict() { return guideDistrict; }
    public void setGuideDistrict(String guideDistrict) { this.guideDistrict = guideDistrict; }

    public String getGuideLanguage() { return guideLanguage; }
    public void setGuideLanguage(String guideLanguage) { this.guideLanguage = guideLanguage; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getYearExperience() { return yearExperience; }
    public void setYearExperience(int yearExperience) { this.yearExperience = yearExperience; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // -------------------- Optional DB helpers --------------------

    public boolean setAvailability(Connection conn, boolean available) {
        if (conn == null) return false;
        if (guideId == null || guideId.trim().isEmpty()) return false;

        String sql = "UPDATE guides SET isavailable = ? WHERE guideid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setString(2, guideId);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) this.isAvailable = available;
            return ok;
        } catch (SQLException e) {
            System.out.println("Error setting availability: " + e.getMessage());
            return false;
        }
    }

    public static Guide getGuideById(Connection conn, String guideId) {
        if (conn == null) return null;
        if (guideId == null || guideId.trim().isEmpty()) return null;

        String sql =
                "SELECT guideid, guidename, guideemail, guidephone, guidedivision, guidedistrict, guidelanguage, " +
                "specialization, rating, totalratings, isavailable, yearexperience, status " +
                "FROM guides WHERE guideid = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guideId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Guide g = new Guide();
                g.guideId = rs.getString("guideid");
                g.guideName = rs.getString("guidename");
                g.guideEmail = rs.getString("guideemail");
                g.guidePhone = rs.getString("guidephone");
                g.guideDivision = rs.getString("guidedivision");
                g.guideDistrict = rs.getString("guidedistrict");
                g.guideLanguage = rs.getString("guidelanguage");
                g.specialization = rs.getString("specialization");
                g.rating = rs.getDouble("rating");
                g.totalRatings = rs.getInt("totalratings");
                g.isAvailable = rs.getBoolean("isavailable");
                g.yearExperience = rs.getInt("yearexperience");
                g.status = rs.getString("status");
                return g;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching guide: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return "Guide{" +
                "guideId='" + guideId + '\'' +
                ", guideName='" + guideName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", rating=" + rating +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
