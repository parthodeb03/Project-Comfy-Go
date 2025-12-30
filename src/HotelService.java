import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HotelService {

    private final Connection conn;

    public HotelService(Connection conn) {
        this.conn = conn;
    }

    // -------------------- Tourist: Browse/Search --------------------

    public List<Hotel> searchHotelsByLocation(String location) {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels WHERE hotellocation LIKE ? ORDER BY hotelrating DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + (location == null ? "" : location.trim()) + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) hotels.add(mapHotel(rs));
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }

        return hotels;
    }

    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels ORDER BY hotelrating DESC LIMIT 50";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) hotels.add(mapHotel(rs));

        } catch (SQLException e) {
            System.out.println("Failed to fetch hotels: " + e.getMessage());
        }

        return hotels;
    }

    public Hotel getHotelById(String hotelId) {
        if (hotelId == null || hotelId.trim().isEmpty()) return null;

        String sql = "SELECT * FROM hotels WHERE hotelid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapHotel(rs);
            }
        } catch (SQLException e) {
            System.out.println("Hotel fetch failed: " + e.getMessage());
        }

        return null;
    }

    // -------------------- Booking + Payment (Tourist) --------------------

    /**
     * Full flow used by ComfyGo menu:
     * - Payment is marked COMPLETED if paidAmount >= totalPrice, else FAILED
     * - Booking status becomes COMPLETED/FAILED accordingly
     * - Rooms reduce only when booking COMPLETED
     */
    public boolean bookHotelWithPayment(String userId, String hotelId, String checkIn, String checkOut,
                                        int numRooms, double totalPrice,
                                        String paymentMethod, double paidAmount) {

        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("Login required to book a hotel!");
            return false;
        }
        if (hotelId == null || hotelId.trim().isEmpty()) {
            System.out.println("Hotel ID is required!");
            return false;
        }
        if (numRooms <= 0) {
            System.out.println("Number of rooms must be at least 1!");
            return false;
        }
        if (totalPrice <= 0) {
            System.out.println("Total price must be > 0!");
            return false;
        }

        Hotel hotel = getHotelById(hotelId);
        if (hotel == null) {
            System.out.println("Hotel not found!");
            return false;
        }
        if (hotel.getRoomAvailability() < numRooms) {
            System.out.println("Not enough rooms available! Available: " + hotel.getRoomAvailability());
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // 1) Create payment
            Payment payment = new Payment();
            payment.setAmount(totalPrice);
            payment.setPaymentMethod(paymentMethod == null || paymentMethod.isBlank() ? "CASH" : paymentMethod.trim());
            payment.setPaymentStatus(Payment.STATUS_PENDING);
            payment.setDescription("Hotel booking payment for: " + hotel.getHotelName());

            if (!payment.createPayment(conn)) {
                conn.rollback();
                System.out.println("Payment record creation failed!");
                return false;
            }

            // 2) Create booking
            Booking booking = new Booking();
            booking.setUserId(userId.trim());
            booking.setCheckInDate(checkIn);
            booking.setCheckOutDate(checkOut);
            booking.setTotalPrice(totalPrice);
            booking.setBookingStatus(Booking.STATUS_PENDING);
            booking.setPaymentId(payment.getPaymentId());
            booking.setHotelName(hotel.getHotelName());
            booking.setHotelLocation(hotel.getHotelLocation());
            booking.setNumberOfRooms(numRooms);

            if (!booking.createBooking(conn)) {
                conn.rollback();
                System.out.println("Booking insert failed!");
                return false;
            }

            // 3) Finalize payment
            payment.markCompletedOrFailed(conn, totalPrice, paidAmount);

            // 4) Booking status based on payment
            String finalBookingStatus =
                    Payment.STATUS_COMPLETED.equalsIgnoreCase(payment.getPaymentStatus())
                            ? Booking.STATUS_COMPLETED
                            : Booking.STATUS_FAILED;

            booking.updateBookingStatus(conn, finalBookingStatus);

            // 5) Reduce rooms only if completed
            if (Booking.STATUS_COMPLETED.equalsIgnoreCase(finalBookingStatus)) {
                updateRoomAvailability(hotelId, hotel.getRoomAvailability() - numRooms);
            }

            conn.commit();
            System.out.println("Hotel booking saved!");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Payment ID: " + payment.getPaymentId());
            System.out.println("Payment Status: " + payment.getPaymentStatus());
            System.out.println("Booking Status: " + finalBookingStatus);
            return true;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.out.println("Booking/payment failed: " + e.getMessage());
            return false;

        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
        }
    }

    public List<Booking> getUserHotelBookings(String userId) {
        return Booking.getBookingsByUser(userId, conn);
    }

    public void displayUserHotelBookings(String userId) {
        List<Booking> list = getUserHotelBookings(userId);

        System.out.println("=".repeat(80));
        System.out.println("MY HOTEL BOOKINGS");
        System.out.println("=".repeat(80));

        if (list.isEmpty()) {
            System.out.println("No hotel bookings found!");
            System.out.println("=".repeat(80));
            return;
        }

        for (Booking b : list) {
            System.out.println(
                    "BookingID: " + b.getBookingId() +
                    " | Hotel: " + b.getHotelName() +
                    " | Location: " + b.getHotelLocation() +
                    " | Rooms: " + b.getNumberOfRooms() +
                    " | Total: BDT " + b.getTotalPrice() +
                    " | Status: " + b.getBookingStatus() +
                    " | PaymentID: " + b.getPaymentId()
            );
        }

        System.out.println("=".repeat(80));
    }

    // -------------------- Cancel + Refund --------------------

    public boolean cancelHotelBookingForUser(String userId, String bookingId) {
        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("Login required!");
            return false;
        }
        if (bookingId == null || bookingId.trim().isEmpty()) {
            System.out.println("Booking ID required!");
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            String readSql = "SELECT bookingstatus, paymentid, hotelname, hotellocation, numberofrooms " +
                    "FROM booking WHERE bookingid = ? AND userid = ?";

            String bookingStatus;
            String paymentId;
            String hotelName;
            String hotelLocation;
            int rooms;

            try (PreparedStatement ps = conn.prepareStatement(readSql)) {
                ps.setString(1, bookingId.trim());
                ps.setString(2, userId.trim());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        System.out.println("Booking not found for this user!");
                        return false;
                    }

                    bookingStatus = rs.getString("bookingstatus");
                    paymentId = rs.getString("paymentid");
                    hotelName = rs.getString("hotelname");
                    hotelLocation = rs.getString("hotellocation");
                    rooms = rs.getInt("numberofrooms");
                }
            }

            if (Booking.STATUS_CANCELLED.equalsIgnoreCase(bookingStatus)) {
                conn.rollback();
                System.out.println("Booking already cancelled!");
                return false;
            }

            // 1) Cancel booking
            String cancelSql = "UPDATE booking SET bookingstatus = ? WHERE bookingid = ? AND userid = ?";
            try (PreparedStatement ps = conn.prepareStatement(cancelSql)) {
                ps.setString(1, Booking.STATUS_CANCELLED);
                ps.setString(2, bookingId.trim());
                ps.setString(3, userId.trim());

                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    System.out.println("Failed to cancel booking!");
                    return false;
                }
            }

            // 2) Refund if payment was COMPLETED
            if (paymentId != null && !paymentId.trim().isEmpty()) {
                String payStatusSql = "SELECT paymentstatus FROM payment WHERE paymentid = ?";
                String payStatus = null;

                try (PreparedStatement ps = conn.prepareStatement(payStatusSql)) {
                    ps.setString(1, paymentId.trim());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) payStatus = rs.getString("paymentstatus");
                    }
                }

                if (payStatus != null && Payment.STATUS_COMPLETED.equalsIgnoreCase(payStatus)) {
                    String refundSql = "UPDATE payment SET paymentstatus = ? WHERE paymentid = ?";
                    try (PreparedStatement ps = conn.prepareStatement(refundSql)) {
                        ps.setString(1, Payment.STATUS_REFUNDED);
                        ps.setString(2, paymentId.trim());
                        ps.executeUpdate();
                    }
                }
            }

            // 3) Restore rooms if it was completed/confirmed
            if (rooms > 0 && (Booking.STATUS_COMPLETED.equalsIgnoreCase(bookingStatus)
                    || Booking.STATUS_CONFIRMED.equalsIgnoreCase(bookingStatus))) {

                String restoreSql =
                        "UPDATE hotels SET roomavailability = roomavailability + ? " +
                        "WHERE hotelname = ? AND hotellocation = ?";

                try (PreparedStatement ps = conn.prepareStatement(restoreSql)) {
                    ps.setInt(1, rooms);
                    ps.setString(2, hotelName);
                    ps.setString(3, hotelLocation);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("Booking cancelled successfully!");
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.out.println("Cancel/refund failed: " + e.getMessage());
            return false;

        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
        }
    }

    // -------------------- Manager: Update --------------------

    public boolean updateRoomAvailability(String hotelId, int newAvailability) {
        if (hotelId == null || hotelId.trim().isEmpty()) return false;

        String sql = "UPDATE hotels SET roomavailability = ? WHERE hotelid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newAvailability);
            ps.setString(2, hotelId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateHotelPrice(String hotelId, double newPrice) {
        if (hotelId == null || hotelId.trim().isEmpty()) return false;

        String sql = "UPDATE hotels SET hotelpricepernight = ? WHERE hotelid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, hotelId.trim());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Price updated to BDT " + newPrice);
            return ok;

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateRoomAvailabilityForManager(String managerId, int newAvailability) {
        String hotelId = getHotelIdByManager(managerId);
        if (hotelId == null) {
            System.out.println("No hotel found for this manager!");
            return false;
        }
        return updateRoomAvailability(hotelId, newAvailability);
    }

    public boolean updateHotelPriceForManager(String managerId, double newPrice) {
        String hotelId = getHotelIdByManager(managerId);
        if (hotelId == null) {
            System.out.println("No hotel found for this manager!");
            return false;
        }
        return updateHotelPrice(hotelId, newPrice);
    }

    private String getHotelIdByManager(String managerId) {
        if (managerId == null || managerId.trim().isEmpty()) return null;

        String sql = "SELECT hotelid FROM hotels WHERE managerid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, managerId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("hotelid");
            }
        } catch (SQLException e) {
            System.out.println("Failed to find manager hotel: " + e.getMessage());
        }

        return null;
    }

    // -------------------- Food Menu --------------------

    public List<String> getFoodMenu(String hotelId) {
        List<String> menu = new ArrayList<>();
        if (hotelId == null || hotelId.trim().isEmpty()) return menu;

        String sql = "SELECT foodname, foodprice, fooddescription FROM foodmenu WHERE hotelid = ? AND isavailable = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getString("foodname") + " - BDT " +
                            rs.getDouble("foodprice") + " (" + rs.getString("fooddescription") + ")";
                    menu.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Menu fetch failed: " + e.getMessage());
        }

        return menu;
    }

    public void displayHotelInfo(Hotel hotel) {
        if (hotel == null) {
            System.out.println("Hotel not found!");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("HOTEL DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("Name: " + hotel.getHotelName());
        System.out.println("Location: " + hotel.getHotelLocation());
        System.out.println("Price/Night: BDT " + hotel.getPricePerNight());
        System.out.println("Rating: " + hotel.getRating() + "/5");
        System.out.println("Rooms Available: " + hotel.getRoomAvailability() + "/" + hotel.getTotalRooms());
        System.out.println("Features: " + hotel.getFeatures());
        System.out.println("=".repeat(60));
    }

    // -------------------- Helpers --------------------

    private Hotel mapHotel(ResultSet rs) throws SQLException {
        Hotel hotel = new Hotel();
        hotel.setHotelId(rs.getString("hotelid"));
        hotel.setHotelName(rs.getString("hotelname"));
        hotel.setHotelLocation(rs.getString("hotellocation"));
        hotel.setPricePerNight(rs.getDouble("hotelpricepernight"));
        hotel.setRating(rs.getDouble("hotelrating"));
        hotel.setRoomAvailability(rs.getInt("roomavailability"));
        hotel.setRoomCategory(rs.getString("roomcategory"));
        hotel.setTotalRooms(rs.getInt("totalrooms"));
        hotel.setFeatures(rs.getString("hotelfeatures"));
        hotel.setManagerId(rs.getString("managerid"));
        return hotel;
    }
}
