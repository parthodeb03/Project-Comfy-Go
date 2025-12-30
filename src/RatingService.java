import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RatingService {

    private final Connection conn;

    public RatingService(Connection conn) {
        this.conn = conn;
    }

    public boolean submitRating(String userId, String ratingType, String targetName, int rating, String review) {
        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("Login required!");
            return false;
        }
        if (ratingType == null || ratingType.trim().isEmpty()) {
            System.out.println("Rating type required!");
            return false;
        }
        if (targetName == null || targetName.trim().isEmpty()) {
            System.out.println("Target name required!");
            return false;
        }
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5");
            return false;
        }
        if (review != null && review.length() > 500) {
            System.out.println("Review text is too long (max 500 characters)");
            return false;
        }

        String ratingId;
        try {
            ratingId = IdGenerator.uniqueNumericId(conn, "ratings", "ratingid", 12, 60);
        } catch (SQLException e) {
            System.out.println("Failed to generate rating ID: " + e.getMessage());
            return false;
        }

        String sql = "INSERT INTO ratings (ratingid, userid, ratingtype, targetname, rating, review) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ratingId);
            ps.setString(2, userId.trim());
            ps.setString(3, ratingType.trim().toUpperCase());
            ps.setString(4, targetName.trim());
            ps.setInt(5, rating);

            String rv = (review == null || review.trim().isEmpty()) ? null : review.trim();
            ps.setString(6, rv);

            ps.executeUpdate();
            System.out.println("Rating submitted successfully! Rating ID: " + ratingId);
            return true;

        } catch (SQLException e) {
            System.out.println("Rating submission failed: " + e.getMessage());
            return false;
        }
    }

    public boolean rateHotel(String userId, String hotelName, int rating, String review) {
        return submitRating(userId, "HOTEL", hotelName, rating, review);
    }

    public boolean rateTouristSpot(String userId, String spotName, int rating, String review) {
        return submitRating(userId, "SPOT", spotName, rating, review);
    }

    public boolean rateGuide(String userId, String guideName, int rating, String review) {
        return submitRating(userId, "GUIDE", guideName, rating, review);
    }

    public void displayRatings(String ratingType, String targetName) {
        String sql =
                "SELECT rating, review, ratingdate FROM ratings " +
                "WHERE ratingtype = ? AND targetname = ? " +
                "ORDER BY ratingdate DESC LIMIT 10";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ratingType.trim().toUpperCase());
            ps.setString(2, targetName.trim());

            try (ResultSet rs = ps.executeQuery()) {
                boolean has = false;

                System.out.println("=".repeat(70));
                System.out.println("RATINGS FOR " + targetName.toUpperCase());
                System.out.println("=".repeat(70));

                while (rs.next()) {
                    has = true;

                    int r = rs.getInt("rating");
                    String rev = rs.getString("review");
                    Timestamp dt = rs.getTimestamp("ratingdate");

                    String stars = "*".repeat(Math.max(0, r)) + "-".repeat(Math.max(0, 5 - r));

                    System.out.println(stars + " (" + r + "/5)");
                    System.out.println("Review: " + (rev == null ? "No text review" : rev));
                    System.out.println("Date: " + dt);
                    System.out.println("-".repeat(70));
                }

                if (!has) System.out.println("No ratings available yet.");
                System.out.println("=".repeat(70));
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch ratings: " + e.getMessage());
        }
    }

    public double getAverageRating(String ratingType, String targetName) {
        String sql = "SELECT AVG(rating) AS avgrating FROM ratings WHERE ratingtype = ? AND targetname = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ratingType.trim().toUpperCase());
            ps.setString(2, targetName.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("avgrating");
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch average rating: " + e.getMessage());
        }

        return 0.0;
    }

    public int getRatingCount(String ratingType, String targetName) {
        String sql = "SELECT COUNT(*) AS cnt FROM ratings WHERE ratingtype = ? AND targetname = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ratingType.trim().toUpperCase());
            ps.setString(2, targetName.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch rating count: " + e.getMessage());
        }

        return 0;
    }
}