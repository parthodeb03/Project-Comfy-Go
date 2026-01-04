import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HotelService {

    // Booking status (align with booking table)
    public static final String BOOKING_PENDING = "PENDING";
    public static final String BOOKING_CONFIRMED = "CONFIRMED";
    public static final String BOOKING_COMPLETED = "COMPLETED";
    public static final String BOOKING_FAILED = "FAILED";
    public static final String BOOKING_CANCELLED = "CANCELLED";

    // Payment: ONLY 2 statuses
    public static final String PAY_COMPLETED = "COMPLETED";
    public static final String PAY_CANCELLED = "CANCELLED";

    private final Connection conn;

    public HotelService(Connection conn) {
        this.conn = conn;
    }

    // -------------------- Tourist: Browse/Search --------------------

    public List<Hotel> searchHotelsByLocation(String location) {
        List<Hotel> hotels = new ArrayList<>();
        String sql = "SELECT * FROM hotels WHERE hotellocation LIKE ? ORDER BY hotelrating DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + normalize(location) + "%");
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
        if (isBlank(hotelId)) return null;

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

    // -------------------- Manager: Hotel Lookup --------------------

    public Hotel getHotelByManagerId(String managerId) {
        if (isBlank(managerId)) return null;

        String sql = "SELECT * FROM hotels WHERE managerid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, managerId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapHotel(rs);
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch manager hotel: " + e.getMessage());
        }
        return null;
    }

    public void displayManagerHotel(String managerId) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) {
            System.out.println("No hotel found for this manager!");
            return;
        }
        displayHotelInfo(h);
    }

    // -------------------- Booking + Payment (Tourist) --------------------
    /**
     * Rules implemented:
     * - If paidAmount < totalPrice: create payment as CANCELLED, do NOT create booking
     * - If paidAmount >= totalPrice: create payment COMPLETED, create booking COMPLETED, reduce rooms
     */
    public boolean bookHotelWithPayment(
            String userId,
            String hotelId,
            String checkIn,
            String checkOut,
            int numRooms,
            double totalPrice,
            String paymentMethod,
            double paidAmount
    ) {
        if (isBlank(userId)) { System.out.println("Login required to book a hotel!"); return false; }
        if (isBlank(hotelId)) { System.out.println("Hotel ID is required!"); return false; }
        if (numRooms <= 0) { System.out.println("Number of rooms must be at least 1!"); return false; }
        if (totalPrice <= 0) { System.out.println("Total price must be > 0!"); return false; }

        Hotel hotel = getHotelById(hotelId);
        if (hotel == null) { System.out.println("Hotel not found!"); return false; }

        if (hotel.getRoomAvailability() < numRooms) {
            System.out.println("Not enough rooms available! Available: " + hotel.getRoomAvailability());
            return false;
        }

        Date ci;
        Date co;
        try {
            ci = Date.valueOf(checkIn);
            co = Date.valueOf(checkOut);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format (use YYYY-MM-DD).");
            return false;
        }

        if (!co.after(ci)) {
            System.out.println("Check-out date must be after check-in date!");
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            boolean fullyPaid = paidAmount >= totalPrice;
            String paymentStatus = fullyPaid ? PAY_COMPLETED : PAY_CANCELLED;

            String paymentId = createPayment(
                    totalPrice,
                    paymentMethod,
                    "Hotel booking: " + hotel.getHotelName(),
                    paymentStatus
            );

            if (paymentId == null) {
                conn.rollback();
                System.out.println("Payment record creation failed!");
                return false;
            }

            if (!fullyPaid) {
                conn.commit();
                System.out.println("Payment not completed. Payment saved as CANCELLED. Booking not created.");
                System.out.println("Payment ID: " + paymentId);
                return false;
            }

            String bookingId = createHotelBooking(
                    userId.trim(),
                    ci,
                    co,
                    totalPrice,
                    BOOKING_COMPLETED,
                    paymentId,
                    hotel.getHotelName(),
                    hotel.getHotelLocation(),
                    numRooms
            );

            if (bookingId == null) {
                conn.rollback();
                System.out.println("Booking insert failed!");
                return false;
            }

            int newAvail = hotel.getRoomAvailability() - numRooms;
            updateRoomAvailability(hotel.getHotelId(), newAvail);

            conn.commit();
            System.out.println("Hotel booking saved!");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Payment ID: " + paymentId);
            System.out.println("Payment Status: " + paymentStatus);
            System.out.println("Booking Status: " + BOOKING_COMPLETED);
            return true;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            System.out.println("Booking/payment failed: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
        }
    }

    // -------------------- Tourist: View bookings --------------------

    public void displayUserHotelBookings(String userId) {
        if (isBlank(userId)) {
            System.out.println("Login required!");
            return;
        }

        String sql =
                "SELECT bookingid, hotelname, hotellocation, numberofrooms, totalprice, bookingstatus, paymentid, checkindate, checkoutdate, bookingdate " +
                "FROM booking WHERE userid = ? ORDER BY bookingdate DESC";

        System.out.println("=".repeat(90));
        System.out.println("MY HOTEL BOOKINGS");
        System.out.println("=".repeat(90));

        boolean foundAny = false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("%-12s | %-22s | %-14s | %-5s | %-10s | %-10s | %-12s%n",
                        "BookingID", "Hotel", "Location", "Room", "Total", "Status", "PaymentID");
                System.out.println("-".repeat(90));

                while (rs.next()) {
                    foundAny = true;
                    System.out.printf("%-12s | %-22s | %-14s | %-5d | %-10.0f | %-10s | %-12s%n",
                            safe(rs.getString("bookingid")),
                            truncate(safe(rs.getString("hotelname")), 22),
                            truncate(safe(rs.getString("hotellocation")), 14),
                            rs.getInt("numberofrooms"),
                            rs.getDouble("totalprice"),
                            safe(rs.getString("bookingstatus")),
                            safe(rs.getString("paymentid"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch bookings: " + e.getMessage());
        }

        if (!foundAny) System.out.println("No hotel bookings found!");
        System.out.println("=".repeat(90));
    }

    // -------------------- Tourist: Cancel --------------------

    public boolean cancelHotelBookingForUser(String userId, String bookingId) {
        if (isBlank(userId)) {
            System.out.println("Login required!");
            return false;
        }
        return cancelBookingInternal(bookingId, userId.trim(), null);
    }

    // -------------------- Manager: View bookings --------------------

    public void displayManagerHotelBookings(String managerId) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) {
            System.out.println("No hotel found for this manager!");
            return;
        }

        String sql =
                "SELECT b.bookingid, b.userid, u.username, b.checkindate, b.checkoutdate, b.numberofrooms, " +
                "b.totalprice, b.bookingstatus, b.paymentid, b.bookingdate " +
                "FROM booking b " +
                "LEFT JOIN users u ON b.userid = u.userid " +
                "WHERE b.hotelname = ? AND b.hotellocation = ? " +
                "ORDER BY b.bookingdate DESC";

        System.out.println("=".repeat(120));
        System.out.println("HOTEL BOOKINGS FOR: " + h.getHotelName() + " (" + h.getHotelLocation() + ")");
        System.out.println("=".repeat(120));

        boolean foundAny = false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getHotelName());
            ps.setString(2, h.getHotelLocation());
            try (ResultSet rs = ps.executeQuery()) {

                System.out.printf("%-3s | %-12s | %-18s | %-10s | %-10s | %-5s | %-10s | %-10s | %-12s%n",
                        "No", "BookingID", "Tourist", "CheckIn", "CheckOut", "Room", "Total", "Status", "PaymentID");
                System.out.println("-".repeat(120));

                int i = 1;
                while (rs.next()) {
                    foundAny = true;
                    System.out.printf("%-3d | %-12s | %-18s | %-10s | %-10s | %-5d | %-10.0f | %-10s | %-12s%n",
                            i++,
                            safe(rs.getString("bookingid")),
                            truncate(safe(rs.getString("username")), 18),
                            safe(String.valueOf(rs.getDate("checkindate"))),
                            safe(String.valueOf(rs.getDate("checkoutdate"))),
                            rs.getInt("numberofrooms"),
                            rs.getDouble("totalprice"),
                            safe(rs.getString("bookingstatus")),
                            safe(rs.getString("paymentid"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch hotel bookings: " + e.getMessage());
        }

        if (!foundAny) System.out.println("No bookings found for your hotel.");
        System.out.println("=".repeat(120));
    }

    public boolean updateBookingStatusForManager(String managerId, String bookingId, String newStatus) {
        if (isBlank(managerId) || isBlank(bookingId) || isBlank(newStatus)) return false;

        newStatus = newStatus.trim().toUpperCase();
        if (!isAllowedBookingStatus(newStatus)) {
            System.out.println("Invalid booking status: " + newStatus);
            return false;
        }

        if (BOOKING_CANCELLED.equalsIgnoreCase(newStatus)) {
            return cancelHotelBookingForManager(managerId, bookingId);
        }

        Hotel h = getHotelByManagerId(managerId);
        if (h == null) {
            System.out.println("No hotel found for this manager!");
            return false;
        }

        String sql =
                "UPDATE booking SET bookingstatus = ? " +
                "WHERE bookingid = ? AND hotelname = ? AND hotellocation = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, bookingId.trim());
            ps.setString(3, h.getHotelName());
            ps.setString(4, h.getHotelLocation());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Booking status updated to " + newStatus);
            else System.out.println("Booking not found for your hotel (or update failed).");
            return ok;
        } catch (SQLException e) {
            System.out.println("Booking status update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelHotelBookingForManager(String managerId, String bookingId) {
        if (isBlank(managerId)) return false;

        Hotel h = getHotelByManagerId(managerId);
        if (h == null) {
            System.out.println("No hotel found for this manager!");
            return false;
        }

        return cancelBookingInternal(bookingId, null, h);
    }

    // -------------------- Manager: Update hotel fields --------------------

    public boolean updateRoomAvailabilityForManager(String managerId, int newAvailability) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) {
            System.out.println("No hotel found for this manager!");
            return false;
        }
        return updateRoomAvailability(h.getHotelId(), newAvailability);
    }

    public boolean updateHotelPriceForManager(String managerId, double newPrice) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) {
            System.out.println("No hotel found for this manager!");
            return false;
        }
        return updateHotelPrice(h.getHotelId(), newPrice);
    }

    public boolean updateRoomAvailability(String hotelId, int newAvailability) {
        if (isBlank(hotelId)) return false;

        Hotel h = getHotelById(hotelId);
        if (h == null) return false;

        int max = h.getTotalRooms() > 0 ? h.getTotalRooms() : Integer.MAX_VALUE;
        int clamped = Math.max(0, Math.min(newAvailability, max));
        if (clamped != newAvailability) {
            System.out.println("Room availability adjusted to valid range: " + clamped);
        }

        String sql = "UPDATE hotels SET roomavailability = ? WHERE hotelid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clamped);
            ps.setString(2, hotelId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateHotelPrice(String hotelId, double newPrice) {
        if (isBlank(hotelId)) return false;
        if (newPrice <= 0) {
            System.out.println("Price must be > 0!");
            return false;
        }

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

    // -------------------- Food Menu (public read for tourists) --------------------

    public List<String> getFoodMenu(String hotelId) {
        List<String> menu = new ArrayList<>();
        if (isBlank(hotelId)) return menu;

        String sql = "SELECT foodname, foodprice, fooddescription FROM foodmenu WHERE hotelid = ? AND isavailable = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String item =
                            safe(rs.getString("foodname")) +
                            " - BDT " + rs.getDouble("foodprice") +
                            " (" + safe(rs.getString("fooddescription")) + ")";
                    menu.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Menu fetch failed: " + e.getMessage());
        }
        return menu;
    }

    // -------------------- Food Menu (Manager CRUD) --------------------

    public void displayFoodMenuForManager(String managerId) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) { System.out.println("No hotel found for this manager!"); return; }

        String sql = "SELECT menuid, foodname, foodcategory, foodprice, isavailable " +
                     "FROM foodmenu WHERE hotelid = ? ORDER BY foodcategory, foodname";

        System.out.println("=".repeat(80));
        System.out.println("FOOD MENU (" + h.getHotelName() + ")");
        System.out.println("=".repeat(80));

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getHotelId());
            try (ResultSet rs = ps.executeQuery()) {
                System.out.printf("%-10s | %-22s | %-12s | %-10s | %-10s%n",
                        "MenuID", "Name", "Category", "Price", "Available");
                System.out.println("-".repeat(80));

                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.printf("%-10s | %-22s | %-12s | %-10.0f | %-10s%n",
                            safe(rs.getString("menuid")),
                            truncate(safe(rs.getString("foodname")), 22),
                            truncate(safe(rs.getString("foodcategory")), 12),
                            rs.getDouble("foodprice"),
                            rs.getBoolean("isavailable") ? "YES" : "NO"
                    );
                }

                if (!any) System.out.println("No food items found.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch food menu: " + e.getMessage());
        }

        System.out.println("=".repeat(80));
    }

    public boolean addFoodItemForManager(String managerId, String foodName, String category,
                                        double price, String description, boolean isAvailable) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) { System.out.println("No hotel found for this manager!"); return false; }
        if (isBlank(foodName) || price <= 0) { System.out.println("Food name + valid price required!"); return false; }

        try {
            String menuId = IdGenerator.uniqueNumericId(conn, "foodmenu", "menuid", 12, 60);
            String sql = "INSERT INTO foodmenu (menuid, hotelid, foodname, foodcategory, foodprice, fooddescription, isavailable) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, menuId);
                ps.setString(2, h.getHotelId());
                ps.setString(3, foodName.trim());
                ps.setString(4, isBlank(category) ? "General" : category.trim());
                ps.setDouble(5, price);
                ps.setString(6, isBlank(description) ? "" : description.trim());
                ps.setBoolean(7, isAvailable);

                boolean ok = ps.executeUpdate() > 0;
                if (ok) System.out.println("Food item added. MenuID: " + menuId);
                return ok;
            }
        } catch (SQLException e) {
            System.out.println("Add food item failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFoodItemForManager(String managerId, String menuId, String foodName, String category,
                                           double price, String description, boolean isAvailable) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) { System.out.println("No hotel found for this manager!"); return false; }
        if (isBlank(menuId)) return false;
        if (isBlank(foodName) || price <= 0) { System.out.println("Food name + valid price required!"); return false; }

        String sql = "UPDATE foodmenu SET foodname=?, foodcategory=?, foodprice=?, fooddescription=?, isavailable=? " +
                     "WHERE menuid=? AND hotelid=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, foodName.trim());
            ps.setString(2, isBlank(category) ? "General" : category.trim());
            ps.setDouble(3, price);
            ps.setString(4, isBlank(description) ? "" : description.trim());
            ps.setBoolean(5, isAvailable);
            ps.setString(6, menuId.trim());
            ps.setString(7, h.getHotelId());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Food item updated.");
            else System.out.println("Menu item not found for your hotel.");
            return ok;
        } catch (SQLException e) {
            System.out.println("Update food item failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteFoodItemForManager(String managerId, String menuId) {
        Hotel h = getHotelByManagerId(managerId);
        if (h == null) { System.out.println("No hotel found for this manager!"); return false; }
        if (isBlank(menuId)) return false;

        String sql = "DELETE FROM foodmenu WHERE menuid=? AND hotelid=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, menuId.trim());
            ps.setString(2, h.getHotelId());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Food item deleted.");
            else System.out.println("Menu item not found for your hotel.");
            return ok;
        } catch (SQLException e) {
            System.out.println("Delete food item failed: " + e.getMessage());
            return false;
        }
    }

    // -------------------- Display hotel --------------------

    public void displayHotelInfo(Hotel hotel) {
        if (hotel == null) {
            System.out.println("Hotel not found!");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("HOTEL DETAILS");
        System.out.println("=".repeat(60));
        System.out.println("ID: " + safe(hotel.getHotelId()));
        System.out.println("Name: " + safe(hotel.getHotelName()));
        System.out.println("Location: " + safe(hotel.getHotelLocation()));
        System.out.println("Price/Night: BDT " + hotel.getPricePerNight());
        System.out.println("Rating: " + hotel.getRating() + "/5");
        System.out.println("Rooms Available: " + hotel.getRoomAvailability() + "/" + hotel.getTotalRooms());
        System.out.println("Category: " + safe(hotel.getRoomCategory()));
        System.out.println("Description: " + safe(hotel.getDescription()));
        System.out.println("Features: " + safe(hotel.getFeatures()));

        List<String> menu = getFoodMenu(hotel.getHotelId());
        if (menu != null && !menu.isEmpty()) {
            System.out.println("-".repeat(60));
            System.out.println("Food Menu:");
            for (String item : menu) System.out.println("• " + item);
        }

        System.out.println("=".repeat(60));
    }

    // ===================== Internal helpers =====================

    private String createPayment(double amount, String method, String description, String status) throws SQLException {
        String paymentId = IdGenerator.uniqueNumericId(conn, "payment", "paymentid", 12, 60);

        String transactionId = "TXN" + System.currentTimeMillis() + "-" +
                paymentId.substring(Math.max(0, paymentId.length() - 4));

        String payMethod = isBlank(method) ? "CASH" : method.trim().toUpperCase();
        String st = isBlank(status) ? PAY_CANCELLED : status.trim().toUpperCase();
        if (!PAY_COMPLETED.equals(st) && !PAY_CANCELLED.equals(st)) st = PAY_CANCELLED;

        String sql = "INSERT INTO payment (paymentid, amount, paymentmethod, paymentstatus, transactionid, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentId);
            ps.setDouble(2, amount);
            ps.setString(3, payMethod);
            ps.setString(4, st);
            ps.setString(5, transactionId);
            ps.setString(6, description);
            int rows = ps.executeUpdate();
            return rows > 0 ? paymentId : null;
        }
    }

    private String createHotelBooking(
            String userId,
            Date checkIn,
            Date checkOut,
            double totalPrice,
            String bookingStatus,
            String paymentId,
            String hotelName,
            String hotelLocation,
            int rooms
    ) throws SQLException {

        String bookingId = IdGenerator.uniqueNumericId(conn, "booking", "bookingid", 12, 60);

        String sql =
                "INSERT INTO booking " +
                "(bookingid, userid, checkindate, checkoutdate, totalprice, bookingstatus, paymentid, " +
                "hotelname, hotellocation, numberofrooms) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            ps.setString(2, userId);
            ps.setDate(3, checkIn);
            ps.setDate(4, checkOut);
            ps.setDouble(5, totalPrice);
            ps.setString(6, bookingStatus);
            ps.setString(7, paymentId);
            ps.setString(8, hotelName);
            ps.setString(9, hotelLocation);
            ps.setInt(10, rooms);

            int rows = ps.executeUpdate();
            return rows > 0 ? bookingId : null;
        }
    }

    /**
     * Cancellation core:
     * - If userId is non-null => must match booking.userid
     * - If managerHotel is non-null => must match booking.hotelname+hotellocation
     * - Always sets bookingstatus=CANCELLED
     * - Always sets paymentstatus=CANCELLED (only 2 statuses)
     * - Restores rooms if old booking reserved rooms (COMPLETED/CONFIRMED)
     */
    private boolean cancelBookingInternal(String bookingId, String userId, Hotel managerHotel) {
        if (isBlank(bookingId)) {
            System.out.println("Booking ID required!");
            return false;
        }

        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            String readSql =
                    "SELECT bookingstatus, paymentid, hotelname, hotellocation, numberofrooms, userid " +
                    "FROM booking WHERE bookingid = ? LIMIT 1";

            String bookingStatus;
            String paymentId;
            String hotelName;
            String hotelLocation;
            int rooms;
            String bookedUserId;

            try (PreparedStatement ps = conn.prepareStatement(readSql)) {
                ps.setString(1, bookingId.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        System.out.println("Booking not found!");
                        return false;
                    }

                    bookingStatus = rs.getString("bookingstatus");
                    paymentId = rs.getString("paymentid");
                    hotelName = rs.getString("hotelname");
                    hotelLocation = rs.getString("hotellocation");
                    rooms = rs.getInt("numberofrooms");
                    bookedUserId = rs.getString("userid");
                }
            }

            if (userId != null && !safe(userId).equals(safe(bookedUserId))) {
                conn.rollback();
                System.out.println("Permission denied: booking does not belong to this user.");
                return false;
            }

            if (managerHotel != null) {
                if (!safe(managerHotel.getHotelName()).equals(safe(hotelName)) ||
                    !safe(managerHotel.getHotelLocation()).equals(safe(hotelLocation))) {
                    conn.rollback();
                    System.out.println("Permission denied: booking does not belong to your hotel.");
                    return false;
                }
            }

            if (BOOKING_CANCELLED.equalsIgnoreCase(bookingStatus)) {
                conn.rollback();
                System.out.println("Booking already cancelled!");
                return false;
            }

            // 1) Cancel booking
            String cancelSql = "UPDATE booking SET bookingstatus = ? WHERE bookingid = ?";
            try (PreparedStatement ps = conn.prepareStatement(cancelSql)) {
                ps.setString(1, BOOKING_CANCELLED);
                ps.setString(2, bookingId.trim());
                if (ps.executeUpdate() <= 0) {
                    conn.rollback();
                    System.out.println("Failed to cancel booking!");
                    return false;
                }
            }

            // 2) Payment -> CANCELLED
            if (!isBlank(paymentId)) {
                String cancelPaySql = "UPDATE payment SET paymentstatus = 'CANCELLED' WHERE paymentid = ?";
                try (PreparedStatement ps = conn.prepareStatement(cancelPaySql)) {
                    ps.setString(1, paymentId.trim());
                    ps.executeUpdate();
                }
            }

            // 3) Restore rooms if old booking reserved rooms
            if (rooms > 0 && (BOOKING_COMPLETED.equalsIgnoreCase(bookingStatus) || BOOKING_CONFIRMED.equalsIgnoreCase(bookingStatus))) {
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
            System.out.println("Cancel failed: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
        }
    }

    private boolean isAllowedBookingStatus(String s) {
        if (s == null) return false;
        return BOOKING_PENDING.equals(s) ||
               BOOKING_CONFIRMED.equals(s) ||
               BOOKING_COMPLETED.equals(s) ||
               BOOKING_FAILED.equals(s) ||
               BOOKING_CANCELLED.equals(s);
    }

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
        hotel.setDescription(rs.getString("hoteldescription"));
        hotel.setManagerId(rs.getString("managerid"));
        return hotel;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String normalize(String s) {
        return s == null ? "" : s.trim();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        if (max <= 3) return s.length() <= max ? s : s.substring(0, max);
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    public List<Hotel.Room> getAvailableRoomCount(String hotelId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAvailableRoomCount'");
    }
}