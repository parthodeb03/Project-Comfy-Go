import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * GuideService
 * - Listing/searching guides
 * - Hiring a guide (creates a guidebooking row)
 * - Guide-side booking management (view + update tour/payment status)
 *
 * Tables used (from comfygo.sql):
 * - guides
 * - guidebooking
 * - users (for tourist name in views)
 */
public class GuideService {

    public static final String TOUR_STATUS_PENDING = "PENDING";
    public static final String TOUR_STATUS_CONFIRMED = "CONFIRMED";
    public static final String TOUR_STATUS_REJECTED = "REJECTED";
    public static final String TOUR_STATUS_COMPLETED = "COMPLETED";
    public static final String TOUR_STATUS_CANCELLED = "CANCELLED";

    public static final String PAY_STATUS_PENDING = "PENDING";
    public static final String PAY_STATUS_COMPLETED = "COMPLETED";
    public static final String PAY_STATUS_FAILED = "FAILED";
    public static final String PAY_STATUS_REFUNDED = "REFUNDED";

    private final Connection conn;

    public GuideService(Connection conn) {
        this.conn = conn;
    }

    // ===================== Public DTO =====================
    public static class GuideBookingInfo {
        private String bookingId;
        private String userId;
        private String touristName;
        private String guideId;
        private Timestamp bookingDate;
        private int tourDurationDays;
        private String tourPurpose;
        private String tourLocation;
        private String tourStatus;
        private double guideFee;
        private String paymentStatus;
        private String specialRequest;

        public String getBookingId() { return bookingId; }
        public String getUserId() { return userId; }
        public String getTouristName() { return touristName; }
        public String getGuideId() { return guideId; }
        public Timestamp getBookingDate() { return bookingDate; }
        public int getTourDurationDays() { return tourDurationDays; }
        public String getTourPurpose() { return tourPurpose; }
        public String getTourLocation() { return tourLocation; }
        public String getTourStatus() { return tourStatus; }
        public double getGuideFee() { return guideFee; }
        public String getPaymentStatus() { return paymentStatus; }
        public String getSpecialRequest() { return specialRequest; }
    }

    // ===================== Tourist: Guide browse/search =====================
    public List<Guide> getAvailableGuides() {
        List<Guide> guides = new ArrayList<>();
        String sql = "SELECT * FROM guides WHERE isavailable = TRUE AND status = 'ACTIVE' ORDER BY rating DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) guides.add(mapGuide(rs));
        } catch (SQLException e) {
            System.out.println("Failed to fetch guides: " + e.getMessage());
        }
        return guides;
    }

    public List<Guide> searchGuidesByDivision(String division) {
        List<Guide> guides = new ArrayList<>();
        String sql =
                "SELECT * FROM guides " +
                "WHERE guidedivision = ? AND isavailable = TRUE AND status = 'ACTIVE' " +
                "ORDER BY rating DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, normalize(division));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) guides.add(mapGuide(rs));
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
        return guides;
    }

    public List<Guide> searchGuidesBySpecialization(String specialization) {
        List<Guide> guides = new ArrayList<>();
        String sql =
                "SELECT * FROM guides " +
                "WHERE specialization LIKE ? AND isavailable = TRUE AND status = 'ACTIVE' " +
                "ORDER BY rating DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + normalize(specialization) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) guides.add(mapGuide(rs));
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
        return guides;
    }

    public Guide getGuideById(String guideId) {
        if (isBlank(guideId)) return null;
        String sql = "SELECT * FROM guides WHERE guideid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guideId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapGuide(rs);
            }
        } catch (SQLException e) {
            System.out.println("Guide fetch failed: " + e.getMessage());
        }
        return null;
    }

    // ===================== Tourist: Hire guide =====================
    /**
     * Creates a row in guidebooking.
     * Payment is tracked inside guidebooking.paymentstatus (per your schema),
     * so this method sets it to PENDING; you can later mark it COMPLETED/FAILED.
     */
    public boolean hireGuide(String userId, String guideId, String location, int days, String purpose) {
        if (isBlank(userId)) {
            System.out.println("User ID required!");
            return false;
        }
        if (isBlank(guideId)) {
            System.out.println("Guide ID required!");
            return false;
        }
        if (days <= 0) {
            System.out.println("Tour duration must be at least 1 day!");
            return false;
        }

        Guide g = getGuideById(guideId);
        if (g == null) {
            System.out.println("Guide not found!");
            return false;
        }
        if (!g.isAvailable()) {
            System.out.println("Guide is not available right now!");
            return false;
        }
        if (!"ACTIVE".equalsIgnoreCase(safe(g.getStatus()))) {
            System.out.println("Guide is not active!");
            return false;
        }

        // Fee model (non-prototype): derive from experience + rating
        double dailyFee = calculateDailyFee(g);
        double guideFee = dailyFee * days;

        String bookingId;
        try {
            bookingId = IdGenerator.uniqueNumericId(conn, "guidebooking", "bookingid", 12, 60);
        } catch (SQLException e) {
            System.out.println("Booking ID generation failed: " + e.getMessage());
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            String sql =
                    "INSERT INTO guidebooking " +
                    "(bookingid, userid, guideid, tourdurationdays, tourpurpose, tourlocation, tourstatus, guidefee, paymentstatus) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, bookingId);
                ps.setString(2, userId.trim());
                ps.setString(3, guideId.trim());
                ps.setInt(4, days);
                ps.setString(5, nullableTrim(purpose));
                ps.setString(6, nullableTrim(location));
                ps.setString(7, TOUR_STATUS_PENDING);
                ps.setDouble(8, guideFee);
                ps.setString(9, PAY_STATUS_PENDING);
                ps.executeUpdate();
            }

            // Optional: mark guide as unavailable when they have a pending booking
            setGuideAvailabilityInternal(guideId, false);

            conn.commit();

            System.out.println("Guide hiring request created!");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Estimated fee: BDT " + guideFee + " (" + days + " days @ BDT " + dailyFee + "/day)");
            System.out.println("Tour status: " + TOUR_STATUS_PENDING + ", Payment: " + PAY_STATUS_PENDING);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.out.println("Hiring failed: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
        }
    }

    // ===================== Guide: Availability =====================
    /**
     * Safer availability update:
     * - Prevents setting available=TRUE if guide still has PENDING/CONFIRMED tours.
     */
    public boolean setGuideAvailability(String guideId, boolean isAvailable) {
        if (isBlank(guideId)) return false;

        if (isAvailable && hasActiveTours(guideId)) {
            System.out.println("Cannot set AVAILABLE while you have active tours (PENDING/CONFIRMED).");
            return false;
        }

        return setGuideAvailabilityInternal(guideId, isAvailable);
    }

    // ===================== Guide: View bookings =====================
    public List<GuideBookingInfo> getBookingsForGuide(String guideId) {
        List<GuideBookingInfo> list = new ArrayList<>();
        if (isBlank(guideId)) return list;

        String sql =
                "SELECT gb.bookingid, gb.userid, u.username AS touristname, gb.guideid, gb.bookingdate, " +
                "gb.tourdurationdays, gb.tourpurpose, gb.tourlocation, gb.tourstatus, gb.guidefee, gb.paymentstatus, gb.specialrequest " +
                "FROM guidebooking gb " +
                "LEFT JOIN users u ON gb.userid = u.userid " +
                "WHERE gb.guideid = ? " +
                "ORDER BY gb.bookingdate DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guideId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GuideBookingInfo b = new GuideBookingInfo();
                    b.bookingId = rs.getString("bookingid");
                    b.userId = rs.getString("userid");
                    b.touristName = rs.getString("touristname");
                    b.guideId = rs.getString("guideid");
                    b.bookingDate = rs.getTimestamp("bookingdate");
                    b.tourDurationDays = rs.getInt("tourdurationdays");
                    b.tourPurpose = rs.getString("tourpurpose");
                    b.tourLocation = rs.getString("tourlocation");
                    b.tourStatus = rs.getString("tourstatus");
                    b.guideFee = rs.getDouble("guidefee");
                    b.paymentStatus = rs.getString("paymentstatus");
                    b.specialRequest = rs.getString("specialrequest");
                    list.add(b);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch guide bookings: " + e.getMessage());
        }

        return list;
    }

    public void displayGuideBookings(String guideId) {
        List<GuideBookingInfo> list = getBookingsForGuide(guideId);

        System.out.println("\n" + "=".repeat(110));
        System.out.println("MY GUIDE BOOKINGS");
        System.out.println("=".repeat(110));

        if (list.isEmpty()) {
            System.out.println("No guide bookings found!");
            System.out.println("=".repeat(110));
            return;
        }

        System.out.printf("%-3s | %-12s | %-18s | %-10s | %-10s | %-14s | %-10s | %-12s%n",
                "No", "BookingID", "Tourist", "Days", "Fee(BDT)", "TourStatus", "Payment", "Location");
        System.out.println("-".repeat(110));

        int i = 1;
        for (GuideBookingInfo b : list) {
            System.out.printf("%-3d | %-12s | %-18s | %-10d | %-10.0f | %-14s | %-10s | %-12s%n",
                    i++,
                    safe(b.getBookingId()),
                    truncate(safe(b.getTouristName()), 18),
                    b.getTourDurationDays(),
                    b.getGuideFee(),
                    safe(b.getTourStatus()),
                    safe(b.getPaymentStatus()),
                    truncate(safe(b.getTourLocation()), 12)
            );
        }

        System.out.println("=".repeat(110));
    }

    // ===================== Guide: Manage bookings =====================
    /**
     * Allowed transitions (basic rules):
     * - PENDING -> CONFIRMED / REJECTED / CANCELLED
     * - CONFIRMED -> COMPLETED / CANCELLED
     * (Other transitions rejected)
     */
    public boolean updateTourStatusForGuide(String guideId, String bookingId, String newStatus) {
        if (isBlank(guideId) || isBlank(bookingId) || isBlank(newStatus)) return false;

        newStatus = newStatus.trim().toUpperCase();

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            String current = getCurrentTourStatusForGuide(guideId, bookingId);
            if (current == null) {
                conn.rollback();
                System.out.println("Booking not found for this guide!");
                return false;
            }

            if (!isValidTourStatusTransition(current, newStatus)) {
                conn.rollback();
                System.out.println("Invalid tour status transition: " + current + " -> " + newStatus);
                return false;
            }

            String sql = "UPDATE guidebooking SET tourstatus = ? WHERE bookingid = ? AND guideid = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setString(2, bookingId.trim());
                ps.setString(3, guideId.trim());
                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    System.out.println("Failed to update tour status!");
                    return false;
                }
            }

            // If no more active tours after completion/cancel/reject, allow guide to become available again
            if (!hasActiveTours(guideId)) {
                setGuideAvailabilityInternal(guideId, true);
            }

            conn.commit();
            System.out.println("Tour status updated to " + newStatus);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.out.println("Tour status update failed: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
        }
    }

    public boolean updatePaymentStatusForGuide(String guideId, String bookingId, String newPaymentStatus) {
        if (isBlank(guideId) || isBlank(bookingId) || isBlank(newPaymentStatus)) return false;
        newPaymentStatus = newPaymentStatus.trim().toUpperCase();

        if (!PAY_STATUS_PENDING.equals(newPaymentStatus) &&
            !PAY_STATUS_COMPLETED.equals(newPaymentStatus) &&
            !PAY_STATUS_FAILED.equals(newPaymentStatus) &&
            !PAY_STATUS_REFUNDED.equals(newPaymentStatus)) {
            System.out.println("Invalid payment status: " + newPaymentStatus);
            return false;
        }

        String sql = "UPDATE guidebooking SET paymentstatus = ? WHERE bookingid = ? AND guideid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPaymentStatus);
            ps.setString(2, bookingId.trim());
            ps.setString(3, guideId.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Payment status updated to " + newPaymentStatus);
            return ok;
        } catch (SQLException e) {
            System.out.println("Payment status update failed: " + e.getMessage());
            return false;
        }
    }

    // ===================== UI helpers =====================
    public void displayGuideInfo(Guide guide) {
        if (guide == null) {
            System.out.println("Guide not found!");
            return;
        }

        double dailyFee = calculateDailyFee(guide);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("GUIDE PROFILE");
        System.out.println("=".repeat(60));
        System.out.println("Name: " + safe(guide.getGuideName()));
        System.out.println("Email: " + safe(guide.getGuideEmail()));
        System.out.println("Phone: " + safe(guide.getGuidePhone()));
        System.out.println("Specialization: " + safe(guide.getSpecialization()));
        System.out.println("Division: " + safe(guide.getGuideDivision()));
        System.out.println("District: " + safe(guide.getGuideDistrict()));
        System.out.println("Languages: " + safe(guide.getGuideLanguage()));
        System.out.println("Experience: " + guide.getYearExperience() + " years");
        System.out.println("Rating: " + guide.getRating() + "/5");
        System.out.println("Availability: " + (guide.isAvailable() ? "AVAILABLE" : "NOT AVAILABLE"));
        System.out.println("Estimated fee: BDT " + dailyFee + "/day");
        System.out.println("=".repeat(60));
    }

    // ===================== Internal helpers =====================
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

    private boolean setGuideAvailabilityInternal(String guideId, boolean isAvailable) {
        String sql = "UPDATE guides SET isavailable = ? WHERE guideid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isAvailable);
            ps.setString(2, guideId.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Availability updated to " + (isAvailable ? "AVAILABLE" : "NOT AVAILABLE"));
            return ok;
        } catch (SQLException e) {
            System.out.println("Availability update failed: " + e.getMessage());
            return false;
        }
    }

    private boolean hasActiveTours(String guideId) {
        String sql =
                "SELECT 1 FROM guidebooking " +
                "WHERE guideid = ? AND tourstatus IN ('PENDING', 'CONFIRMED') " +
                "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, guideId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            // fail-open to avoid blocking guide usage due to a transient read error
            return false;
        }
    }

    private String getCurrentTourStatusForGuide(String guideId, String bookingId) throws SQLException {
        String sql = "SELECT tourstatus FROM guidebooking WHERE bookingid = ? AND guideid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingId.trim());
            ps.setString(2, guideId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("tourstatus");
            }
        }
        return null;
    }

    private boolean isValidTourStatusTransition(String from, String to) {
        if (from == null || to == null) return false;
        from = from.trim().toUpperCase();
        to = to.trim().toUpperCase();

        if (from.equals(to)) return true;

        return switch (from) {
            case TOUR_STATUS_PENDING -> to.equals(TOUR_STATUS_CONFIRMED) || to.equals(TOUR_STATUS_REJECTED) || to.equals(TOUR_STATUS_CANCELLED);
            case TOUR_STATUS_CONFIRMED -> to.equals(TOUR_STATUS_COMPLETED) || to.equals(TOUR_STATUS_CANCELLED);
            default -> false;
        };
    }

    /**
     * Non-prototype fee calculation:
     * - Base depends on specialization (light weighting)
     * - Adds experience premium
     * - Adds rating premium
     */
    private double calculateDailyFee(Guide g) {
        double base = 2500.0;

        String spec = safe(g.getSpecialization()).toLowerCase();
        if (spec.contains("adventure")) base += 800;
        else if (spec.contains("history")) base += 500;
        else if (spec.contains("nature")) base += 400;
        else if (spec.contains("beach")) base += 300;

        int exp = Math.max(0, g.getYearExperience());
        base += Math.min(2000.0, exp * 150.0); // cap experience premium

        double rating = Math.max(0.0, Math.min(5.0, g.getRating()));
        base += rating * 200.0;

        // round to nearest 50
        return Math.round(base / 50.0) * 50.0;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim();
    }

    private static String nullableTrim(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        if (max <= 3) return s.length() <= max ? s : s.substring(0, max);
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}