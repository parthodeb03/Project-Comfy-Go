import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransportService {

    public static final String STATUS_ROUTE = "ROUTE";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private final Connection conn;

    public TransportService(Connection conn) {
        this.conn = conn;
        ensureSeedRoutes();
    }

    public boolean bookTransport(String userId, String transportType, String departureLocation,
                                 String arrivalLocation, String departureDate, String arrivalDate,
                                 int passengers, String seatNumber, double fare,
                                 String vehicleRegNo, String vehicleCompany) {

        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("User ID is required!");
            return false;
        }

        if (transportType == null || transportType.trim().isEmpty()) {
            System.out.println("Transport type is required!");
            return false;
        }

        if (departureLocation == null || departureLocation.trim().isEmpty()
                || arrivalLocation == null || arrivalLocation.trim().isEmpty()) {
            System.out.println("Departure and arrival locations are required!");
            return false;
        }

        if (passengers < 1) {
            System.out.println("At least 1 passenger required!");
            return false;
        }

        if (fare <= 0) {
            System.out.println("Invalid fare amount!");
            return false;
        }

        try {
            String ticketId = IdGenerator.uniqueNumericId(conn, "transportbooking", "ticketid", 12, 60);

            String sql = "INSERT INTO transportbooking " +
                    "(ticketid, userid, transporttype, departurelocation, arrivallocation, " +
                    "departuredate, arrivaldate, numberofpassengers, seatnumber, bookingstatus, " +
                    "fare, vehicleregistration, vehiclecompany) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ticketId);
                ps.setString(2, userId.trim());
                ps.setString(3, normalizeType(transportType));
                ps.setString(4, departureLocation.trim());
                ps.setString(5, arrivalLocation.trim());
                ps.setDate(6, Date.valueOf(departureDate.trim()));
                ps.setDate(7, Date.valueOf(arrivalDate.trim()));
                ps.setInt(8, passengers);
                ps.setString(9, seatNumber);
                ps.setString(10, STATUS_CONFIRMED);
                ps.setDouble(11, fare);
                ps.setString(12, vehicleRegNo);
                ps.setString(13, vehicleCompany);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Transport booking successful!");
                    System.out.println("Ticket ID: " + ticketId);
                    return true;
                }

                System.out.println("Failed to create booking!");
                return false;
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format (use YYYY-MM-DD).");
            return false;
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
            return false;
        }
    }

    public List<String> getTransportOptions() {
        List<String> options = new ArrayList<>();
        options.add("Bus");
        options.add("Train");
        options.add("Air");
        options.add("Launch");
        return options;
    }

    /** Routes are stored as rows with userid IS NULL and bookingstatus=ROUTE (seeded). */
    public List<String> getAllRoutes() {
        List<String> routes = new ArrayList<>();

        String sql = "SELECT transporttype, departurelocation, arrivallocation, vehiclecompany, fare " +
                "FROM transportbooking " +
                "WHERE bookingstatus = ? AND userid IS NULL " +
                "ORDER BY transporttype, departurelocation, arrivallocation";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, STATUS_ROUTE);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    routes.add(
                            rs.getString("transporttype") + " | " +
                            rs.getString("departurelocation") + " -> " +
                            rs.getString("arrivallocation") + " | " +
                            rs.getString("vehiclecompany") + " | BDT " +
                            rs.getDouble("fare")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Route fetch failed: " + e.getMessage());
        }

        return routes;
    }

    public void displayAvailableRoutes() {
        List<String> routes = getAllRoutes();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("AVAILABLE ROUTES (Prototype)");
        System.out.println("=".repeat(80));
        if (routes.isEmpty()) {
            System.out.println("No routes available.");
        } else {
            for (String r : routes) System.out.println(r);
        }
        System.out.println("=".repeat(80));
    }

    public List<String> getUserTransportBookings(String userId) {
        List<String> bookings = new ArrayList<>();
        if (userId == null || userId.trim().isEmpty()) return bookings;

        String sql = "SELECT ticketid, transporttype, departurelocation, arrivallocation, departuredate, " +
                "numberofpassengers, fare, bookingstatus " +
                "FROM transportbooking WHERE userid = ? ORDER BY ticketid DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int pax = rs.getInt("numberofpassengers");
                    double perFare = rs.getDouble("fare");

                    bookings.add(
                            "[" + rs.getString("ticketid") + "] " +
                            rs.getString("transporttype") + " | " +
                            rs.getString("departurelocation") + "->" + rs.getString("arrivallocation") + " | " +
                            rs.getDate("departuredate") + " | " +
                            "Pax: " + pax + " | " +
                            "Total: BDT " + (perFare * pax) + " | " +
                            rs.getString("bookingstatus")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Transport booking fetch failed: " + e.getMessage());
        }

        return bookings;
    }

    public boolean cancelTicket(String userId, String ticketId) {
        if (userId == null || userId.trim().isEmpty()) return false;
        if (ticketId == null || ticketId.trim().isEmpty()) return false;

        String sql = "UPDATE transportbooking SET bookingstatus = ? WHERE ticketid = ? AND userid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, STATUS_CANCELLED);
            ps.setString(2, ticketId.trim());
            ps.setString(3, userId.trim());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) System.out.println("Ticket cancelled successfully!");
            else System.out.println("Ticket not found / cannot cancel!");
            return ok;

        } catch (SQLException e) {
            System.out.println("Cancel failed: " + e.getMessage());
            return false;
        }
    }

    // -------------------- seed routes --------------------

    private void ensureSeedRoutes() {
        if (conn == null) return;

        try {
            String check = "SELECT COUNT(*) AS cnt FROM transportbooking WHERE userid IS NULL AND bookingstatus = ?";
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, STATUS_ROUTE);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt("cnt") > 0) return;
                }
            }

            seedMinimalRoutes();

        } catch (SQLException e) {
            System.out.println("Route seed check failed: " + e.getMessage());
        }
    }

    private void seedMinimalRoutes() throws SQLException {
        LocalDate d = LocalDate.now();
        insertRoute("Bus", "Dhaka", "Chattogram", "Hanif Enterprise", d, 450);
        insertRoute("Bus", "Chattogram", "Dhaka", "Hanif Enterprise", d, 450);
        insertRoute("Train", "Dhaka", "Sylhet", "Bangladesh Railway", d, 600);
        insertRoute("Train", "Sylhet", "Dhaka", "Bangladesh Railway", d, 600);
    }

    private void insertRoute(String type, String from, String to, String company, LocalDate d, double fare) throws SQLException {
        String ticketId = IdGenerator.uniqueNumericId(conn, "transportbooking", "ticketid", 12, 60);

        String sql = "INSERT INTO transportbooking " +
                "(ticketid, userid, transporttype, departurelocation, arrivallocation, " +
                "departuredate, arrivaldate, numberofpassengers, seatnumber, bookingstatus, " +
                "fare, vehicleregistration, vehiclecompany) " +
                "VALUES (?, NULL, ?, ?, ?, ?, ?, 0, ?, ?, ?, 'N/A', ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketId);
            ps.setString(2, normalizeType(type));
            ps.setString(3, from);
            ps.setString(4, to);
            ps.setDate(5, Date.valueOf(d));
            ps.setDate(6, Date.valueOf(d));
            ps.setString(7, "R" + ticketId.substring(ticketId.length() - 4));
            ps.setString(8, STATUS_ROUTE);
            ps.setDouble(9, fare);
            ps.setString(10, company);
            ps.executeUpdate();
        }
    }

    private String normalizeType(String t) {
        String x = (t == null) ? "" : t.trim();
        if (x.equalsIgnoreCase("bus")) return "Bus";
        if (x.equalsIgnoreCase("train")) return "Train";
        if (x.equalsIgnoreCase("air") || x.equalsIgnoreCase("flight")) return "Air";
        if (x.equalsIgnoreCase("launch")) return "Launch";
        return x;
    }
}