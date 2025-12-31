import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

public class ComfyGo {

    private static Connection conn;
    private static Scanner sc;

    // Session
    private static String currentUserId = null;
    private static String currentUserName = null;
    private static String currentUserRole = null; // TOURIST, GUIDE, MANAGER

    // Services
    private static AuthService authService;
    private static HotelService hotelService;
    private static ManagerService managerService;
    private static GuideService guideService;
    private static TouristSpotService spotService;
    private static TransportService transportService;
    private static RatingService ratingService;

    public static void main(String[] args) {
        try {
            conn = Db.getConnection();
            if (!Db.testConnection()) {
                System.out.println("Failed to connect to database!");
                return;
            }

            authService = new AuthService(conn);
            hotelService = new HotelService(conn);
            managerService = new ManagerService(conn);
            guideService = new GuideService(conn);
            spotService = new TouristSpotService(conn);
            transportService = new TransportService(conn);
            ratingService = new RatingService(conn);

            sc = new Scanner(System.in);
            mainMenu();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            if (sc != null) sc.close();
            Db.closeConnection();
            System.out.println("\nThank you for using ComfyGo! Goodbye!");
        }
    }

    // ===================== MAIN MENU =====================
    private static void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("WELCOME TO COMFYGO - TOURISM MANAGEMENT SYSTEM");
            System.out.println("=".repeat(70));

            if (currentUserId == null) {
                System.out.println("[MAIN MENU]");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Browse Tourist Spots (Guest)");
                System.out.println("4. Search Hotels (Guest)");
                System.out.println("5. View Weather");
                System.out.println("6. Exit");
                System.out.print("Choose option: ");

                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> registrationMenu();
                    case "2" -> loginMenu();
                    case "3" -> browseAllSpots();
                    case "4" -> searchHotelsGuest();
                    case "5" -> viewWeather();
                    case "6" -> running = false;
                    default -> System.out.println("Invalid choice!");
                }

            } else {
                switch (currentUserRole) {
                    case "TOURIST" -> touristMenu();
                    case "GUIDE" -> guideMenu();
                    case "MANAGER" -> managerMenu();
                    default -> {
                        System.out.println("Invalid role. Logging out...");
                        logout();
                    }
                }
            }
        }
    }

    // ===================== REGISTRATION =====================
    private static void registrationMenu() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("REGISTRATION");
        System.out.println("=".repeat(70));
        System.out.println("1. Register as Tourist");
        System.out.println("2. Register as Tour Guide");
        System.out.println("3. Register as Hotel Manager");
        System.out.println("4. Back");
        System.out.print("Choose role: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1" -> registerTourist();
            case "2" -> registerGuide();
            case "3" -> registerManager();
            case "4" -> { /* back */ }
            default -> System.out.println("Invalid choice!");
        }
    }

    private static void registerTourist() {
        System.out.println("\n" + "-".repeat(70));
        System.out.println("TOURIST REGISTRATION");
        System.out.println("-".repeat(70));

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Phone Number: ");
        String phone = sc.nextLine().trim();

        System.out.print("Country: ");
        String country = sc.nextLine().trim();

        System.out.print("NID/Passport Number: ");
        String nid = sc.nextLine().trim();

        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dob = sc.nextLine().trim();

        System.out.print("Address: ");
        String address = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        authService.registerTourist(name, email, phone, nid, "", dob, country, address, password);
    }

    private static void registerGuide() {
        System.out.println("\n" + "-".repeat(70));
        System.out.println("TOUR GUIDE REGISTRATION");
        System.out.println("-".repeat(70));

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Phone Number: ");
        String phone = sc.nextLine().trim();

        System.out.println("Divisions: Dhaka, Chittagong, Khulna, Rajshahi, Barisal, Sylhet, Rangpur, Mymensingh");
        System.out.print("Your Division: ");
        String division = sc.nextLine().trim();

        System.out.print("Your District: ");
        String district = sc.nextLine().trim();

        System.out.print("Languages (comma-separated): ");
        String languages = sc.nextLine().trim();

        System.out.print("Specialization (Beach/Nature/History/Adventure/etc.): ");
        String specialization = sc.nextLine().trim();

        int experience = readInt("Years of Experience: ");

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        authService.registerGuide(name, email, phone, division, district, languages, specialization, experience, password);
    }

    private static void registerManager() {
        System.out.println("\n" + "-".repeat(70));
        System.out.println("HOTEL MANAGER REGISTRATION");
        System.out.println("-".repeat(70));

        System.out.print("Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Phone Number: ");
        String phone = sc.nextLine().trim();

        System.out.print("Manager NID: ");
        String managerNid = sc.nextLine().trim();

        System.out.print("Hotel Name: ");
        String hotelName = sc.nextLine().trim();

        System.out.print("Hotel NID/License: ");
        String hotelNid = sc.nextLine().trim();

        System.out.print("Registration Number: ");
        String regNumber = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        authService.registerManager(name, email, phone, managerNid, hotelName, hotelNid, regNumber, password);
    }

    // ===================== LOGIN =====================
    private static void loginMenu() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("LOGIN");
        System.out.println("=".repeat(70));
        System.out.println("1. Login as Tourist");
        System.out.println("2. Login as Tour Guide");
        System.out.println("3. Login as Hotel Manager");
        System.out.println("4. Back");
        System.out.print("Choose role: ");

        String choice = sc.nextLine().trim();
        if ("4".equals(choice)) return;

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        switch (choice) {
            case "1" -> {
                User user = authService.loginTourist(email, password);
                if (user != null) {
                    currentUserId = user.getUserID();
                    currentUserName = user.getUserName();
                    currentUserRole = "TOURIST";
                    System.out.println("Login successful! Welcome " + currentUserName);
                } else {
                    System.out.println("Login failed!");
                }
            }
            case "2" -> {
                Guide guide = authService.loginGuide(email, password);
                if (guide != null) {
                    currentUserId = guide.getGuideId();
                    currentUserName = guide.getGuideName();
                    currentUserRole = "GUIDE";
                    System.out.println("Login successful! Welcome " + currentUserName);
                } else {
                    System.out.println("Login failed!");
                }
            }
            case "3" -> {
                Manager manager = authService.loginManager(email, password);
                if (manager != null) {
                    currentUserId = manager.getManagerId();
                    currentUserName = manager.getManagerName();
                    currentUserRole = "MANAGER";
                    System.out.println("Login successful! Welcome " + currentUserName);
                } else {
                    System.out.println("Login failed!");
                }
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    // ===================== TOURIST =====================
    private static void touristMenu() {
        boolean inMenu = true;
        while (inMenu && currentUserId != null) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("TOURIST MENU - " + (currentUserName == null ? "" : currentUserName.toUpperCase()));
            System.out.println("=".repeat(70));
            System.out.println("1. Browse Tourist Spots");
            System.out.println("2. Search & Book Hotels");
            System.out.println("3. Book Transport Tickets");
            System.out.println("4. Hire Tour Guide");
            System.out.println("5. View My Bookings (Hotel + Transport)");
            System.out.println("6. Rate & Review");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> browseAllSpots();
                case "2" -> searchAndBookHotels();
                case "3" -> bookTransportTicket();
                case "4" -> hireGuide();
                case "5" -> viewMyBookings();
                case "6" -> submitRating();
                case "7" -> { logout(); inMenu = false; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void browseAllSpots() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TOURIST SPOTS BY DIVISION");
        System.out.println("=".repeat(70));

        List<String> divisions = spotService.getAllDivisions();
        if (divisions == null || divisions.isEmpty()) {
            System.out.println("No divisions available!");
            return;
        }

        System.out.println("Available Divisions:");
        for (int i = 0; i < divisions.size(); i++) System.out.println((i + 1) + ". " + divisions.get(i));

        int choice = readInt("Choose division number (0 to back): ");
        if (choice == 0) return;

        if (choice < 1 || choice > divisions.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        String selectedDivision = divisions.get(choice - 1);
        spotService.listSpotsByDivision(selectedDivision);

        int spotChoice = readInt("Enter spot number to view details (0 to back): ");
        if (spotChoice == 0) return;

        List<TouristSpot> spots = spotService.searchSpotsByDivision(selectedDivision);
        if (spots == null || spotChoice < 1 || spotChoice > spots.size()) {
            System.out.println("Invalid spot choice!");
            return;
        }

        spotService.displaySpotDetails(spots.get(spotChoice - 1));
    }

    private static void searchAndBookHotels() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("HOTEL SEARCH & BOOKING");
        System.out.println("=".repeat(70));

        System.out.print("Enter location (or press Enter for all): ");
        String location = sc.nextLine().trim();

        List<Hotel> hotels = location.isEmpty()
                ? hotelService.getAllHotels()
                : hotelService.searchHotelsByLocation(location);

        if (hotels == null || hotels.isEmpty()) {
            System.out.println("No hotels found!");
            return;
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.printf("%-3s | %-28s | %-18s | %-12s | %-6s | %-9s%n",
                "No", "Hotel Name", "Location", "Price/Night", "Rate", "Available");
        System.out.println("-".repeat(95));

        for (int i = 0; i < hotels.size(); i++) {
            Hotel h = hotels.get(i);
            System.out.printf("%-3d | %-28s | %-18s | BDT %-9.0f | %-6.1f | %-9d%n",
                    (i + 1), h.getHotelName(), h.getHotelLocation(),
                    h.getPricePerNight(), h.getRating(), h.getRoomAvailability());
        }
        System.out.println("=".repeat(95));

        int choice = readInt("Choose hotel number (0 to back): ");
        if (choice == 0) return;

        if (choice < 1 || choice > hotels.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Hotel selectedHotel = hotels.get(choice - 1);
        hotelService.displayHotelInfo(selectedHotel);

        System.out.print("Proceed with booking? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) return;

        System.out.print("Check-in date (YYYY-MM-DD): ");
        String checkIn = sc.nextLine().trim();

        System.out.print("Check-out date (YYYY-MM-DD): ");
        String checkOut = sc.nextLine().trim();

        int numRooms = readInt("Number of rooms: ");

        long nights;
        try {
            LocalDate ci = LocalDate.parse(checkIn);
            LocalDate co = LocalDate.parse(checkOut);
            nights = ChronoUnit.DAYS.between(ci, co);
        } catch (Exception e) {
            System.out.println("Invalid date format. Use YYYY-MM-DD.");
            return;
        }

        if (nights <= 0) {
            System.out.println("Check-out date must be after check-in date!");
            return;
        }

        double totalPrice = selectedHotel.getPricePerNight() * numRooms * nights;

        System.out.println("\nPAYMENT");
        System.out.println("Nights: " + nights);
        System.out.println("Total to pay: BDT " + Math.round(totalPrice));

        System.out.print("Payment method (Cash/Bkash/Nagad/Card): ");
        String method = sc.nextLine().trim();
        if (method.isEmpty()) method = "Cash";

        System.out.println("Note: If paid amount < total price, payment will be CANCELLED and booking will not be created.");
        double paidAmount = readDouble("Enter paid amount: ");

        hotelService.bookHotelWithPayment(
                currentUserId,
                selectedHotel.getHotelId(),
                checkIn,
                checkOut,
                numRooms,
                totalPrice,
                method,
                paidAmount
        );
    }

    private static void searchHotelsGuest() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("HOTEL SEARCH (GUEST)");
        System.out.println("=".repeat(70));

        List<Hotel> allHotels = hotelService.getAllHotels();
        if (allHotels == null || allHotels.isEmpty()) {
            System.out.println("No hotels available!");
            return;
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.printf("%-3s | %-28s | %-18s | %-12s | %-6s | %-9s%n",
                "No", "Hotel Name", "Location", "Price/Night", "Rate", "Available");
        System.out.println("-".repeat(95));

        for (int i = 0; i < allHotels.size(); i++) {
            Hotel h = allHotels.get(i);
            System.out.printf("%-3d | %-28s | %-18s | BDT %-9.0f | %-6.1f | %-9d%n",
                    (i + 1), h.getHotelName(), h.getHotelLocation(),
                    h.getPricePerNight(), h.getRating(), h.getRoomAvailability());
        }
        System.out.println("=".repeat(95));
        System.out.println("Login to book a hotel.");
    }

    private static void viewMyBookings() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("MY BOOKINGS (HOTEL + TRANSPORT)");
        System.out.println("=".repeat(70));

        hotelService.displayUserHotelBookings(currentUserId);

        List<String> tBookings = transportService.getUserTransportBookings(currentUserId);
        System.out.println("\n" + "=".repeat(80));
        System.out.println("MY TRANSPORT BOOKINGS");
        System.out.println("=".repeat(80));

        if (tBookings == null || tBookings.isEmpty()) {
            System.out.println("No transport bookings found!");
        } else {
            for (String b : tBookings) System.out.println(b);
        }

        System.out.println("=".repeat(80));
        System.out.println("\nCANCEL OPTIONS");
        System.out.println("1. Cancel a Hotel Booking");
        System.out.println("2. Cancel a Transport Ticket");
        System.out.println("3. Back");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("Enter Booking ID to cancel: ");
                String bookingId = sc.nextLine().trim();
                hotelService.cancelHotelBookingForUser(currentUserId, bookingId);
            }
            case "2" -> {
                System.out.print("Enter Ticket ID to cancel: ");
                String ticketId = sc.nextLine().trim();
                transportService.cancelTicket(currentUserId, ticketId);
            }
            case "3" -> { /* back */ }
            default -> System.out.println("Invalid choice!");
        }
    }

    private static void bookTransportTicket() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TRANSPORT BOOKING");
        System.out.println("=".repeat(70));

        transportService.displayAvailableRoutes();

        System.out.print("Transport type (Bus/Train/Launch/Air): ");
        String transportType = sc.nextLine().trim();

        System.out.print("Departure location: ");
        String departure = sc.nextLine().trim();

        System.out.print("Arrival location: ");
        String arrival = sc.nextLine().trim();

        System.out.print("Departure date (YYYY-MM-DD): ");
        String depDate = sc.nextLine().trim();

        System.out.print("Issue date (YYYY-MM-DD): ");
        String issueDate = sc.nextLine().trim();

        int passengers = readInt("Number of passengers: ");

        System.out.print("Seat number/Cabin: ");
        String seat = sc.nextLine().trim();

        double fare = readDouble("Fare per passenger (BDT): ");

        System.out.print("Vehicle registration number: ");
        String regNo = sc.nextLine().trim();

        System.out.print("Vehicle/Company name: ");
        String company = sc.nextLine().trim();

        transportService.bookTransport(
                currentUserId, transportType, departure, arrival,
                depDate, issueDate, passengers, seat, fare, regNo, company
        );
    }

    private static void hireGuide() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("HIRE A TOUR GUIDE");
        System.out.println("=".repeat(70));

        List<Guide> guides = guideService.getAvailableGuides();
        if (guides == null || guides.isEmpty()) {
            System.out.println("No available guides right now!");
            return;
        }

        for (int i = 0; i < guides.size(); i++) {
            Guide g = guides.get(i);
            System.out.println((i + 1) + ". " + g.getGuideName() + " | " + g.getSpecialization() + " | Rating: " + g.getRating());
        }

        int choice = readInt("Enter guide number to hire (0 to back): ");
        if (choice == 0) return;

        if (choice < 1 || choice > guides.size()) {
            System.out.println("Invalid choice!");
            return;
        }

        Guide selectedGuide = guides.get(choice - 1);
        guideService.displayGuideInfo(selectedGuide);

        System.out.print("Proceed with hiring? (y/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("y")) return;

        System.out.print("Tour location: ");
        String location = sc.nextLine().trim();

        int days = readInt("Tour duration (days): ");

        System.out.print("Tour purpose: ");
        String purpose = sc.nextLine().trim();

        guideService.hireGuide(currentUserId, selectedGuide.getGuideId(), location, days, purpose);
    }

    private static void submitRating() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("RATE & REVIEW");
        System.out.println("=".repeat(70));
        System.out.println("1. Rate a Hotel");
        System.out.println("2. Rate a Tourist Spot");
        System.out.println("3. Rate a Tour Guide");
        System.out.println("4. Back");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        if ("4".equals(choice)) return;

        System.out.print("Name of entity: ");
        String entityName = sc.nextLine().trim();

        int rating = readInt("Rating (1-5): ");

        System.out.print("Review text (optional): ");
        String review = sc.nextLine().trim();

        switch (choice) {
            case "1" -> ratingService.rateHotel(currentUserId, entityName, rating, review);
            case "2" -> ratingService.rateTouristSpot(currentUserId, entityName, rating, review);
            case "3" -> ratingService.rateGuide(currentUserId, entityName, rating, review);
            default -> System.out.println("Invalid choice!");
        }
    }

    private static void viewWeather() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("WEATHER INFORMATION (Sample)");
        System.out.println("=".repeat(70));

        System.out.printf("%-15s | %-12s | %-10s | %-8s | %-8s%n",
                "Division", "Temperature", "Condition", "Humidity", "Wind");
        System.out.println("-".repeat(70));

        String[][] weatherData = {
                {"Dhaka", "22.5°C", "Clear", "65%", "6 km/h"},
                {"Chittagong", "23.5°C", "Cloudy", "68%", "8.5 km/h"},
                {"Khulna", "20.5°C", "Clear", "64%", "7 km/h"},
                {"Sylhet", "18.5°C", "Cloudy", "70%", "5 km/h"},
                {"Cox's Bazar", "24.0°C", "Sunny", "65%", "9 km/h"}
        };

        for (String[] row : weatherData) {
            System.out.printf("%-15s | %-12s | %-10s | %-8s | %-8s%n",
                    row[0], row[1], row[2], row[3], row[4]);
        }

        System.out.println("=".repeat(70));
    }

    // ===================== GUIDE =====================
    private static void guideMenu() {
        boolean inMenu = true;
        while (inMenu && currentUserId != null) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("GUIDE MENU - " + (currentUserName == null ? "" : currentUserName.toUpperCase()));
            System.out.println("=".repeat(70));
            System.out.println("1. View Profile");
            System.out.println("2. Update Availability");
            System.out.println("3. View My Bookings");
            System.out.println("4. Manage Booking Status");
            System.out.println("5. View Ratings");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println("[Your Profile]");
                    System.out.println("Guide ID: " + currentUserId);
                    System.out.println("Name: " + currentUserName);
                }
                case "2" -> {
                    System.out.print("Set availability (y/n): ");
                    boolean available = sc.nextLine().trim().equalsIgnoreCase("y");
                    guideService.setGuideAvailability(currentUserId, available);
                }
                case "3" -> guideService.displayGuideBookings(currentUserId);
                case "4" -> manageGuideBookings();
                case "5" -> ratingService.displayRatings("GUIDE", currentUserName);
                case "6" -> { logout(); inMenu = false; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void manageGuideBookings() {
        guideService.displayGuideBookings(currentUserId);

        System.out.println("\nGUIDE BOOKING ACTIONS");
        System.out.println("1. Update Tour Status (PENDING/CONFIRMED/REJECTED/COMPLETED/CANCELLED)");
        System.out.println("2. Update Payment Status (PENDING/COMPLETED/FAILED/REFUNDED)");
        System.out.println("3. Back");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("Enter Booking ID: ");
                String bookingId = sc.nextLine().trim();
                System.out.print("Enter new tour status: ");
                String status = sc.nextLine().trim();
                guideService.updateTourStatusForGuide(currentUserId, bookingId, status);
            }
            case "2" -> {
                System.out.print("Enter Booking ID: ");
                String bookingId = sc.nextLine().trim();
                System.out.print("Enter new payment status: ");
                String status = sc.nextLine().trim();
                guideService.updatePaymentStatusForGuide(currentUserId, bookingId, status);
            }
            case "3" -> { /* back */ }
            default -> System.out.println("Invalid choice!");
        }
    }

    // ===================== MANAGER =====================
    private static void managerMenu() {
        boolean inMenu = true;
        while (inMenu && currentUserId != null) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("MANAGER MENU - " + (currentUserName == null ? "" : currentUserName.toUpperCase()));
            System.out.println("=".repeat(70));
            System.out.println("1. View My Hotel");
            System.out.println("2. Add My Hotel");
            System.out.println("3. Manage Food Items");
            System.out.println("4. Update Room Availability");
            System.out.println("5. Update Hotel Price");
            System.out.println("6. View Bookings");
            System.out.println("7. Manage Booking Status");
            System.out.println("8. View Hotel Ratings");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> hotelService.displayManagerHotel(currentUserId);
                case "2" -> addMyHotelUI();
                case "3" -> manageFoodUI();
                case "4" -> {
                    int availability = readInt("Enter new room availability: ");
                    boolean ok = hotelService.updateRoomAvailabilityForManager(currentUserId, availability);
                    if (ok) System.out.println("Room availability updated.");
                }
                case "5" -> {
                    double newPrice = readDouble("Enter new price per night: ");
                    boolean ok = hotelService.updateHotelPriceForManager(currentUserId, newPrice);
                    if (ok) System.out.println("Hotel price updated.");
                }
                case "6" -> hotelService.displayManagerHotelBookings(currentUserId);
                case "7" -> manageManagerBookings();
                case "8" -> {
                    Hotel myHotel = hotelService.getHotelByManagerId(currentUserId);
                    if (myHotel == null) System.out.println("No hotel found for this manager!");
                    else ratingService.displayRatings("HOTEL", myHotel.getHotelName());
                }
                case "9" -> { logout(); inMenu = false; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void addMyHotelUI() {
        Hotel existing = hotelService.getHotelByManagerId(currentUserId);
        if (existing != null) {
            System.out.println("You already have a hotel in the database.");
            hotelService.displayHotelInfo(existing);
            return;
        }

        System.out.print("Hotel name: ");
        String name = sc.nextLine().trim();

        System.out.print("Location: ");
        String location = sc.nextLine().trim();

        double price = readDouble("Price per night (BDT): ");

        int totalRooms = readInt("Total rooms: ");

        System.out.print("Room category: ");
        String category = sc.nextLine().trim();

        System.out.print("Features (comma separated): ");
        String features = sc.nextLine().trim();

        managerService.addHotel(currentUserId, name, location, price, totalRooms, category, features);
    }

    private static void manageFoodUI() {
        boolean loop = true;
        while (loop) {
            hotelService.displayFoodMenuForManager(currentUserId);

            System.out.println("FOOD MENU ACTIONS");
            System.out.println("1. Add item");
            System.out.println("2. Update item");
            System.out.println("3. Delete item");
            System.out.println("4. Back");
            System.out.print("Choose: ");

            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> {
                    System.out.print("Food name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Category: ");
                    String cat = sc.nextLine().trim();
                    double price = readDouble("Price (BDT): ");
                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();
                    System.out.print("Available? (y/n): ");
                    boolean av = sc.nextLine().trim().equalsIgnoreCase("y");
                    hotelService.addFoodItemForManager(currentUserId, name, cat, price, desc, av);
                }
                case "2" -> {
                    System.out.print("MenuID: ");
                    String menuId = sc.nextLine().trim();
                    System.out.print("Food name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Category: ");
                    String cat = sc.nextLine().trim();
                    double price = readDouble("Price (BDT): ");
                    System.out.print("Description: ");
                    String desc = sc.nextLine().trim();
                    System.out.print("Available? (y/n): ");
                    boolean av = sc.nextLine().trim().equalsIgnoreCase("y");
                    hotelService.updateFoodItemForManager(currentUserId, menuId, name, cat, price, desc, av);
                }
                case "3" -> {
                    System.out.print("MenuID: ");
                    String menuId = sc.nextLine().trim();
                    hotelService.deleteFoodItemForManager(currentUserId, menuId);
                }
                case "4" -> loop = false;
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void manageManagerBookings() {
        hotelService.displayManagerHotelBookings(currentUserId);

        System.out.println("\nMANAGER BOOKING ACTIONS");
        System.out.println("1. Update Booking Status (PENDING/CONFIRMED/COMPLETED/FAILED/CANCELLED)");
        System.out.println("2. Cancel Booking");
        System.out.println("3. Back");
        System.out.print("Choose option: ");

        String choice = sc.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("Enter Booking ID: ");
                String bookingId = sc.nextLine().trim();
                System.out.print("Enter new status: ");
                String status = sc.nextLine().trim();
                hotelService.updateBookingStatusForManager(currentUserId, bookingId, status);
            }
            case "2" -> {
                System.out.print("Enter Booking ID to cancel: ");
                String bookingId = sc.nextLine().trim();
                hotelService.cancelHotelBookingForManager(currentUserId, bookingId);
            }
            case "3" -> { /* back */ }
            default -> System.out.println("Invalid choice!");
        }
    }

    // ===================== HELPERS =====================
    private static void logout() {
        currentUserId = null;
        currentUserName = null;
        currentUserRole = null;
        System.out.println("Logged out successfully!");
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount, try again.");
            }
        }
    }
}