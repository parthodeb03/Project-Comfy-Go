import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Guide Service
 * Handles guide listing, searching, hiring, and availability updates.
 */
public class GuideService {

    private final Connection conn;

    public GuideService(Connection conn) {
        this.conn = conn;
    }

    // ===== GET ALL AVAILABLE GUIDES =====
    public List<Guide> getAvailableGuides() {
        List<Guide> guides = new ArrayList<>();
        String sql = "SELECT * FROM guides WHERE isavailable = TRUE AND status = 'ACTIVE' ORDER BY rating DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                guides.add(mapGuide(rs));
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch guides: " + e.getMessage());
        }

        return guides;
    }

    // ===== SEARCH GUIDES BY DIVISION =====
    public List<Guide> searchGuidesByDivision(String division) {
        List<Guide> guides = new ArrayList<>();
        if (division == null) division = "";

        String sql =
                "SELECT * FROM guides " +
                "WHERE guidedivision = ? AND isavailable = TRUE AND status = 'ACTIVE' " +
                "ORDER BY rating DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, division.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) guides.add(mapGuide(rs));
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }

        return guides;
    }

    // ===== SEARCH GUIDES BY SPECIALIZATION =====
    public List<Guide> searchGuidesBySpecialization(String specialization) {
        List<Guide> guides = new ArrayList<>();
        if (specialization == null) specialization = "";

        String sql =
                "SELECT * FROM guides " +
                "WHERE specialization LIKE ? AND isavailable = TRUE AND status = 'ACTIVE' " +
                "ORDER BY rating DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + specialization.trim() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) guides.add(mapGuide(rs));
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }

        return guides;
    }

    // ===== GET GUIDE BY ID =====
    public Guide getGuideById(String guideId) {
        if (guideId == null || guideId.trim().isEmpty()) return null;

        String sql = "SELECT * FROM guides WHERE guideid = ? LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guideId.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapGuide(rs);
            }
        } catch (SQLException e) {
            System.out.println("Guide fetch failed: " + e.getMessage());
        }

        return null;
    }

    // ===== HIRE GUIDE =====
    public boolean hireGuide(String userId, String guideId, String location, int days, String purpose) {
        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("User ID required!");
            return false;
        }
        if (guideId == null || guideId.trim().isEmpty()) {
            System.out.println("Guide ID required!");
            return false;
        }
        if (days <= 0) {
            System.out.println("Days must be at least 1!");
            return false;
        }

        // optional: ensure guide exists
        Guide g = getGuideById(guideId);
        if (g == null) {
            System.out.println("Guide not found!");
            return false;
        }
        if (!g.isAvailable()) {
            System.out.println("Guide is not available right now!");
            return false;
        }

        String bookingId;
        try {
            bookingId = IdGenerator.uniqueNumericId(conn, "guidebooking", "bookingid", 12, 60);
        } catch (SQLException e) {
            System.out.println("Booking ID generation failed: " + e.getMessage());
            return false;
        }

        double guideFee = days * 5000.0; // prototype fee

        String sql =
                "INSERT INTO guidebooking " +
                "(bookingid, userid, guideid, tourdurationdays, tourpurpose, tourlocation, tourstatus, guidefee, paymentstatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', ?, 'PENDING')";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            pstmt.setString(2, userId.trim());
            pstmt.setString(3, guideId.trim());
            pstmt.setInt(4, days);
            pstmt.setString(5, safeText(purpose));
            pstmt.setString(6, safeText(location));
            pstmt.setDouble(7, guideFee);

            pstmt.executeUpdate();

            System.out.println("Guide hiring successful!");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Guide Fee: BDT " + guideFee + " (" + days + " days)");
            return true;

        } catch (SQLException e) {
            System.out.println("Hiring failed: " + e.getMessage());
            return false;
        }
    }

    // ===== SET GUIDE AVAILABILITY =====
    public boolean setGuideAvailability(String guideId, boolean isAvailable) {
        if (guideId == null || guideId.trim().isEmpty()) return false;

        String sql = "UPDATE guides SET isavailable = ? WHERE guideid = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isAvailable);
            pstmt.setString(2, guideId.trim());

            boolean ok = pstmt.executeUpdate() > 0;
            if (ok) System.out.println("Availability updated");
            return ok;

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    // ===== DISPLAY GUIDE INFO =====
    public void displayGuideInfo(Guide guide) {
        if (guide == null) {
            System.out.println("Guide not found!");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("GUIDE PROFILE");
        System.out.println("=".repeat(60));
        System.out.println("Name: " + guide.getGuideName());
        System.out.println("Email: " + guide.getGuideEmail());
        System.out.println("Phone: " + guide.getGuidePhone());
        System.out.println("Specialization: " + guide.getSpecialization());
        System.out.println("Division: " + guide.getGuideDivision());
        System.out.println("District: " + guide.getGuideDistrict());
        System.out.println("Languages: " + guide.getGuideLanguage());
        System.out.println("Experience: " + guide.getYearExperience() + " years");
        System.out.println("Rating: " + guide.getRating() + "/5");
        System.out.println("Fee: BDT 5000/day");
        System.out.println("=".repeat(60));
    }

    // ===== LIST ALL GUIDES =====
    public void listAllGuides() {
        List<Guide> guides = getAvailableGuides();
        if (guides.isEmpty()) {
            System.out.println("No guides available!");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("AVAILABLE TOUR GUIDES");
        System.out.println("=".repeat(80));
        System.out.printf("%-3s | %-20s | %-20s | %-15s | %-6s | %-5s%n",
                "No", "Name", "Specialization", "Division", "Rating", "Exp");
        System.out.println("-".repeat(80));

        int count = 1;
        for (Guide guide : guides) {
            System.out.printf("%-3d | %-20s | %-20s | %-15s | %-6.1f | %-5d%n",
                    count,
                    safeText(guide.getGuideName()),
                    safeText(guide.getSpecialization()),
                    safeText(guide.getGuideDivision()),
                    guide.getRating(),
                    guide.getYearExperience());
            count++;
        }
        System.out.println("=".repeat(80));
    }

    // -------------------- helpers --------------------

    private Guide mapGuide(ResultSet rs) throws SQLException {
        Guide guide = new Guide();
        guide.setGuideId(rs.getString("guideid"));
        guide.setGuideName(rs.getString("guidename"));
        guide.setGuideEmail(rs.getString("guideemail"));
        guide.setGuidePhone(rs.getString("guidephone"));
        guide.setGuideDivision(rs.getString("guidedivision"));
        guide.setGuideDistrict(rs.getString("guidedistrict"));
        guide.setGuideLanguage(rs.getString("guidelanguage"));
        guide.setSpecialization(rs.getString("specialization"));
        guide.setRating(rs.getDouble("rating"));
        guide.setTotalRatings(rs.getInt("totalratings"));
        guide.setAvailable(rs.getBoolean("isavailable"));
        guide.setYearExperience(rs.getInt("yearexperience"));
        guide.setStatus(rs.getString("status"));
        return guide;
    }

    private String safeText(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}