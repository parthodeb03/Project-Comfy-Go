import java.sql.*;
import java.util.*;

public class TransportService {
    private final Connection conn;

    public TransportService(Connection conn) {
        this.conn = conn;
    }

    public List<String> getAllRoutes() {
        List<String> routes = new ArrayList<>();
        String sql = "SELECT transporttype, departurelocation, arrivallocation, " +
                    "estimatedduration, fare FROM transport ORDER BY transporttype";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String route = String.format("%s: %s -> %s | %s | BDT %.2f",
                    rs.getString("transporttype"),
                    rs.getString("departurelocation"),
                    rs.getString("arrivallocation"),
                    rs.getString("estimatedduration"),
                    rs.getDouble("fare"));
                routes.add(route);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching routes: " + e.getMessage());
        }
        
        return routes;
    }

    public List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        Set<String> uniqueLocations = new TreeSet<>();
        
        String sql = "SELECT DISTINCT departurelocation FROM transport " +
                    "UNION SELECT DISTINCT arrivallocation FROM transport";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String loc = rs.getString(1);
                if (loc != null && !loc.trim().isEmpty()) {
                    uniqueLocations.add(loc.trim());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching locations: " + e.getMessage());
        }
        
        locations.addAll(uniqueLocations);
        return locations;
    }

    public static class RouteInfo {
        public String duration;
        public double fare;
        
        public RouteInfo(String duration, double fare) {
            this.duration = duration;
            this.fare = fare;
        }
    }

    public RouteInfo parseRoute(String route) {
        try {
            String[] parts = route.split("\\|");
            if (parts.length >= 3) {
                String duration = parts[1].trim();
                String fareStr = parts[2].trim().replaceAll("[^0-9.]", "");
                double fare = Double.parseDouble(fareStr);
                return new RouteInfo(duration, fare);
            }
        } catch (Exception e) {
            System.out.println("Error parsing route: " + e.getMessage());
        }
        return new RouteInfo("Unknown", 500.0);
    }

    public int getAvailableSeats(String type, String from, String to, String date) {
        String sql = "SELECT availableseats FROM transport " +
                    "WHERE transporttype = ? AND departurelocation = ? AND arrivallocation = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, from);
            ps.setString(3, to);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("availableseats");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking seats: " + e.getMessage());
        }
        
        return 25;
    }

    public boolean bookTransport(String userId, String transportType, String from, String to,String date, String bookingTime, int passengers, String seatNumbers,double fare, String ticketClass, String provider,boolean isReturn, String returnDate) {
        
        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("User ID required!");
            return false;
        }

        try {
            String ticketId = IdGenerator.uniqueNumericId(conn, "transportbooking", "ticketid", 12, 60);
            
            // FIXED: Match actual database columns
            String sql = "INSERT INTO transportbooking (ticketid, userid, transporttype, " +
                        "departurelocation, arrivallocation, departuredate, numberofpassengers, " +
                        "seatnumber, fare, vehiclecompany, bookingstatus) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'CONFIRMED')";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ticketId);
                ps.setString(2, userId.trim());
                ps.setString(3, transportType);
                ps.setString(4, from);
                ps.setString(5, to);
                ps.setDate(6, java.sql.Date.valueOf(date));
                ps.setInt(7, passengers);
                ps.setString(8, seatNumbers);
                ps.setDouble(9, fare);
                ps.setString(10, provider);
                
                int rows = ps.executeUpdate();
                
                if (isReturn && returnDate != null && !returnDate.trim().isEmpty()) {
                    String returnTicketId = IdGenerator.uniqueNumericId(conn, "transportbooking", "ticketid", 12, 60);
                    
                    try (PreparedStatement psReturn = conn.prepareStatement(sql)) {
                        psReturn.setString(1, returnTicketId);
                        psReturn.setString(2, userId.trim());
                        psReturn.setString(3, transportType);
                        psReturn.setString(4, to);
                        psReturn.setString(5, from);
                        psReturn.setDate(6, java.sql.Date.valueOf(returnDate));
                        psReturn.setInt(7, passengers);
                        psReturn.setString(8, seatNumbers);
                        psReturn.setDouble(9, fare / 2);
                        psReturn.setString(10, provider);
                        
                        psReturn.executeUpdate();
                        System.out.println("Return trip booked! Ticket ID: " + returnTicketId);
                    }
                }
                
                System.out.println("Transport booking successful! Ticket ID: " + ticketId);
                return rows > 0;
            }
        } catch (SQLException e) {
            System.out.println("Booking failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<String> searchTransport(String type, String from, String to) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT transporttype, departurelocation, arrivallocation, " +
                    "estimatedduration, fare FROM transport " +
                    "WHERE transporttype = ? AND departurelocation = ? AND arrivallocation = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, from);
            ps.setString(3, to);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String route = String.format("%s: %s -> %s | %s | BDT %.2f",
                        rs.getString("transporttype"),
                        rs.getString("departurelocation"),
                        rs.getString("arrivallocation"),
                        rs.getString("estimatedduration"),
                        rs.getDouble("fare"));
                    results.add(route);
                }
            }
        } catch (SQLException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
        
        return results;
    }

    public List<Map<String, Object>> getUserBookings(String userId) {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String sql = "SELECT ticketid, transporttype, departurelocation, arrivallocation, " +
                    "departuredate, numberofpassengers, fare, bookingstatus " +
                    "FROM transportbooking WHERE userid = ? ORDER BY departuredate DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> booking = new HashMap<>();
                    booking.put("ticketId", rs.getString("ticketid"));
                    booking.put("type", rs.getString("transporttype"));
                    booking.put("from", rs.getString("departurelocation"));
                    booking.put("to", rs.getString("arrivallocation"));
                    booking.put("date", rs.getDate("departuredate"));
                    booking.put("passengers", rs.getInt("numberofpassengers"));
                    booking.put("fare", rs.getDouble("fare"));
                    booking.put("status", rs.getString("bookingstatus"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching bookings: " + e.getMessage());
        }
        
        return bookings;
    }
}