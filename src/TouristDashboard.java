import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class TouristDashboard extends JPanel {

    private final ComfyGoGUI mainFrame;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;

    public TouristDashboard(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.LIGHT);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ComfyGoGUI.LIGHT);

        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(createHotelsPanel(), "HOTELS");
        contentPanel.add(createSpotsPanel(), "SPOTS");
        contentPanel.add(createGuidesPanel(), "GUIDES");
        contentPanel.add(createTransportPanel(), "TRANSPORT");
        contentPanel.add(createBookingsPanel(), "BOOKINGS");
        contentPanel.add(createRatingsPanel(), "RATINGS");

        add(contentPanel, BorderLayout.CENTER);
        contentLayout.show(contentPanel, "HOME");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ComfyGoGUI.PRIMARY);
        sidebar.setPreferredSize(new Dimension(260, 750));
        sidebar.setBorder(new EmptyBorder(18, 16, 18, 16));

        JLabel userLabel = new JLabel(mainFrame.getCurrentUserName() == null ? "Tourist" : mainFrame.getCurrentUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Tourist");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(220, 245, 235));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(userLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 14)));

        sidebar.add(navBtn("🏠 Dashboard", "HOME"));
        sidebar.add(navBtn("🏨 Hotels", "HOTELS"));
        sidebar.add(navBtn("🗺️ Tourist Spots", "SPOTS"));
        sidebar.add(navBtn("👤 Tour Guides", "GUIDES"));
        sidebar.add(navBtn("🚌 Transport", "TRANSPORT"));
        sidebar.add(navBtn("📋 My Bookings", "BOOKINGS"));
        sidebar.add(navBtn("⭐ Rate & Review", "RATINGS"));

        sidebar.add(Box.createVerticalGlue());

        JButton logout = ComfyGoGUI.createStyledButton("Logout", ComfyGoGUI.DANGER);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> mainFrame.logout());
        sidebar.add(logout);

        return sidebar;
    }

    private JButton navBtn(String text, String panel) {
        JButton btn = ComfyGoGUI.createStyledButton(text, new Color(34, 120, 82));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 48));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ComfyGoGUI.SUCCESS);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(34, 120, 82));
            }
        });
        
        btn.addActionListener(e -> contentLayout.show(contentPanel, panel));
        return btn;
    }

    // ---------- Screen shell ----------
    private JPanel screenShell(String titleText) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(ComfyGoGUI.LIGHT);
        shell.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ComfyGoGUI.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 227, 217), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ComfyGoGUI.PRIMARY);

        header.add(title, BorderLayout.WEST);
        shell.add(header, BorderLayout.NORTH);
        return shell;
    }

    // ---------- HOME ----------
    private JPanel createHomePanel() {
        JPanel shell = screenShell("Dashboard");

        JPanel body = new JPanel(new GridLayout(2, 2, 14, 14));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        body.add(statCard("Hotels", "Browse & book hotels", ComfyGoGUI.PRIMARY));
        body.add(statCard("Tourist Spots", "Explore top places", ComfyGoGUI.SUCCESS));
        body.add(statCard("Tour Guides", "Hire available guides", ComfyGoGUI.WARNING));
        body.add(statCard("Transport", "View routes & options", ComfyGoGUI.DANGER));

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel statCard(String title, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ComfyGoGUI.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 227, 217), 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(accent);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(new Color(70, 90, 80));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(t);
        left.add(Box.createRigidArea(new Dimension(0, 6)));
        left.add(s);

        card.add(left, BorderLayout.CENTER);
        return card;
    }

    // ---------- HOTELS ----------
    private JPanel createHotelsPanel() {
        JPanel shell = screenShell("Search & Book Hotels");

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel searchCard = ComfyGoGUI.cardPanel();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JTextField locationField = ComfyGoGUI.createStyledTextField();
        locationField.setPreferredSize(new Dimension(320, 42));

        JButton searchBtn = ComfyGoGUI.createStyledButton("Search", ComfyGoGUI.LIME);
        JButton showAllBtn = ComfyGoGUI.createStyledButton("Show All", ComfyGoGUI.SECONDARY);

        searchCard.add(ComfyGoGUI.label("Location"));
        searchCard.add(locationField);
        searchCard.add(searchBtn);
        searchCard.add(showAllBtn);

        String[] columns = {"Hotel Name", "Location", "Price/Night", "Rating", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = makeTable(model);

        JScrollPane tableScroll = ComfyGoGUI.scrollWrap(table);
        tableScroll.getViewport().setBackground(ComfyGoGUI.SURFACE);

        JPanel tableCard = ComfyGoGUI.cardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(tableScroll, BorderLayout.CENTER);

        JButton bookBtn = ComfyGoGUI.createStyledButton("Book Selected Hotel", ComfyGoGUI.SUCCESS);
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionRow.setOpaque(false);
        actionRow.add(bookBtn);
        tableCard.add(actionRow, BorderLayout.SOUTH);

        Runnable loadAll = () -> updateHotelTable(model, mainFrame.getHotelService().getAllHotels());
        Runnable doSearch = () -> updateHotelTable(model, mainFrame.getHotelService().searchHotelsByLocation(locationField.getText().trim()));

        loadAll.run();
        searchBtn.addActionListener(e -> doSearch.run());
        showAllBtn.addActionListener(e -> loadAll.run());

        bookBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a hotel!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String hotelName = String.valueOf(model.getValueAt(row, 0));
            showHotelBookingDialog(hotelName);
        });

        body.add(searchCard, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private void updateHotelTable(DefaultTableModel model, List<Hotel> hotels) {
        model.setRowCount(0);
        for (Hotel h : hotels) {
            model.addRow(new Object[]{
                    h.getHotelName(),
                    h.getHotelLocation(),
                    "BDT " + h.getPricePerNight(),
                    h.getRating() + "/5",
                    h.getRoomAvailability()
            });
        }
    }

    // ---------- SPOTS ----------
    private JPanel createSpotsPanel() {
        JPanel shell = screenShell("Explore Tourist Spots");

        List<TouristSpot> spots = mainFrame.getSpotService().getAllSpots();
        String[] columns = {"Spot Name", "Division", "District", "Entry Fee", "Rating"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (TouristSpot s : spots) {
            model.addRow(new Object[]{
                    s.getSpotName(),
                    s.getDivision(),
                    s.getDistrict(),
                    "BDT " + s.getEntryFee(),
                    s.getRating() + "/5"
            });
        }

        JTable table = makeTable(model);
        JScrollPane sp = ComfyGoGUI.scrollWrap(table);

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(sp, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));
        body.add(card, BorderLayout.CENTER);

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // ---------- GUIDES ----------
    private JPanel createGuidesPanel() {
        JPanel shell = screenShell("Hire Tour Guides");

        List<Guide> guides = mainFrame.getGuideService().getAvailableGuides();
        String[] columns = {"Guide Name", "Specialization", "Division", "Experience", "Rating"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (Guide g : guides) {
            model.addRow(new Object[]{
                    g.getGuideName(),
                    g.getSpecialization(),
                    g.getGuideDivision(),
                    g.getYearExperience() + " years",
                    g.getRating() + "/5"
            });
        }

        JTable table = makeTable(model);
        JScrollPane sp = ComfyGoGUI.scrollWrap(table);

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(sp, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));
        body.add(card, BorderLayout.CENTER);

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // ---------- TRANSPORT ----------
    private JPanel createTransportPanel() {
        JPanel shell = screenShell("Transport Routes");

        JTextArea routesArea = new JTextArea(18, 60);
        routesArea.setEditable(false);
        routesArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        routesArea.setBackground(new Color(252, 254, 253));
        routesArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        List<String> routes = mainFrame.getTransportService().getAllRoutes();
        StringBuilder sb = new StringBuilder();
        for (String r : routes) sb.append(r).append("\n");
        routesArea.setText(sb.toString());

        JScrollPane sp = ComfyGoGUI.scrollWrap(routesArea);

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(sp, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));
        body.add(card, BorderLayout.CENTER);

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // ---------- BOOKINGS ----------
    private JPanel createBookingsPanel() {
        JPanel shell = screenShell("My Bookings");
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Create tabs for different booking types
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(ComfyGoGUI.SURFACE);

        // Hotel Bookings Tab
        tabs.addTab("🏨 Hotels", createHotelBookingsTable());

        // Transport Bookings Tab
        tabs.addTab("🚌 Transport", createTransportBookingsTable());

        // Guide Bookings Tab
        tabs.addTab("👤 Guides", createGuideBookingsTable());

        body.add(tabs, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createHotelBookingsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ComfyGoGUI.SURFACE);

        String[] columns = {"Booking ID", "Hotel", "Location", "Check-In", "Check-Out", "Rooms", "Total (BDT)", "Status", "Payment", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Only Actions column editable
            }
        };

        // Fetch bookings from database
        List<Booking> bookings = Booking.getBookingsByUser(mainFrame.getCurrentUserId(), mainFrame.getConnection());

        for (Booking b : bookings) {
            model.addRow(new Object[]{
                    b.getBookingId(),
                    b.getHotelName(),
                    b.getHotelLocation(),
                    b.getCheckInDate(),
                    b.getCheckOutDate(),
                    b.getNumberOfRooms(),
                    String.format("%.2f", b.getTotalPrice()),
                    b.getBookingStatus(),
                    b.getPaymentId() != null ? "✓ Paid" : "✗ Unpaid",
                    "View/Cancel"
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setBackground(new Color(252, 254, 253));
        table.setGridColor(new Color(225, 235, 230));
        table.setSelectionBackground(new Color(46, 204, 113, 50));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(ComfyGoGUI.VIBRANT_BLUE);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Add action buttons in table
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), mainFrame, model));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add refresh button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);
        JButton refreshBtn = ComfyGoGUI.createStyledButton("🔄 Refresh", ComfyGoGUI.VIBRANT_BLUE);
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<Booking> updatedBookings = Booking.getBookingsByUser(mainFrame.getCurrentUserId(), mainFrame.getConnection());
            for (Booking b : updatedBookings) {
                model.addRow(new Object[]{
                        b.getBookingId(), b.getHotelName(), b.getHotelLocation(),
                        b.getCheckInDate(), b.getCheckOutDate(), b.getNumberOfRooms(),
                        String.format("%.2f", b.getTotalPrice()), b.getBookingStatus(),
                        b.getPaymentId() != null ? "✓ Paid" : "✗ Unpaid", "View/Cancel"
                });
            }
        });
        actionPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransportBookingsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ComfyGoGUI.SURFACE);

        String[] columns = {"Ticket ID", "Type", "From", "To", "Date", "Passengers", "Fare (BDT)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Fetch transport bookings from database
        String sql = "SELECT ticketid, transporttype, departurelocation, arrivallocation, " +
                "departuredate, numberofpassengers, fare, bookingstatus " +
                "FROM transportbooking WHERE userid = ? ORDER BY departuredate DESC";

        try (java.sql.PreparedStatement ps = mainFrame.getConnection().prepareStatement(sql)) {
            ps.setString(1, mainFrame.getCurrentUserId());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("ticketid"),
                            rs.getString("transporttype"),
                            rs.getString("departurelocation"),
                            rs.getString("arrivallocation"),
                            rs.getDate("departuredate"),
                            rs.getInt("numberofpassengers"),
                            String.format("%.2f", rs.getDouble("fare")),
                            rs.getString("bookingstatus")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading transport bookings: " + e.getMessage());
        }

        JTable table = new JTable(model);
        styleTable(table, ComfyGoGUI.VIBRANT_ORANGE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add refresh button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);
        JButton refreshBtn = ComfyGoGUI.createStyledButton("🔄 Refresh", ComfyGoGUI.VIBRANT_ORANGE);
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try (java.sql.PreparedStatement ps = mainFrame.getConnection().prepareStatement(sql)) {
                ps.setString(1, mainFrame.getCurrentUserId());
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("ticketid"), rs.getString("transporttype"),
                                rs.getString("departurelocation"), rs.getString("arrivallocation"),
                                rs.getDate("departuredate"), rs.getInt("numberofpassengers"),
                                String.format("%.2f", rs.getDouble("fare")), rs.getString("bookingstatus")
                        });
                    }
                }
            } catch (Exception ex) {
                System.out.println("Error refreshing: " + ex.getMessage());
            }
        });
        actionPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createGuideBookingsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ComfyGoGUI.SURFACE);

        String[] columns = {"Booking ID", "Guide Name", "Duration (days)", "Location", "Fee (BDT)", "Status", "Payment"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Fetch guide bookings from database
        String sql = "SELECT gb.bookingid, g.guidename, gb.tourdurationdays, gb.tourlocation, " +
                "gb.guidefee, gb.tourstatus, gb.paymentstatus " +
                "FROM guidebooking gb " +
                "LEFT JOIN guides g ON gb.guideid = g.guideid " +
                "WHERE gb.userid = ? ORDER BY gb.bookingdate DESC";

        try (java.sql.PreparedStatement ps = mainFrame.getConnection().prepareStatement(sql)) {
            ps.setString(1, mainFrame.getCurrentUserId());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("bookingid"),
                            rs.getString("guidename"),
                            rs.getInt("tourdurationdays"),
                            rs.getString("tourlocation"),
                            String.format("%.2f", rs.getDouble("guidefee")),
                            rs.getString("tourstatus"),
                            rs.getString("paymentstatus")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading guide bookings: " + e.getMessage());
        }

        JTable table = new JTable(model);
        styleTable(table, ComfyGoGUI.VIBRANT_TEAL);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add refresh button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setOpaque(false);
        JButton refreshBtn = ComfyGoGUI.createStyledButton("🔄 Refresh", ComfyGoGUI.VIBRANT_TEAL);
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try (java.sql.PreparedStatement ps = mainFrame.getConnection().prepareStatement(sql)) {
                ps.setString(1, mainFrame.getCurrentUserId());
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("bookingid"), rs.getString("guidename"),
                                rs.getInt("tourdurationdays"), rs.getString("tourlocation"),
                                String.format("%.2f", rs.getDouble("guidefee")),
                                rs.getString("tourstatus"), rs.getString("paymentstatus")
                        });
                    }
                }
            } catch (Exception ex) {
                System.out.println("Error refreshing: " + ex.getMessage());
            }
        });
        actionPanel.add(refreshBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void styleTable(JTable table, Color headerColor) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setBackground(new Color(252, 254, 253));
        table.setGridColor(new Color(225, 235, 230));
        table.setSelectionBackground(new Color(46, 204, 113, 50));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
    }

    // ---------- RATINGS ----------
    private JPanel createRatingsPanel() {
        JPanel shell = screenShell("Rate & Review");

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(6, 0, 0, 0));

        JLabel typeLabel = ComfyGoGUI.label("Rating Type");
        String[] types = {"Hotel", "Tourist Spot", "Tour Guide"};
        JComboBox<String> typeCombo = ComfyGoGUI.createStyledComboBox(types);
        typeCombo.setMaximumSize(new Dimension(420, 44));

        JLabel entityLabel = ComfyGoGUI.label("Entity Name");
        JTextField entityField = ComfyGoGUI.createStyledTextField();
        entityField.setMaximumSize(new Dimension(420, 44));

        JLabel ratingLabel = ComfyGoGUI.label("Rating (1-5)");
        JTextField ratingField = ComfyGoGUI.createStyledTextField();
        ratingField.setMaximumSize(new Dimension(420, 44));

        JLabel reviewLabel = ComfyGoGUI.label("Review");
        JTextArea reviewArea = new JTextArea(5, 30);
        reviewArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setBackground(new Color(252, 254, 253));
        reviewArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton submitBtn = ComfyGoGUI.createStyledButton("Submit Rating", ComfyGoGUI.LIME);
        submitBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String entity = entityField.getText().trim();
            String ratingStr = ratingField.getText().trim();
            String review = reviewArea.getText().trim();

            if (entity.isEmpty() || ratingStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please fill all required fields!", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    JOptionPane.showMessageDialog(mainFrame, "Rating must be between 1 and 5!", "Invalid Rating", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = mainFrame.getRatingService().submitRating(
                        mainFrame.getCurrentUserId(),
                        type,
                        entity,
                        rating,
                        review
                );

                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Rating submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    entityField.setText("");
                    ratingField.setText("");
                    reviewArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to submit rating!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Rating must be a number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(typeLabel);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(typeCombo);
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        form.add(entityLabel);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(entityField);
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        form.add(ratingLabel);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(ratingField);
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        form.add(reviewLabel);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(ComfyGoGUI.scrollWrap(reviewArea));
        form.add(Box.createRigidArea(new Dimension(0, 14)));
        form.add(submitBtn);

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(form, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));
        body.add(ComfyGoGUI.scrollWrap(card), BorderLayout.CENTER);

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // ---------- Booking dialog ----------
    private void showHotelBookingDialog(String hotelName) {
        JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(this), "Book Hotel", true);
        dialog.setSize(650, 750);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ComfyGoGUI.LIGHT);

        // Header
        JPanel header = new JPanel();
        header.setBackground(ComfyGoGUI.VIBRANT_BLUE);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel headerTitle = new JLabel("🏨 Book Your Stay");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);
        header.add(headerTitle);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(ComfyGoGUI.SURFACE);
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Hotel info
        JLabel selectedHotel = new JLabel("Selected: " + hotelName);
        selectedHotel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedHotel.setForeground(ComfyGoGUI.PRIMARY);
        selectedHotel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Get hotel details
        Hotel selectedHotelObj = mainFrame.getHotelService().getAllHotels().stream()
                .filter(h -> h.getHotelName().equals(hotelName))
                .findFirst()
                .orElse(null);

        if (selectedHotelObj == null) {
            JOptionPane.showMessageDialog(dialog, "Hotel not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JLabel priceLabel = new JLabel(String.format("Price: BDT %.2f per night", selectedHotelObj.getPricePerNight()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priceLabel.setForeground(ComfyGoGUI.MUTED);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Date fields
        JLabel checkInLabel = createModernLabel("Check-In Date");
        JTextField checkInField = createModernTextField();
        checkInField.setToolTipText("Format: YYYY-MM-DD");

        JLabel checkOutLabel = createModernLabel("Check-Out Date");
        JTextField checkOutField = createModernTextField();
        checkOutField.setToolTipText("Format: YYYY-MM-DD");

        JLabel roomsLabel = createModernLabel("Number of Rooms");
        JSpinner roomsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, selectedHotelObj.getRoomAvailability(), 1));
        roomsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomsSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        roomsSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        ((JSpinner.DefaultEditor) roomsSpinner.getEditor()).getTextField().setEditable(false);

        // Total calculation
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setOpaque(false);
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel totalLabel = new JLabel("Total: BDT 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(ComfyGoGUI.SUCCESS);
        totalPanel.add(totalLabel);

        // Calculate button
        JButton calculateBtn = ComfyGoGUI.createStyledButton("Calculate Total", ComfyGoGUI.GOLD);
        calculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateBtn.addActionListener(e -> {
            try {
                String checkIn = checkInField.getText().trim();
                String checkOut = checkOutField.getText().trim();
                int rooms = (int) roomsSpinner.getValue();

                if (checkIn.isEmpty() || checkOut.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter dates!", "Missing Data", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        java.time.LocalDate.parse(checkIn),
                        java.time.LocalDate.parse(checkOut)
                );

                if (days <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Check-out must be after check-in!", "Invalid Dates", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double total = selectedHotelObj.getPricePerNight() * rooms * days;
                totalLabel.setText(String.format("Total: BDT %.2f (%d rooms × %d nights)", total, rooms, days));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Payment section
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel paymentHeader = new JLabel("💳 Payment Details");
        paymentHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        paymentHeader.setForeground(ComfyGoGUI.PRIMARY);
        paymentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel methodLabel = createModernLabel("Payment Method");
        String[] methods = {"Credit/Debit Card", "Bkash", "Nagad", "Rocket", "Cash"};
        JComboBox<String> methodCombo = new JComboBox<>(methods);
        methodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel amountLabel = createModernLabel("Payment Amount (BDT)");
        JTextField amountField = createModernTextField();

        // Confirm button
        JButton confirmBtn = ComfyGoGUI.createStyledButton("✓ Confirm Booking & Pay", ComfyGoGUI.SUCCESS);
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        confirmBtn.addActionListener(e -> {
            String checkIn = checkInField.getText().trim();
            String checkOut = checkOutField.getText().trim();
            int rooms = (int) roomsSpinner.getValue();
            String paymentMethod = (String) methodCombo.getSelectedItem();
            String amountStr = amountField.getText().trim();

            if (checkIn.isEmpty() || checkOut.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double paidAmount = Double.parseDouble(amountStr);
                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        java.time.LocalDate.parse(checkIn),
                        java.time.LocalDate.parse(checkOut)
                );
                double totalPrice = selectedHotelObj.getPricePerNight() * rooms * days;

                boolean success = mainFrame.getHotelService().bookHotelWithPayment(
                        mainFrame.getCurrentUserId(),
                        selectedHotelObj.getHotelId(),
                        checkIn,
                        checkOut,
                        rooms,
                        totalPrice,
                        paymentMethod,
                        paidAmount
                );

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            String.format("✓ Booking Confirmed!\n\nHotel: %s\nCheck-in: %s\nCheck-out: %s\nRooms: %d\nTotal: BDT %.2f\nPaid: BDT %.2f\n\nThank you for booking with ComfyGo!",
                                    hotelName, checkIn, checkOut, rooms, totalPrice, paidAmount),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    String message = (paidAmount < totalPrice) ?
                            String.format("Payment insufficient!\n\nRequired: BDT %.2f\nPaid: BDT %.2f\n\nPlease pay the full amount to confirm booking.", totalPrice, paidAmount) :
                            "Booking failed! Please check console for details.";
                    JOptionPane.showMessageDialog(dialog, message, "Booking Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Build layout
        formPanel.add(selectedHotel);
        formPanel.add(priceLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(checkInLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(checkInField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(checkOutLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(checkOutField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(roomsLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(roomsSpinner);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(calculateBtn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(totalPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(separator);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(paymentHeader);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(methodLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(methodCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(amountLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(amountField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(confirmBtn);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ComfyGoGUI.DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createModernTextField() {
        JTextField field = ComfyGoGUI.createStyledTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JTextField dialogField(JPanel parent, String label) {
        parent.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel l = ComfyGoGUI.label(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(l);

        parent.add(Box.createRigidArea(new Dimension(0, 6)));

        JTextField f = ComfyGoGUI.createStyledTextField();
        f.setMaximumSize(new Dimension(420, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(f);

        return f;
    }

    private JTable makeTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setBackground(new Color(252, 254, 253));
        table.setGridColor(new Color(225, 235, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(ComfyGoGUI.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        return table;
    }

    // Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBackground(ComfyGoGUI.VIBRANT_ORANGE);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Actions" : value.toString());
            return this;
        }
    }

    // Button Editor
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private ComfyGoGUI mainFrame;
        private DefaultTableModel model;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, ComfyGoGUI frame, DefaultTableModel tableModel) {
            super(checkBox);
            mainFrame = frame;
            model = tableModel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "Actions" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                String bookingId = model.getValueAt(currentRow, 0).toString();
                showBookingActions(bookingId, currentRow);
            }
            isPushed = false;
            return label;
        }

        private void showBookingActions(String bookingId, int row) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(button), "Booking Actions", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(400, 250);
            dialog.setLocationRelativeTo(mainFrame);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));
            panel.setBackground(ComfyGoGUI.SURFACE);

            JLabel title = new JLabel("Booking ID: " + bookingId);
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton viewBtn = ComfyGoGUI.createStyledButton("📄 View Details", ComfyGoGUI.VIBRANT_BLUE);
            viewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewBtn.addActionListener(e -> {
                showBookingDetails(bookingId);
                dialog.dispose();
            });

            JButton cancelBtn = ComfyGoGUI.createStyledButton("❌ Cancel Booking", ComfyGoGUI.SOFT_RED);
            cancelBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            cancelBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to cancel this booking?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = mainFrame.getHotelService().cancelHotelBookingForUser(
                            mainFrame.getCurrentUserId(), bookingId);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Booking cancelled successfully!");
                        model.setValueAt("CANCELLED", row, 7);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to cancel booking!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            panel.add(title);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(viewBtn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(cancelBtn);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.setVisible(true);
        }

        private void showBookingDetails(String bookingId) {
            Booking booking = Booking.getBookingById(bookingId, mainFrame.getConnection());
            if (booking == null) {
                JOptionPane.showMessageDialog(mainFrame, "Booking not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String details = String.format(
                    "Booking Details:\n\n" +
                            "Booking ID: %s\n" +
                            "Hotel: %s\n" +
                            "Location: %s\n" +
                            "Check-In: %s\n" +
                            "Check-Out: %s\n" +
                            "Rooms: %d\n" +
                            "Total Price: BDT %.2f\n" +
                            "Status: %s\n" +
                            "Payment ID: %s",
                    booking.getBookingId(),
                    booking.getHotelName(),
                    booking.getHotelLocation(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getNumberOfRooms(),
                    booking.getTotalPrice(),
                    booking.getBookingStatus(),
                    booking.getPaymentId()
            );

            JOptionPane.showMessageDialog(mainFrame, details, "Booking Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
