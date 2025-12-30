import java.util.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * ComfyGo - Bangladeshi Tourism System
 * Main Application Class with Complete CRUD Operations
 * 
 * Features:
 * - Multi-role system (Tourist, Tour Guide, Hotel Manager)
 * - Hotel booking
 * - Guide hiring
 * - Tourist spot browsing
 * - Transport booking
 * - Rating system
 * - Weather info
 * - Complete data management
 */
public class ComfyGoApp {
    private static final String URL = "jdbc:mysql://localhost:3306/comfygo";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection conn;
    private static Scanner sc;
    private static String currentUserId = null;
    private static String currentUserRole = null;
    private static String currentUserName = null;

    public static void main(String[] args) {
        sc = new Scanner(System.in);

        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to ComfyGo Database Successfully!");
            mainMenu();
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
            sc.close();
        }
    }

    // ===== MAIN MENU =====
    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("WELCOME TO COMFYGO TOURISM SYSTEM");
            if (currentUserId == null) {
                System.out.println("MAIN MENU");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Browse Tourist Spots (Guest)");
                System.out.println("4. View Weather (Guest)");
                System.out.println("5. Exit");
                System.out.print("Choose option: ");

                String choice = sc.nextLine();
                switch (choice) {
                    case "1":
                        registrationMenu();
                        break;
                    case "2":
                        loginMenu();
                        break;
                    case "3":
                        browseAllTouristSpots();
                        break;
                    case "4":
                        viewWeatherInfo();
                        break;
                    case "5":
                        running = false;
                        System.out.println("Thank you for using ComfyGo! See you soon!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                // User is logged in
                switch (currentUserRole) {
                    case "TOURIST":
                        touristMenu();
                        break;
                    case "GUIDE":
                        guideMenu();
                        break;
                    case "MANAGER":
                        managerMenu();
                        break;
                }
                running = false;
            }
        }
    }

    // ===== REGISTRATION MENU =====
    private static void registrationMenu() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("REGISTRATION");
        System.out.println("-".repeat(60));
        System.out.println("Choose role to register:");
        System.out.println("1. Tourist (Normal User)");
        System.out.println("2. Tour Guide");
        System.out.println("3. Hotel Manager");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose role: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1":
                registerTourist();
                break;
            case "2":
                registerGuide();
                break;
            case "3":
                registerManager();
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    // ===== REGISTER TOURIST =====
    private static void registerTourist() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("TOURIST REGISTRATION");
        System.out.println("-".repeat(60));
        try {
            String userID = UUID.randomUUID().toString().substring(0, 12);
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            // Check if email already exists
            if (emailExists(email, "users")) {
                System.out.println("Email already registered!");
                return;
            }

            System.out.print("Phone Number: ");
            String phone = sc.nextLine().trim();
            System.out.print("Country: ");
            String country = sc.nextLine().trim();

            String nid = "";
            String passport = "";
            if (country.equalsIgnoreCase("Bangladesh")) {
                System.out.print("NID (National ID): ");
                nid = sc.nextLine().trim();
            } else {
                System.out.print("Passport Number: ");
                passport = sc.nextLine().trim();
            }

            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dob = sc.nextLine().trim();
            System.out.print("Address: ");
            String address = sc.nextLine().trim();
            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            // Insert into database
            String sql = "INSERT INTO users (userID, userName, userEmail, userPhone, userNID, passportNo, dateOfBirth, userPassword, country, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userID);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, phone);
                pstmt.setString(5, nid);
                pstmt.setString(6, passport);
                pstmt.setString(7, dob);
                pstmt.setString(8, password);
                pstmt.setString(9, country);
                pstmt.setString(10, address);
                pstmt.executeUpdate();

                System.out.println("Registration successful!");
                System.out.println("Your User ID: " + userID);
                System.out.println("Now you can login with your email and password.");
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // ===== REGISTER GUIDE =====
    private static void registerGuide() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("TOUR GUIDE REGISTRATION");
        System.out.println("-".repeat(60));
        try {
            String guideID = UUID.randomUUID().toString().substring(0, 12);
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            if (emailExists(email, "guides")) {
                System.out.println("Email already registered!");
                return;
            }

            System.out.print("Phone Number: ");
            String phone = sc.nextLine().trim();
            System.out.println("Available Divisions: Dhaka, Chittagong, Khulna, Rajshahi, Barisal, Sylhet, Rangpur, Mymensingh");
            System.out.print("Your Division: ");
            String division = sc.nextLine().trim();
            System.out.print("Your District: ");
            String district = sc.nextLine().trim();
            System.out.print("Languages (comma-separated): ");
            String languages = sc.nextLine().trim();
            System.out.print("Specialization (History, Culture, Nature, Adventure, etc.): ");
            String specialization = sc.nextLine().trim();
            System.out.print("Years of Experience: ");
            int experience = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            String sql = "INSERT INTO guides (guideId, guideName, guideEmail, guidePhone, guidePassword, guideDivision, guideDistrict, guideLanguage, specialization, yearExperience) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, guideID);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, phone);
                pstmt.setString(5, password);
                pstmt.setString(6, division);
                pstmt.setString(7, district);
                pstmt.setString(8, languages);
                pstmt.setString(9, specialization);
                pstmt.setInt(10, experience);
                pstmt.executeUpdate();

                System.out.println("Guide Registration successful!");
                System.out.println("Your Guide ID: " + guideID);
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format!");
        }
    }

    // ===== REGISTER MANAGER =====
    private static void registerManager() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("HOTEL MANAGER REGISTRATION");
        System.out.println("-".repeat(60));
        try {
            String managerID = UUID.randomUUID().toString().substring(0, 12);
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            if (emailExists(email, "managers")) {
                System.out.println("Email already registered!");
                return;
            }

            System.out.print("Phone Number: ");
            String phone = sc.nextLine().trim();
            System.out.print("Hotel Name: ");
            String hotelName = sc.nextLine().trim();
            System.out.print("Hotel NID: ");
            String nid = sc.nextLine().trim();
            System.out.print("Registration Number: ");
            String regNumber = sc.nextLine().trim();
            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            String sql = "INSERT INTO managers (managerId, managerName, managerEmail, managerPhone, managerPassword, hotelName, hotelNID, registrationNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, managerID);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, phone);
                pstmt.setString(5, password);
                pstmt.setString(6, hotelName);
                pstmt.setString(7, nid);
                pstmt.setString(8, regNumber);
                pstmt.executeUpdate();

                System.out.println("Manager Registration successful!");
                System.out.println("Your Manager ID: " + managerID);
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    // ===== LOGIN MENU =====
    private static void loginMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("LOGIN");
        System.out.println("=".repeat(60));
        System.out.println("Choose role:");
        System.out.println("1. Tourist");
        System.out.println("2. Tour Guide");
        System.out.println("3. Hotel Manager");
        System.out.println("4. Back");
        System.out.print("Choose role: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1":
                loginUser("TOURIST", "users", "userEmail", "userPassword", "userName");
                break;
            case "2":
                loginUser("GUIDE", "guides", "guideEmail", "guidePassword", "guideName");
                break;
            case "3":
                loginUser("MANAGER", "managers", "managerEmail", "managerPassword", "managerName");
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    // ===== GENERIC LOGIN =====
    private static void loginUser(String role, String table, String emailCol, String passCol, String nameCol) {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        try {
            String idCol = role.equals("TOURIST") ? "userID" : role.equals("GUIDE") ? "guideId" : "managerId";
            String sql = "SELECT " + idCol + ", " + nameCol + " FROM " + table + " WHERE " + emailCol + " = ? AND " + passCol + " = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        currentUserId = rs.getString(1);
                        currentUserRole = role;
                        currentUserName = rs.getString(2);

                        System.out.println("Login successful!");
                        System.out.println("Welcome, " + currentUserName + "!");
                    } else {
                        System.out.println("Invalid email or password!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    // ===== TOURIST MENU =====
    private static void touristMenu() {
        boolean inMenu = true;
        while (inMenu && currentUserId != null) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("TOURIST MENU");
            System.out.println("Welcome, " + currentUserName);
            System.out.println("1. Browse Tourist Spots");
            System.out.println("2. Search & Book Hotels");
            System.out.println("3. Book Transport Tickets");
            System.out.println("4. Hire Tour Guide");
            System.out.println("5. View My Bookings");
            System.out.println("6. Rate & Review");
            System.out.println("7. View Weather");
            System.out.println("8. View My Profile");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    browseAllTouristSpots();
                    break;
                case "2":
                    searchAndBookHotels();
                    break;
                case "3":
                    bookTransportTicket();
                    break;
                case "4":
                    hireGuide();
                    break;
                case "5":
                    viewMyBookings();
                    break;
                case "6":
                    submitRating();
                    break;
                case "7":
                    viewWeatherInfo();
                    break;
                case "8":
                    viewTouristProfile();
                    break;
                case "9":
                    logout();
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ===== BROWSE TOURIST SPOTS =====
    private static void browseAllTouristSpots() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TOURIST SPOTS BY DIVISION");
        System.out.println("=".repeat(60));

        try {
            // First show divisions
            String divisionSql = "SELECT DISTINCT divisionName FROM divisions ORDER BY divisionName";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(divisionSql)) {

                System.out.println("Divisions:");
                List<String> divisions = new ArrayList<>();
                int count = 1;
                while (rs.next()) {
                    String division = rs.getString("divisionName");
                    divisions.add(division);
                    System.out.println(count + ". " + division);
                    count++;
                }

                System.out.print("Choose division number (0 to go back): ");
                try {
                    int choice = Integer.parseInt(sc.nextLine().trim());
                    if (choice == 0) return;
                    if (choice < 1 || choice > divisions.size()) {
                        System.out.println("Invalid choice!");
                        return;
                    }

                    String selectedDivision = divisions.get(choice - 1);

                    // Show districts in division
                    String districtSql = "SELECT DISTINCT districtName FROM districts WHERE divisionName = ? ORDER BY districtName";
                    try (PreparedStatement pstmt = conn.prepareStatement(districtSql)) {
                        pstmt.setString(1, selectedDivision);
                        try (ResultSet districtRs = pstmt.executeQuery()) {

                            System.out.println("Districts in " + selectedDivision + ":");
                            List<String> districts = new ArrayList<>();
                            int distCount = 1;
                            while (districtRs.next()) {
                                String district = districtRs.getString("districtName");
                                districts.add(district);
                                System.out.println(distCount + ". " + district);
                                distCount++;
                            }

                            if (districts.isEmpty()) {
                                System.out.println("No districts found!");
                                return;
                            }

                            System.out.print("Choose district number (0 to go back): ");
                            int distChoice = Integer.parseInt(sc.nextLine().trim());
                            if (distChoice == 0) return;
                            if (distChoice < 1 || distChoice > districts.size()) {
                                System.out.println("Invalid choice!");
                                return;
                            }

                            String selectedDistrict = districts.get(distChoice - 1);
                            showTouristSpotsByLocation(selectedDivision, selectedDistrict);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== SHOW TOURIST SPOTS BY LOCATION =====
    private static void showTouristSpotsByLocation(String division, String district) {
        try {
            String sql = "SELECT spotId, spotName, description, entryFee, rating, bestSeason, visitingHours FROM touristspots WHERE division = ? AND district = ? ORDER BY spotName";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, division);
                pstmt.setString(2, district);

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("\n" + "-".repeat(60));
                    System.out.println("TOURIST SPOTS IN " + district + ", " + division);
                    System.out.println("-".repeat(60));

                    List<String> spotIds = new ArrayList<>();
                    int count = 1;
                    while (rs.next()) {
                        String spotId = rs.getString("spotId");
                        String spotName = rs.getString("spotName");
                        String description = rs.getString("description");
                        double entryFee = rs.getDouble("entryFee");
                        double rating = rs.getDouble("rating");
                        String bestSeason = rs.getString("bestSeason");
                        String visitingHours = rs.getString("visitingHours");

                        spotIds.add(spotId);

                        System.out.println(count + ". " + spotName);
                        System.out.println("   " + description);
                        System.out.println("   Entry Fee: " + entryFee);
                        System.out.println("   Rating: " + rating + "/5");
                        System.out.println("   Best Season: " + bestSeason);
                        System.out.println("   Hours: " + visitingHours);
                        count++;
                    }

                    if (spotIds.isEmpty()) {
                        System.out.println("No tourist spots found!");
                        return;
                    }

                    System.out.print("\nChoose spot number to view more details (0 to go back): ");
                    try {
                        int spotChoice = Integer.parseInt(sc.nextLine().trim());
                        if (spotChoice == 0) return;
                        if (spotChoice < 1 || spotChoice > spotIds.size()) {
                            System.out.println("Invalid choice!");
                            return;
                        }

                        String selectedSpotId = spotIds.get(spotChoice - 1);
                        showSpotDetails(selectedSpotId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== SHOW SPOT DETAILS =====
    private static void showSpotDetails(String spotId) {
        try {
            String sql = "SELECT spotName, division, district, spotAddress, description, entryFee, rating, totalVisitors, bestSeason, visitingHours FROM touristspots WHERE spotId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, spotId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n" + "=".repeat(60));
                        System.out.println("SPOT DETAILS");
                        System.out.println("=".repeat(60));
                        System.out.println("Name: " + rs.getString("spotName"));
                        System.out.println("Location: " + rs.getString("spotAddress") + ", " + rs.getString("district") + ", " + rs.getString("division"));
                        System.out.println("Description: " + rs.getString("description"));
                        System.out.println("Entry Fee: " + rs.getDouble("entryFee"));
                        System.out.println("Rating: " + rs.getDouble("rating") + "/5");
                        System.out.println("Total Visitors: " + rs.getInt("totalVisitors"));
                        System.out.println("Best Season: " + rs.getString("bestSeason"));
                        System.out.println("Visiting Hours: " + rs.getString("visitingHours"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== SEARCH AND BOOK HOTELS =====
    private static void searchAndBookHotels() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SEARCH & BOOK HOTELS");
        System.out.println("=".repeat(60));

        try {
            String sql = "SELECT hotelId, hotelName, hotelLocation, hotelPriceperNight, hotelrating, roomavailability FROM hotels WHERE roomavailability >= 1 ORDER BY hotelrating DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("Available Hotels:");
                List<String> hotelIds = new ArrayList<>();
                int count = 1;
                while (rs.next()) {
                    String hotelId = rs.getString("hotelId");
                    String name = rs.getString("hotelName");
                    String location = rs.getString("hotelLocation");
                    double price = rs.getDouble("hotelPriceperNight");
                    double rating = rs.getDouble("hotelrating");
                    int rooms = rs.getInt("roomavailability");

                    hotelIds.add(hotelId);

                    System.out.println(count + ". " + name);
                    System.out.println("   Location: " + location);
                    System.out.println("   Price: $" + price + " per night, Rating: " + rating + "/5");
                    System.out.println("   Rooms: " + rooms + " available");
                    count++;
                }

                if (hotelIds.isEmpty()) {
                    System.out.println("No hotels available!");
                    return;
                }

                System.out.print("\nChoose hotel number to book (0 to go back): ");
                try {
                    int choice = Integer.parseInt(sc.nextLine().trim());
                    if (choice == 0) return;
                    if (choice < 1 || choice > hotelIds.size()) {
                        System.out.println("Invalid choice!");
                        return;
                    }

                    bookHotel(hotelIds.get(choice - 1));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== BOOK HOTEL =====
    private static void bookHotel(String hotelId) {
        try {
            // Get hotel details
            String hotelSql = "SELECT hotelName, hotelLocation, hotelPriceperNight, roomavailability FROM hotels WHERE hotelId = ?";
            try (PreparedStatement hotelStmt = conn.prepareStatement(hotelSql)) {
                hotelStmt.setString(1, hotelId);

                try (ResultSet rs = hotelStmt.executeQuery()) {
                    if (rs.next()) {
                        String hotelName = rs.getString("hotelName");
                        String hotelLocation = rs.getString("hotelLocation");
                        double pricePerNight = rs.getDouble("hotelPriceperNight");
                        int availableRooms = rs.getInt("roomavailability");

                        System.out.println("\n" + "-".repeat(60));
                        System.out.println("BOOKING HOTEL");
                        System.out.println("Hotel: " + hotelName);
                        System.out.println("Location: " + hotelLocation);
                        System.out.println("Price: $" + pricePerNight + " per night");
                        System.out.println("Available Rooms: " + availableRooms);
                        System.out.println("-".repeat(60));

                        System.out.print("Number of rooms to book: ");
                        int roomsToBook = Integer.parseInt(sc.nextLine().trim());
                        if (roomsToBook <= 0 || roomsToBook > availableRooms) {
                            System.out.println("Invalid number of rooms!");
                            return;
                        }

                        System.out.print("Check-in date (YYYY-MM-DD): ");
                        String checkIn = sc.nextLine().trim();
                        System.out.print("Check-out date (YYYY-MM-DD): ");
                        String checkOut = sc.nextLine().trim();

                        // Calculate number of nights
                        LocalDate checkInDate = LocalDate.parse(checkIn);
                        LocalDate checkOutDate = LocalDate.parse(checkOut);
                        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);

                        if (nights <= 0) {
                            System.out.println("Invalid dates!");
                            return;
                        }

                        double totalPrice = pricePerNight * roomsToBook * nights;

                        System.out.println("\nBOOKING SUMMARY");
                        System.out.println("Rooms: " + roomsToBook);
                        System.out.println("Nights: " + nights);
                        System.out.println("Price per night: $" + pricePerNight);
                        System.out.println("Total: $" + totalPrice);

                        System.out.print("Payment Method (Card/Bkash/Nagad/Cash): ");
                        String paymentMethod = sc.nextLine().trim();

                        // Create payment record
                        String paymentId = UUID.randomUUID().toString().substring(0, 12);
                        String paymentSql = "INSERT INTO payment (paymentid, amount, paymentmethod, paymentstatus, description) VALUES (?, ?, ?, 'SUCCESS', ?)";
                        try (PreparedStatement paymentStmt = conn.prepareStatement(paymentSql)) {
                            paymentStmt.setString(1, paymentId);
                            paymentStmt.setDouble(2, totalPrice);
                            paymentStmt.setString(3, paymentMethod);
                            paymentStmt.setString(4, "Hotel booking at " + hotelName);
                            paymentStmt.executeUpdate();
                        }

                        // Create booking record
                        String bookingId = UUID.randomUUID().toString().substring(0, 12);
                        String bookingSql = "INSERT INTO booking (bookingid, userId, checkInDate, checkOutDate, totalprice, bookingstatus, paymentid, hotelname, hotellocation, numberOfRooms) VALUES (?, ?, ?, ?, ?, 'CONFIRMED', ?, ?, ?, ?)";
                        try (PreparedStatement bookingStmt = conn.prepareStatement(bookingSql)) {
                            bookingStmt.setString(1, bookingId);
                            bookingStmt.setString(2, currentUserId);
                            bookingStmt.setString(3, checkIn);
                            bookingStmt.setString(4, checkOut);
                            bookingStmt.setDouble(5, totalPrice);
                            bookingStmt.setString(6, paymentId);
                            bookingStmt.setString(7, hotelName);
                            bookingStmt.setString(8, hotelLocation);
                            bookingStmt.setInt(9, roomsToBook);
                            bookingStmt.executeUpdate();
                        }

                        // Update room availability
                        String updateSql = "UPDATE hotels SET roomavailability = roomavailability - ? WHERE hotelId = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, roomsToBook);
                            updateStmt.setString(2, hotelId);
                            updateStmt.executeUpdate();
                        }

                        System.out.println("\nHotel booked successfully!");
                        System.out.println("Booking ID: " + bookingId);
                        System.out.println("Payment ID: " + paymentId);
                    }
                }
            }
        } catch (SQLException | java.time.format.DateTimeParseException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    // ===== BOOK TRANSPORT TICKET =====
    private static void bookTransportTicket() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("BOOK TRANSPORT TICKET");
        System.out.println("=".repeat(60));

        try {
            String sql = "SELECT transportId, transportType, fromLocation, toLocation, price, departureTime, arrivalTime, availableSeats FROM transport ORDER BY fromLocation, toLocation";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("Transport Options:");
                List<String> transportIds = new ArrayList<>();
                int count = 1;
                while (rs.next()) {
                    String transportId = rs.getString("transportId");
                    String type = rs.getString("transportType");
                    String from = rs.getString("fromLocation");
                    String to = rs.getString("toLocation");
                    double price = rs.getDouble("price");
                    String departure = rs.getString("departureTime");
                    String arrival = rs.getString("arrivalTime");
                    int seats = rs.getInt("availableSeats");

                    transportIds.add(transportId);

                    System.out.println(count + ". " + type + " from " + from + " to " + to);
                    System.out.println("   Price: $" + price + ", " + departure + " - " + arrival + ", Seats: " + seats);
                    count++;
                }

                if (transportIds.isEmpty()) {
                    System.out.println("No transport available!");
                    return;
                }

                System.out.print("\nChoose transport (0 to go back): ");
                try {
                    int choice = Integer.parseInt(sc.nextLine().trim());
                    if (choice == 0) return;
                    if (choice < 1 || choice > transportIds.size()) {
                        System.out.println("Invalid choice!");
                        return;
                    }

                    bookTransport(transportIds.get(choice - 1));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== BOOK TRANSPORT =====
    private static void bookTransport(String transportId) {
        try {
            String transportSql = "SELECT transportType, fromLocation, toLocation, price, availableSeats FROM transport WHERE transportId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(transportSql)) {
                stmt.setString(1, transportId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String type = rs.getString("transportType");
                        String from = rs.getString("fromLocation");
                        String to = rs.getString("toLocation");
                        double price = rs.getDouble("price");
                        int availableSeats = rs.getInt("availableSeats");

                        System.out.println("\n" + "-".repeat(60));
                        System.out.println("Booking " + type + " from " + from + " to " + to);
                        System.out.println("Price: $" + price);
                        System.out.println("Available Seats: " + availableSeats);
                        System.out.println("-".repeat(60));

                        System.out.print("Number of tickets: ");
                        int tickets = Integer.parseInt(sc.nextLine().trim());
                        if (tickets <= 0 || tickets > availableSeats) {
                            System.out.println("Invalid number of tickets!");
                            return;
                        }

                        System.out.print("Travel date (YYYY-MM-DD): ");
                        String travelDate = sc.nextLine().trim();

                        double totalPrice = price * tickets;

                        System.out.println("\nBOOKING SUMMARY");
                        System.out.println("Tickets: " + tickets);
                        System.out.println("Price per ticket: $" + price);
                        System.out.println("Total: $" + totalPrice);

                        System.out.print("Payment Method (Card/Bkash/Nagad/Cash): ");
                        String paymentMethod = sc.nextLine().trim();

                        // Create payment
                        String paymentId = UUID.randomUUID().toString().substring(0, 12);
                        String paymentSql = "INSERT INTO payment (paymentid, amount, paymentmethod, paymentstatus, description) VALUES (?, ?, ?, 'SUCCESS', ?)";
                        try (PreparedStatement paymentStmt = conn.prepareStatement(paymentSql)) {
                            paymentStmt.setString(1, paymentId);
                            paymentStmt.setDouble(2, totalPrice);
                            paymentStmt.setString(3, paymentMethod);
                            paymentStmt.setString(4, "Transport booking " + type);
                            paymentStmt.executeUpdate();
                        }

                        // Create booking
                        String ticketId = UUID.randomUUID().toString().substring(0, 12);
                        String bookingSql = "INSERT INTO transportbooking (ticketId, transportId, userId, travelDate, numberOfTickets, totalPrice, status) VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMED')";
                        try (PreparedStatement bookingStmt = conn.prepareStatement(bookingSql)) {
                            bookingStmt.setString(1, ticketId);
                            bookingStmt.setString(2, transportId);
                            bookingStmt.setString(3, currentUserId);
                            bookingStmt.setString(4, travelDate);
                            bookingStmt.setInt(5, tickets);
                            bookingStmt.setDouble(6, totalPrice);
                            bookingStmt.executeUpdate();
                        }

                        // Update availability
                        String updateSql = "UPDATE transport SET availableSeats = availableSeats - ? WHERE transportId = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, tickets);
                            updateStmt.setString(2, transportId);
                            updateStmt.executeUpdate();
                        }

                        System.out.println("\nTickets booked successfully!");
                        System.out.println("Ticket ID: " + ticketId);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    // ===== HIRE GUIDE =====
    private static void hireGuide() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("HIRE TOUR GUIDE");
        System.out.println("=".repeat(60));

        try {
            String sql = "SELECT guideId, guideName, guideEmail, guidePhone, specialization, guideLanguage, rating FROM guides WHERE isAvailable = TRUE ORDER BY rating DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                System.out.println("Available Guides:");
                List<String> guideIds = new ArrayList<>();
                int count = 1;
                while (rs.next()) {
                    String guideId = rs.getString("guideId");
                    String name = rs.getString("guideName");
                    String specialization = rs.getString("specialization");
                    String languages = rs.getString("guideLanguage");
                    double rating = rs.getDouble("rating");

                    guideIds.add(guideId);

                    System.out.println(count + ". " + name);
                    System.out.println("   Specialization: " + specialization);
                    System.out.println("   Languages: " + languages);
                    System.out.println("   Rating: " + rating + "/5");
                    count++;
                }

                if (guideIds.isEmpty()) {
                    System.out.println("No guides available!");
                    return;
                }

                System.out.print("\nChoose guide (0 to go back): ");
                try {
                    int choice = Integer.parseInt(sc.nextLine().trim());
                    if (choice == 0) return;
                    if (choice < 1 || choice > guideIds.size()) {
                        System.out.println("Invalid choice!");
                        return;
                    }

                    hireSelectedGuide(guideIds.get(choice - 1));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== HIRE SELECTED GUIDE =====
    private static void hireSelectedGuide(String guideId) {
        try {
            String guideSql = "SELECT guideName FROM guides WHERE guideId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(guideSql)) {
                stmt.setString(1, guideId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String guideName = rs.getString("guideName");

                        System.out.println("\n" + "-".repeat(60));
                        System.out.println("Hiring Guide: " + guideName);
                        System.out.println("-".repeat(60));

                        System.out.print("Number of days: ");
                        int days = Integer.parseInt(sc.nextLine().trim());
                        if (days <= 0) {
                            System.out.println("Invalid duration!");
                            return;
                        }

                        // Fixed fee for guide
                        double feePerDay = 3500;
                        double totalFee = feePerDay * days;

                        System.out.println("\nBOOKING SUMMARY");
                        System.out.println("Guide: " + guideName);
                        System.out.println("Duration: " + days + " days");
                        System.out.println("Fee per day: $" + feePerDay);
                        System.out.println("Total Fee: $" + totalFee);

                        System.out.print("Payment Method (Card/Bkash/Nagad/Cash): ");
                        String paymentMethod = sc.nextLine().trim();

                        // Create payment
                        String paymentId = UUID.randomUUID().toString().substring(0, 12);
                        String paymentSql = "INSERT INTO payment (paymentid, amount, paymentmethod, paymentstatus, description) VALUES (?, ?, ?, 'SUCCESS', ?)";
                        try (PreparedStatement paymentStmt = conn.prepareStatement(paymentSql)) {
                            paymentStmt.setString(1, paymentId);
                            paymentStmt.setDouble(2, totalFee);
                            paymentStmt.setString(3, paymentMethod);
                            paymentStmt.setString(4, "Guide hiring - " + guideName);
                            paymentStmt.executeUpdate();
                        }

                        // Create booking
                        String bookingId = UUID.randomUUID().toString().substring(0, 12);
                        String bookingSql = "INSERT INTO booking (bookingid, userId, totalprice, bookingstatus, paymentid, guidename) VALUES (?, ?, ?, 'CONFIRMED', ?, ?)";
                        try (PreparedStatement bookingStmt = conn.prepareStatement(bookingSql)) {
                            bookingStmt.setString(1, bookingId);
                            bookingStmt.setString(2, currentUserId);
                            bookingStmt.setDouble(3, totalFee);
                            bookingStmt.setString(4, paymentId);
                            bookingStmt.setString(5, guideName);
                            bookingStmt.executeUpdate();
                        }

                        System.out.println("\nGuide hired successfully!");
                        System.out.println("Booking ID: " + bookingId);
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Hiring failed: " + e.getMessage());
        }
    }

    // ===== VIEW MY BOOKINGS =====
    private static void viewMyBookings() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("MY BOOKINGS");
        System.out.println("=".repeat(60));

        try {
            String sql = "SELECT bookingid, checkInDate, checkOutDate, totalprice, bookingstatus, hotelname, guidename FROM booking WHERE userId = ? ORDER BY bookingdate DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println();
                    int count = 1;
                    while (rs.next()) {
                        String bookingId = rs.getString("bookingid");
                        String checkIn = rs.getString("checkInDate");
                        String checkOut = rs.getString("checkOutDate");
                        double price = rs.getDouble("totalprice");
                        String status = rs.getString("bookingstatus");
                        String hotelName = rs.getString("hotelname");
                        String guideName = rs.getString("guidename");

                        System.out.println(count + ". Booking ID: " + bookingId);
                        if (hotelName != null) {
                            System.out.println("   Hotel: " + hotelName);
                            System.out.println("   Check-in: " + checkIn + " | Check-out: " + checkOut);
                        }
                        if (guideName != null) {
                            System.out.println("   Guide: " + guideName);
                        }
                        System.out.println("   Total: $" + price);
                        System.out.println("   Status: " + status);
                        System.out.println();
                        count++;
                    }

                    if (count == 1) {
                        System.out.println("No bookings found!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== SUBMIT RATING =====
    private static void submitRating() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RATE & REVIEW");
        System.out.println("=".repeat(60));
        System.out.println("What would you like to rate?");
        System.out.println("1. Hotel");
        System.out.println("2. Tourist Spot");
        System.out.println("3. Tour Guide");
        System.out.println("4. Back");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1":
                rateHotel();
                break;
            case "2":
                rateTouristSpot();
                break;
            case "3":
                rateGuide();
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    // ===== RATE HOTEL =====
    private static void rateHotel() {
        try {
            System.out.print("Hotel Name: ");
            String hotelName = sc.nextLine().trim();
            System.out.print("Rating (1-5): ");
            int rating = Integer.parseInt(sc.nextLine().trim());
            if (rating < 1 || rating > 5) {
                System.out.println("Invalid rating!");
                return;
            }
            System.out.print("Review (optional): ");
            String review = sc.nextLine().trim();

            String ratingId = UUID.randomUUID().toString().substring(0, 12);
            String sql = "INSERT INTO ratings (ratingId, userId, ratingType, targetName, rating, review) VALUES (?, ?, 'HOTEL', ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ratingId);
                pstmt.setString(2, currentUserId);
                pstmt.setString(3, hotelName);
                pstmt.setInt(4, rating);
                pstmt.setString(5, review);
                pstmt.executeUpdate();

                System.out.println("Rating submitted successfully!");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== RATE TOURIST SPOT =====
    private static void rateTouristSpot() {
        try {
            System.out.print("Spot Name: ");
            String spotName = sc.nextLine().trim();
            System.out.print("Rating (1-5): ");
            int rating = Integer.parseInt(sc.nextLine().trim());
            if (rating < 1 || rating > 5) {
                System.out.println("Invalid rating!");
                return;
            }
            System.out.print("Review (optional): ");
            String review = sc.nextLine().trim();

            String ratingId = UUID.randomUUID().toString().substring(0, 12);
            String sql = "INSERT INTO ratings (ratingId, userId, ratingType, targetName, rating, review) VALUES (?, ?, 'SPOT', ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ratingId);
                pstmt.setString(2, currentUserId);
                pstmt.setString(3, spotName);
                pstmt.setInt(4, rating);
                pstmt.setString(5, review);
                pstmt.executeUpdate();

                System.out.println("Rating submitted successfully!");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== RATE GUIDE =====
    private static void rateGuide() {
        try {
            System.out.print("Guide Name: ");
            String guideName = sc.nextLine().trim();
            System.out.print("Rating (1-5): ");
            int rating = Integer.parseInt(sc.nextLine().trim());
            if (rating < 1 || rating > 5) {
                System.out.println("Invalid rating!");
                return;
            }
            System.out.print("Review (optional): ");
            String review = sc.nextLine().trim();

            String ratingId = UUID.randomUUID().toString().substring(0, 12);
            String sql = "INSERT INTO ratings (ratingId, userId, ratingType, targetName, rating, review) VALUES (?, ?, 'GUIDE', ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, ratingId);
                pstmt.setString(2, currentUserId);
                pstmt.setString(3, guideName);
                pstmt.setInt(4, rating);
                pstmt.setString(5, review);
                pstmt.executeUpdate();

                System.out.println("Rating submitted successfully!");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== VIEW WEATHER INFO (FIXED) =====
    private static void viewWeatherInfo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("WEATHER INFORMATION");
        System.out.println("=".repeat(60));

        String sql = "SELECT division, temperature, `condition`, humidity, windSpeed FROM weather ORDER BY division";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-20s %7s %-15s %10s %12s%n",
                    "Division", "Temp", "Condition", "Humidity", "Wind");
            System.out.println("-".repeat(70));

            boolean any = false;

            while (rs.next()) {
                any = true;

                String division = rs.getString("division");
                double temp = rs.getDouble("temperature");
                String condition = rs.getString("condition");
                int humidity = rs.getInt("humidity");
                double windSpeed = rs.getDouble("windSpeed");

                System.out.printf("%-20s %6.1f°C %-15s %9d%% %10.1f km/h%n",
                        division, temp, condition, humidity, windSpeed);
            }

            if (!any) {
                System.out.println("No weather data found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== VIEW TOURIST PROFILE =====
    private static void viewTouristProfile() {
        try {
            String sql = "SELECT userName, userEmail, userPhone, country, address, dateOfBirth, registrationDate FROM users WHERE userID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n" + "=".repeat(60));
                        System.out.println("MY PROFILE");
                        System.out.println("=".repeat(60));
                        System.out.println("ID: " + currentUserId);
                        System.out.println("Name: " + rs.getString("userName"));
                        System.out.println("Email: " + rs.getString("userEmail"));
                        System.out.println("Phone: " + rs.getString("userPhone"));
                        System.out.println("Country: " + rs.getString("country"));
                        System.out.println("Address: " + rs.getString("address"));
                        System.out.println("Date of Birth: " + rs.getString("dateOfBirth"));
                        System.out.println("Registered: " + rs.getString("registrationDate"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== GUIDE MENU =====
    private static void guideMenu() {
        boolean inMenu = true;
        while (inMenu && currentUserId != null) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("GUIDE MENU");
            System.out.println("Welcome, " + currentUserName);
            System.out.println("=".repeat(60));
            System.out.println("1. View My Profile");
            System.out.println("2. Set Availability");
            System.out.println("3. View Booking Requests");
            System.out.println("4. Update Profile");
            System.out.println("5. View Ratings");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    viewGuideProfile();
                    break;
                case "2":
                    setGuideAvailability();
                    break;
                case "3":
                    viewGuideBookingRequests();
                    break;
                case "4":
                    updateGuideProfile();
                    break;
                case "5":
                    viewGuideRatings();
                    break;
                case "6":
                    logout();
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ===== VIEW GUIDE PROFILE =====
    private static void viewGuideProfile() {
        try {
            String sql = "SELECT guideName, guideEmail, guidePhone, guideDivision, guideDistrict, guideLanguage, specialization, yearExperience, rating FROM guides WHERE guideId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n" + "=".repeat(60));
                        System.out.println("GUIDE PROFILE");
                        System.out.println("=".repeat(60));
                        System.out.println("ID: " + currentUserId);
                        System.out.println("Name: " + rs.getString("guideName"));
                        System.out.println("Email: " + rs.getString("guideEmail"));
                        System.out.println("Phone: " + rs.getString("guidePhone"));
                        System.out.println("Location: " + rs.getString("guideDivision") + ", " + rs.getString("guideDistrict"));
                        System.out.println("Languages: " + rs.getString("guideLanguage"));
                        System.out.println("Specialization: " + rs.getString("specialization"));
                        System.out.println("Experience: " + rs.getInt("yearExperience") + " years");
                        System.out.println("Rating: " + rs.getDouble("rating") + "/5");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== SET GUIDE AVAILABILITY (FIXED) =====
    private static void setGuideAvailability() {
        System.out.print("Mark as available? (yes/no): ");
        String input = sc.nextLine().trim().toLowerCase();
        boolean available = input.equals("yes") || input.equals("y");

        try {
            String sql = "UPDATE guides SET isAvailable = ? WHERE guideId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, available);
                pstmt.setString(2, currentUserId);
                pstmt.executeUpdate();

                System.out.println("Availability updated to: " + (available ? "Available" : "Not Available"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== VIEW GUIDE BOOKING REQUESTS =====
    private static void viewGuideBookingRequests() {
        try {
            String sql = "SELECT b.bookingid, b.userId, u.userName, b.totalprice, b.bookingstatus, b.bookingdate FROM booking b JOIN users u ON b.userId = u.userID WHERE b.guidename = (SELECT guideName FROM guides WHERE guideId = ?) ORDER BY b.bookingdate DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("BOOKING REQUESTS");
                    System.out.println("=".repeat(60));
                    System.out.println();

                    int count = 1;
                    while (rs.next()) {
                        String bookingId = rs.getString("bookingid");
                        String userName = rs.getString("userName");
                        double price = rs.getDouble("totalprice");
                        String status = rs.getString("bookingstatus");

                        System.out.println(count + ". Booking ID: " + bookingId);
                        System.out.println("   Tourist: " + userName);
                        System.out.println("   Fee: $" + price);
                        System.out.println("   Status: " + status);
                        System.out.println();
                        count++;
                    }

                    if (count == 1) {
                        System.out.println("No booking requests!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== UPDATE GUIDE PROFILE =====
    private static void updateGuideProfile() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("UPDATE PROFILE");
        System.out.println("-".repeat(60));
        try {
            System.out.print("New Phone (press Enter to skip): ");
            String phone = sc.nextLine().trim();
            System.out.print("New Languages (press Enter to skip): ");
            String languages = sc.nextLine().trim();
            System.out.print("New Specialization (press Enter to skip): ");
            String specialization = sc.nextLine().trim();

            StringBuilder updateQuery = new StringBuilder("UPDATE guides SET ");
            List<String> updates = new ArrayList<>();

            if (!phone.isEmpty()) {
                updates.add("guidePhone = '" + phone + "'");
            }
            if (!languages.isEmpty()) {
                updates.add("guideLanguage = '" + languages + "'");
            }
            if (!specialization.isEmpty()) {
                updates.add("specialization = '" + specialization + "'");
            }

            if (updates.isEmpty()) {
                System.out.println("No updates provided!");
                return;
            }

            updateQuery.append(String.join(", ", updates));
            updateQuery.append(" WHERE guideId = '").append(currentUserId).append("'");

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(updateQuery.toString());
                System.out.println("Profile updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== VIEW GUIDE RATINGS =====
    private static void viewGuideRatings() {
        try {
            String sql = "SELECT rating, review, ratingDate FROM ratings WHERE ratingType = 'GUIDE' AND targetName = (SELECT guideName FROM guides WHERE guideId = ?) ORDER BY ratingDate DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("MY RATINGS");
                    System.out.println("=".repeat(60));
                    System.out.println();

                    int count = 1;
                    while (rs.next()) {
                        int rating = rs.getInt("rating");
                        String review = rs.getString("review");
                        String date = rs.getString("ratingDate");

                        System.out.println(count + ". Rating: " + rating + "/5");
                        if (review != null && !review.isEmpty()) {
                            System.out.println("   Review: " + review);
                        }
                        System.out.println("   Date: " + date);
                        System.out.println();
                        count++;
                    }

                    if (count == 1) {
                        System.out.println("No ratings yet!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== MANAGER MENU =====
    private static void managerMenu() {
        boolean inMenu = true;
        while (inMenu && currentUserId != null) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("MANAGER MENU");
            System.out.println("Welcome, " + currentUserName);
            System.out.println("=".repeat(60));
            System.out.println("1. View My Profile");
            System.out.println("2. Add New Hotel");
            System.out.println("3. View My Hotel");
            System.out.println("4. Add Room");
            System.out.println("5. Add Food Menu");
            System.out.println("6. View Bookings");
            System.out.println("7. Update Room Availability");
            System.out.println("8. View Statistics");
            System.out.println("9. Update Hotel Price");
            System.out.println("10. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    viewManagerProfile();
                    break;
                case "2":
                    addNewHotel();
                    break;
                case "3":
                    viewManagerHotel();
                    break;
                case "4":
                    addRoom();
                    break;
                case "5":
                    addFoodMenu();
                    break;
                case "6":
                    viewHotelBookings();
                    break;
                case "7":
                    updateRoomAvailability();
                    break;
                case "8":
                    viewHotelStatistics();
                    break;
                case "9":
                    updateHotelPrice();
                    break;
                case "10":
                case "0":
                    logout();
                    inMenu = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ===== VIEW MANAGER PROFILE =====
    private static void viewManagerProfile() {
        try {
            String sql = "SELECT managerName, managerEmail, managerPhone, hotelName, registrationNumber FROM managers WHERE managerId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("\n" + "=".repeat(60));
                        System.out.println("MANAGER PROFILE");
                        System.out.println("=".repeat(60));
                        System.out.println("ID: " + currentUserId);
                        System.out.println("Name: " + rs.getString("managerName"));
                        System.out.println("Email: " + rs.getString("managerEmail"));
                        System.out.println("Phone: " + rs.getString("managerPhone"));
                        System.out.println("Hotel Name: " + rs.getString("hotelName"));
                        System.out.println("Registration: " + rs.getString("registrationNumber"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== ADD NEW HOTEL =====
    private static void addNewHotel() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("ADD NEW HOTEL");
        System.out.println("-".repeat(60));
        try {
            System.out.print("Name: ");
            String hotelName = sc.nextLine().trim();
            System.out.print("Location: ");
            String location = sc.nextLine().trim();
            System.out.print("Price per Night: $");
            double price = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Initial Rating (0-5): ");
            double rating = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Total Rooms: ");
            int totalRooms = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Room Category: ");
            String category = sc.nextLine().trim();
            System.out.print("Features/Amenities: ");
            String features = sc.nextLine().trim();

            String hotelId = UUID.randomUUID().toString().substring(0, 12);
            String sql = "INSERT INTO hotels (hotelId, hotelName, hotelLocation, hotelPriceperNight, hotelrating, roomavailability, roomcategory, hotelFeatures, managerId, totalRooms) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, hotelId);
                pstmt.setString(2, hotelName);
                pstmt.setString(3, location);
                pstmt.setDouble(4, price);
                pstmt.setDouble(5, rating);
                pstmt.setInt(6, totalRooms);
                pstmt.setString(7, category);
                pstmt.setString(8, features);
                pstmt.setString(9, currentUserId);
                pstmt.setInt(10, totalRooms);
                pstmt.executeUpdate();

                System.out.println("Hotel added successfully!");
                System.out.println("Hotel ID: " + hotelId);
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== VIEW MANAGER HOTEL =====
    private static void viewManagerHotel() {
        try {
            String sql = "SELECT hotelId, hotelName, hotelLocation, hotelPriceperNight, hotelrating, roomavailability, totalRooms FROM hotels WHERE managerId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("MY HOTELS");
                    System.out.println("=".repeat(60));
                    System.out.println();

                    if (!rs.next()) {
                        System.out.println("No hotels registered!");
                        return;
                    }

                    do {
                        String name = rs.getString("hotelName");
                        String location = rs.getString("hotelLocation");
                        double price = rs.getDouble("hotelPriceperNight");
                        double rating = rs.getDouble("hotelrating");
                        int available = rs.getInt("roomavailability");
                        int total = rs.getInt("totalRooms");

                        System.out.println("Hotel: " + name);
                        System.out.println("Location: " + location);
                        System.out.println("Price per night: $" + price);
                        System.out.println("Rating: " + rating + "/5");
                        System.out.println("Rooms: " + available + "/" + total + " available");
                        System.out.println();
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== ADD ROOM =====
    private static void addRoom() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("ADD ROOM");
        System.out.println("-".repeat(60));
        try {
            // Get manager's hotel
            String hotelSql = "SELECT hotelId, hotelName FROM hotels WHERE managerId = ? LIMIT 1";
            String hotelId = null;
            try (PreparedStatement pstmt = conn.prepareStatement(hotelSql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        hotelId = rs.getString("hotelId");
                        System.out.println(rs.getString("hotelName"));
                    } else {
                        System.out.println("No hotel registered!");
                        return;
                    }
                }
            }

            System.out.print("Room Number: ");
            String roomNumber = sc.nextLine().trim();
            System.out.print("Room Type: ");
            String roomType = sc.nextLine().trim();
            System.out.print("Price: $");
            double price = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Capacity (guests): ");
            int capacity = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Amenities: ");
            String amenities = sc.nextLine().trim();

            String roomId = UUID.randomUUID().toString().substring(0, 12);
            String sql = "INSERT INTO rooms (roomId, hotelId, roomNumber, roomType, price, capacity, amenities) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, roomId);
                pstmt.setString(2, hotelId);
                pstmt.setString(3, roomNumber);
                pstmt.setString(4, roomType);
                pstmt.setDouble(5, price);
                pstmt.setInt(6, capacity);
                pstmt.setString(7, amenities);
                pstmt.executeUpdate();

                System.out.println("Room added successfully!");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== ADD FOOD MENU =====
    private static void addFoodMenu() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("ADD FOOD MENU");
        System.out.println("-".repeat(60));
        try {
            // Get manager's hotel
            String hotelSql = "SELECT hotelId, hotelName FROM hotels WHERE managerId = ? LIMIT 1";
            String hotelId = null;
            try (PreparedStatement pstmt = conn.prepareStatement(hotelSql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        hotelId = rs.getString("hotelId");
                        System.out.println(rs.getString("hotelName"));
                    } else {
                        System.out.println("No hotel registered!");
                        return;
                    }
                }
            }

            System.out.print("Item Name: ");
            String itemName = sc.nextLine().trim();
            System.out.print("Category: ");
            String category = sc.nextLine().trim();
            System.out.print("Price: $");
            double price = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Description: ");
            String description = sc.nextLine().trim();

            String menuId = UUID.randomUUID().toString().substring(0, 12);
            String sql = "INSERT INTO foodmenu (menuId, hotelId, itemName, category, price, description) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, menuId);
                pstmt.setString(2, hotelId);
                pstmt.setString(3, itemName);
                pstmt.setString(4, category);
                pstmt.setDouble(5, price);
                pstmt.setString(6, description);
                pstmt.executeUpdate();

                System.out.println("Menu item added successfully!");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== VIEW HOTEL BOOKINGS =====
    private static void viewHotelBookings() {
        try {
            String sql = "SELECT b.bookingid, u.userName, b.checkInDate, b.checkOutDate, b.numberOfRooms, b.totalprice, b.bookingstatus FROM booking b JOIN users u ON b.userId = u.userID WHERE b.hotelname IN (SELECT hotelName FROM hotels WHERE managerId = ?) ORDER BY b.bookingdate DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("HOTEL BOOKINGS");
                    System.out.println("=".repeat(60));
                    System.out.println();

                    int count = 1;
                    while (rs.next()) {
                        String bookingId = rs.getString("bookingid");
                        String userName = rs.getString("userName");
                        String checkIn = rs.getString("checkInDate");
                        String checkOut = rs.getString("checkOutDate");
                        int rooms = rs.getInt("numberOfRooms");
                        double price = rs.getDouble("totalprice");
                        String status = rs.getString("bookingstatus");

                        System.out.println(count + ". Booking ID: " + bookingId);
                        System.out.println("   Guest: " + userName);
                        System.out.println("   " + checkIn + " to " + checkOut);
                        System.out.println("   Rooms: " + rooms + " | Price: $" + price);
                        System.out.println("   Status: " + status);
                        System.out.println();
                        count++;
                    }

                    if (count == 1) {
                        System.out.println("No bookings!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== UPDATE ROOM AVAILABILITY =====
    private static void updateRoomAvailability() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("UPDATE ROOM AVAILABILITY");
        System.out.println("-".repeat(60));
        try {
            System.out.print("Number of available rooms: ");
            int rooms = Integer.parseInt(sc.nextLine().trim());

            String sql = "UPDATE hotels SET roomavailability = ? WHERE managerId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, rooms);
                pstmt.setString(2, currentUserId);
                int updated = pstmt.executeUpdate();

                if (updated > 0) {
                    System.out.println("Room availability updated!");
                } else {
                    System.out.println("Hotel not found!");
                }
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== VIEW HOTEL STATISTICS =====
    private static void viewHotelStatistics() {
        try {
            String sql = "SELECT hotelName, totalRooms, roomavailability, (SELECT COUNT(*) FROM booking WHERE hotelname = h.hotelName) as totalBookings, (SELECT SUM(totalprice) FROM booking WHERE hotelname = h.hotelName) as totalRevenue FROM hotels h WHERE managerId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, currentUserId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println("HOTEL STATISTICS");
                    System.out.println("=".repeat(60));
                    System.out.println();

                    if (!rs.next()) {
                        System.out.println("No hotel registered!");
                        return;
                    }

                    do {
                        String hotelName = rs.getString("hotelName");
                        int totalRooms = rs.getInt("totalRooms");
                        int available = rs.getInt("roomavailability");
                        int bookings = rs.getInt("totalBookings");
                        double revenue = rs.getDouble("totalRevenue");

                        int occupied = totalRooms - available;
                        double occupancyRate = (double) occupied / totalRooms * 100;

                        System.out.println("Hotel: " + hotelName);
                        System.out.println("Total Rooms: " + totalRooms);
                        System.out.println("Occupied: " + occupied + " | Available: " + available);
                        System.out.println("Occupancy Rate: " + String.format("%.1f", occupancyRate) + "%");
                        System.out.println("Total Bookings: " + bookings);
                        System.out.println("Total Revenue: $" + String.format("%.2f", revenue));
                        System.out.println();
                    } while (rs.next());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== UPDATE HOTEL PRICE =====
    private static void updateHotelPrice() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("UPDATE HOTEL PRICE");
        System.out.println("-".repeat(60));
        try {
            System.out.print("New price per night: $");
            double newPrice = Double.parseDouble(sc.nextLine().trim());

            String sql = "UPDATE hotels SET hotelPriceperNight = ? WHERE managerId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, newPrice);
                pstmt.setString(2, currentUserId);
                int updated = pstmt.executeUpdate();

                if (updated > 0) {
                    System.out.println("Price updated to $" + newPrice + "!");
                } else {
                    System.out.println("Hotel not found!");
                }
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ===== LOGOUT =====
    private static void logout() {
        System.out.println("Logging out...");
        currentUserId = null;
        currentUserRole = null;
        currentUserName = null;
        System.out.println("Logged out successfully!");
    }

    // ===== HELPER METHOD: CHECK IF EMAIL EXISTS =====
    private static boolean emailExists(String email, String table) throws SQLException {
        String emailCol = table.equals("users") ? "userEmail" : table.equals("guides") ? "guideEmail" : "managerEmail";
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + emailCol + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}