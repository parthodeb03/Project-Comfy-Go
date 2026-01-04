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

    // Modern color palette
    private static final Color GRADIENT_START = new Color(67, 160, 71);
    private static final Color GRADIENT_END = new Color(46, 125, 50);
    private static final Color BACKGROUND_DARK = new Color(240, 245, 248);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TRANSPORT_BUS = new Color(255, 193, 7);
    private static final Color TRANSPORT_TRAIN = new Color(33, 150, 243);
    private static final Color TRANSPORT_AIR = new Color(156, 39, 176);
    private static final Color TRANSPORT_LAUNCH = new Color(0, 188, 212);

    public TouristDashboard(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_DARK);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(BACKGROUND_DARK);

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
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, 0, getHeight(), GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 750));
        sidebar.setBorder(new EmptyBorder(24, 20, 24, 20));

        // User profile section
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setOpaque(false);
        profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userIcon = new JLabel("USER");
        userIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        userIcon.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(mainFrame.getCurrentUserName() == null ? "Tourist" : mainFrame.getCurrentUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Tourist Account");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(255, 255, 255, 200));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(userIcon);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        profilePanel.add(userLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        profilePanel.add(roleLabel);

        sidebar.add(profilePanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 100));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Navigation buttons
        sidebar.add(navBtn("Dashboard", "HOME", new Color(76, 175, 80)));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(navBtn("Hotels", "HOTELS", new Color(33, 150, 243)));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(navBtn("Tourist Spots", "SPOTS", new Color(255, 152, 0)));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(navBtn("Tour Guides", "GUIDES", new Color(156, 39, 176)));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(navBtn("Transport", "TRANSPORT", new Color(244, 67, 54)));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(navBtn("My Bookings", "BOOKINGS", new Color(0, 188, 212)));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(navBtn("Rate & Review", "RATINGS", new Color(255, 193, 7)));

        sidebar.add(Box.createVerticalGlue());

        JButton logout = createModernButton("Logout", new Color(239, 83, 80));
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> mainFrame.logout());
        sidebar.add(logout);

        return sidebar;
    }

    private JButton navBtn(String text, String panel, Color baseColor) {
        JButton btn = createModernButton(text, new Color(255, 255, 255, 30));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 255, 255, 50));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 255, 255, 30));
            }
        });
        btn.addActionListener(e -> contentLayout.show(contentPanel, panel));
        return btn;
    }

    private JButton createModernButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
            new EmptyBorder(12, 20, 12, 20)
        ));
        return btn;
    }

    private JPanel screenShell(String titleText) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BACKGROUND_DARK);
        shell.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(33, 33, 33));
        header.add(title, BorderLayout.WEST);

        shell.add(header, BorderLayout.NORTH);
        return shell;
    }

    // HOME PANEL
    private JPanel createHomePanel() {
        JPanel shell = screenShell("Dashboard");
        JPanel body = new JPanel(new GridLayout(2, 2, 20, 20));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        body.add(statCard("Hotels", "Browse & book hotels", new Color(33, 150, 243)));
        body.add(statCard("Tourist Spots", "Explore top places", new Color(76, 175, 80)));
        body.add(statCard("Tour Guides", "Hire available guides", new Color(156, 39, 176)));
        body.add(statCard("Transport", "View routes & options", new Color(244, 67, 54)));

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel statCard(String title, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 2),
            new EmptyBorder(24, 24, 24, 24)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(accent);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        s.setForeground(new Color(100, 100, 100));

        textPanel.add(t);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(s);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // HOTELS PANEL
    private JPanel createHotelsPanel() {
        JPanel shell = screenShell("Search & Book Hotels");
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel searchCard = createCard();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JLabel locLabel = new JLabel("Location:");
        locLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JTextField locationField = createStyledTextField();
        locationField.setPreferredSize(new Dimension(320, 45));

        JButton searchBtn = createModernButton("Search", new Color(76, 175, 80));
        JButton showAllBtn = createModernButton("Show All", new Color(33, 150, 243));

        searchCard.add(locLabel);
        searchCard.add(locationField);
        searchCard.add(searchBtn);
        searchCard.add(showAllBtn);

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);

        JPanel leftPanel = createCard();
        leftPanel.setLayout(new BorderLayout());

        String[] columns = {"Hotel Name", "Location", "Price/Night", "Rating", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = createStyledTable(model, new Color(33, 150, 243));
        
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        JLabel tableTitle = new JLabel("Available Hotels");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(new Color(33, 33, 33));
        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(tableTitle, BorderLayout.WEST);

        leftPanel.add(tableHeader, BorderLayout.NORTH);
        leftPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel bookingFormPanel = createInlineHotelBookingForm(table, model);

        splitPanel.add(leftPanel);
        splitPanel.add(bookingFormPanel);

        Runnable loadAll = () -> updateHotelTable(model, mainFrame.getHotelService().getAllHotels());
        Runnable doSearch = () -> updateHotelTable(model, mainFrame.getHotelService().searchHotelsByLocation(locationField.getText().trim()));
        loadAll.run();

        searchBtn.addActionListener(e -> doSearch.run());
        showAllBtn.addActionListener(e -> loadAll.run());

        body.add(searchCard, BorderLayout.NORTH);
        body.add(splitPanel, BorderLayout.CENTER);

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createInlineHotelBookingForm(JTable hotelTable, DefaultTableModel hotelModel) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel formTitle = new JLabel("Book Your Hotel");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(new Color(33, 33, 33));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel selectedHotelLabel = new JLabel("Select a hotel from the table");
        selectedHotelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectedHotelLabel.setForeground(new Color(100, 100, 100));
        selectedHotelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(33, 150, 243));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel checkInLabel = createFormLabel("Check-In Date (YYYY-MM-DD)");
        JTextField checkInField = createStyledTextField();
        checkInField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        checkInField.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkInField.setText(java.time.LocalDate.now().plusDays(1).toString());

        JLabel checkOutLabel = createFormLabel("Check-Out Date (YYYY-MM-DD)");
        JTextField checkOutField = createStyledTextField();
        checkOutField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        checkOutField.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkOutField.setText(java.time.LocalDate.now().plusDays(3).toString());

        JLabel roomsLabel = createFormLabel("Number of Rooms");
        JSpinner roomsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        roomsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomsSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        roomsSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel totalLabel = new JLabel("Total: BDT 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(new Color(76, 175, 80));
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton calculateBtn = createModernButton("Calculate Total", new Color(255, 152, 0));
        calculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel paymentHeader = new JLabel("Payment Information");
        paymentHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paymentHeader.setForeground(new Color(33, 33, 33));
        paymentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel methodLabel = createFormLabel("Payment Method");
        String[] methods = {"Cash", "Credit Card", "Debit Card", "bKash", "Nagad", "Rocket"};
        JComboBox<String> methodCombo = createStyledComboBox(methods);
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel amountLabel = createFormLabel("Payment Amount (BDT)");
        JTextField amountField = createStyledTextField();
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);

        calculateBtn.addActionListener(e -> {
            int selectedRow = hotelTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a hotel from the table first!", "No Hotel Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String checkIn = checkInField.getText().trim();
            String checkOut = checkOutField.getText().trim();
            if (!checkIn.isEmpty() && !checkOut.isEmpty()) {
                try {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(
                        java.time.LocalDate.parse(checkIn),
                        java.time.LocalDate.parse(checkOut)
                    );
                    
                    String hotelName = String.valueOf(hotelModel.getValueAt(selectedRow, 0));
                    String priceStr = String.valueOf(hotelModel.getValueAt(selectedRow, 2)).replace("BDT ", "");
                    double pricePerNight = Double.parseDouble(priceStr);
                    int rooms = (int) roomsSpinner.getValue();
                    double total = pricePerNight * rooms * days;
                    
                    totalLabel.setText(String.format("Total: BDT %.2f (%d nights Ã— %d rooms)", total, days, rooms));
                    amountField.setText(String.format("%.2f", total));
                } catch (Exception ex) {
                    totalLabel.setText("Invalid dates!");
                    JOptionPane.showMessageDialog(mainFrame, "Invalid date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton bookBtn = createModernButton("Confirm Booking & Pay", new Color(76, 175, 80));
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bookBtn.addActionListener(e -> {
            int selectedRow = hotelTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a hotel from the table first!", "No Hotel Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String checkIn = checkInField.getText().trim();
            String checkOut = checkOutField.getText().trim();
            String amountStr = amountField.getText().trim();

            if (checkIn.isEmpty() || checkOut.isEmpty() || amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please fill all fields and calculate total!", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String hotelName = String.valueOf(hotelModel.getValueAt(selectedRow, 0));
                Hotel selectedHotel = null;
                List<Hotel> allHotels = mainFrame.getHotelService().getAllHotels();
                for (Hotel h : allHotels) {
                    if (h.getHotelName().equals(hotelName)) {
                        selectedHotel = h;
                        break;
                    }
                }

                if (selectedHotel == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Hotel not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double paidAmount = Double.parseDouble(amountStr);
                int rooms = (int) roomsSpinner.getValue();
                long days = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.parse(checkIn),
                    java.time.LocalDate.parse(checkOut)
                );
                double totalPrice = selectedHotel.getPricePerNight() * rooms * days;
                String paymentMethod = (String) methodCombo.getSelectedItem();

                boolean success = mainFrame.getHotelService().bookHotelWithPayment(
                    mainFrame.getCurrentUserId(),
                    selectedHotel.getHotelId(),
                    checkIn,
                    checkOut,
                    rooms,
                    totalPrice,
                    paymentMethod,
                    paidAmount
                );

                if (success) {
                    JOptionPane.showMessageDialog(mainFrame,
                        String.format("Booking Confirmed!\n\nHotel: %s\nCheck-in: %s\nCheck-out: %s\nRooms: %d\nTotal: BDT %.2f\nPaid: BDT %.2f\n\nThank you for booking with ComfyGo!",
                        hotelName, checkIn, checkOut, rooms, totalPrice, paidAmount),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    checkInField.setText(java.time.LocalDate.now().plusDays(1).toString());
                    checkOutField.setText(java.time.LocalDate.now().plusDays(3).toString());
                    roomsSpinner.setValue(1);
                    amountField.setText("");
                    totalLabel.setText("Total: BDT 0.00");
                    hotelTable.clearSelection();
                    selectedHotelLabel.setText("Select a hotel from the table");
                    priceLabel.setText("");
                    
                    contentLayout.show(contentPanel, "BOOKINGS");

                } else {
                    String message = (paidAmount < totalPrice) ?
                        String.format("Payment insufficient!\n\nRequired: BDT %.2f\nPaid: BDT %.2f\n\nPlease pay the full amount.", totalPrice, paidAmount) :
                        "Booking failed! Please check console for details.";
                    JOptionPane.showMessageDialog(mainFrame, message, "Booking Failed", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        hotelTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = hotelTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String hotelName = String.valueOf(hotelModel.getValueAt(selectedRow, 0));
                    String location = String.valueOf(hotelModel.getValueAt(selectedRow, 1));
                    String priceStr = String.valueOf(hotelModel.getValueAt(selectedRow, 2));
                    selectedHotelLabel.setText("Selected: " + hotelName + " - " + location);
                    priceLabel.setText(priceStr + " per night");
                    totalLabel.setText("Click 'Calculate Total' to see price");
                }
            }
        });

        formPanel.add(formTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(selectedHotelLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(priceLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(checkInLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(checkInField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(checkOutLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(checkOutField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(roomsLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(roomsSpinner);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(calculateBtn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(totalLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(separator);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(paymentHeader);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(methodLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(methodCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(amountLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(amountField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        formPanel.add(bookBtn);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
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

    // SPOTS PANEL
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

        JTable table = createStyledTable(model, new Color(255, 152, 0));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        card.add(sp, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));
        body.add(card, BorderLayout.CENTER);

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // GUIDES PANEL - UPDATED WITH BOOKING & PAYMENT
    private JPanel createGuidesPanel() {
        JPanel shell = screenShell("Hire Tour Guides");

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);

        JPanel leftPanel = createCard();
        leftPanel.setLayout(new BorderLayout());

        String[] columns = {"Guide Name", "Specialization", "Division", "Experience", "Rating"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Guide> guides = mainFrame.getGuideService().getAvailableGuides();
        for (Guide g : guides) {
            model.addRow(new Object[]{
                g.getGuideName(),
                g.getSpecialization(),
                g.getGuideDivision(),
                g.getYearExperience() + " years",
                g.getRating() + "/5"
            });
        }

        JTable table = createStyledTable(model, new Color(156, 39, 176));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        JLabel tableTitle = new JLabel("Available Tour Guides");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(new Color(33, 33, 33));
        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(tableTitle, BorderLayout.WEST);

        leftPanel.add(tableHeader, BorderLayout.NORTH);
        leftPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel bookingFormPanel = createGuideBookingForm(table, model);

        splitPanel.add(leftPanel);
        splitPanel.add(bookingFormPanel);

        body.add(splitPanel, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createGuideBookingForm(JTable guideTable, DefaultTableModel guideModel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(243, 229, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(156, 39, 176), 3),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("Hire Tour Guide");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        formTitle.setForeground(new Color(156, 39, 176));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Select a guide and complete booking");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(formTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitle);

        JPanel selectedGuideCard = new JPanel();
        selectedGuideCard.setLayout(new BoxLayout(selectedGuideCard, BoxLayout.Y_AXIS));
        selectedGuideCard.setBackground(new Color(156, 39, 176, 30));
        selectedGuideCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(156, 39, 176), 2),
            new EmptyBorder(12, 12, 12, 12)
        ));
        selectedGuideCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectedGuideCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel selectedGuideLabel = new JLabel("No guide selected");
        selectedGuideLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedGuideLabel.setForeground(new Color(156, 39, 176));
        selectedGuideLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel guideDetailsLabel = new JLabel("Select a guide from the table");
        guideDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        guideDetailsLabel.setForeground(new Color(60, 60, 60));
        guideDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel guideFeeLabel = new JLabel("");
        guideFeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        guideFeeLabel.setForeground(new Color(76, 175, 80));
        guideFeeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        selectedGuideCard.add(selectedGuideLabel);
        selectedGuideCard.add(Box.createRigidArea(new Dimension(0, 6)));
        selectedGuideCard.add(guideDetailsLabel);
        selectedGuideCard.add(Box.createRigidArea(new Dimension(0, 6)));
        selectedGuideCard.add(guideFeeLabel);

        JLabel locationLabel = createColoredFormLabel("Tour Location", new Color(156, 39, 176));
        JTextField locationField = createColoredTextField();

        JLabel durationLabel = createColoredFormLabel("Tour Duration (Days)", new Color(156, 39, 176));
        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        durationSpinner.setFont(new Font("Segoe UI", Font.BOLD, 15));
        durationSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        durationSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        ((JSpinner.DefaultEditor) durationSpinner.getEditor()).getTextField().setBackground(new Color(255, 255, 255));

        JLabel startDateLabel = createColoredFormLabel("Start Date (YYYY-MM-DD)", new Color(156, 39, 176));
        JTextField startDateField = createColoredTextField();
        startDateField.setText(java.time.LocalDate.now().plusDays(2).toString());

        JLabel feeDisplayLabel = createColoredFormLabel("Total Guide Fee", new Color(156, 39, 176));
        JTextField totalFeeField = createColoredTextField();
        totalFeeField.setEditable(false);
        totalFeeField.setText("BDT 0.00");
        totalFeeField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalFeeField.setForeground(new Color(76, 175, 80));

        JButton calculateBtn = new JButton("Calculate Total Fee");
        calculateBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        calculateBtn.setBackground(new Color(255, 152, 0));
        calculateBtn.setForeground(Color.WHITE);
        calculateBtn.setFocusPainted(false);
        calculateBtn.setBorderPainted(false);
        calculateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        calculateBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(245, 124, 0), 2),
            new EmptyBorder(12, 20, 12, 20)
        ));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setForeground(new Color(156, 39, 176));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel paymentHeader = new JLabel("Payment Information");
        paymentHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        paymentHeader.setForeground(new Color(156, 39, 176));
        paymentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel paymentMethodLabel = createColoredFormLabel("Payment Method", new Color(156, 39, 176));
        String[] methods = {"bKash", "Nagad", "Rocket", "Credit Card", "Debit Card", "Cash"};
        JComboBox<String> paymentMethodCombo = new JComboBox<>(methods);
        paymentMethodCombo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        paymentMethodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        paymentMethodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentMethodCombo.setBackground(Color.WHITE);

        JLabel paymentAmountLabel = createColoredFormLabel("Payment Amount (BDT)", new Color(156, 39, 176));
        JTextField paymentAmountField = createColoredTextField();
        paymentAmountField.setFont(new Font("Segoe UI", Font.BOLD, 16));

        calculateBtn.addActionListener(e -> {
            int selectedRow = guideTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Please select a guide from the table first!", 
                    "No Guide Selected", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String guideName = String.valueOf(guideModel.getValueAt(selectedRow, 0));
            int duration = (int) durationSpinner.getValue();

            Guide selectedGuide = null;
            List<Guide> allGuides = mainFrame.getGuideService().getAvailableGuides();
            for (Guide g : allGuides) {
                if (g.getGuideName().equals(guideName)) {
                    selectedGuide = g;
                    break;
                }
            }

            if (selectedGuide != null) {
                double dailyFee = selectedGuide.getDailyFee();
                double totalFee = dailyFee * duration;
                totalFeeField.setText(String.format("BDT %.2f", totalFee));
                paymentAmountField.setText(String.format("%.2f", totalFee));
                
                JOptionPane.showMessageDialog(mainFrame,
                    String.format("Fee Calculated!\n\n" +
                        "Guide: %s\n" +
                        "Daily Rate: BDT %.2f\n" +
                        "Duration: %d days\n" +
                        "Total Fee: BDT %.2f",
                        guideName, dailyFee, duration, totalFee),
                    "Fee Calculation",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton bookBtn = new JButton("CONFIRM BOOKING & PAY");
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bookBtn.setBackground(new Color(76, 175, 80));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setBorderPainted(false);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        bookBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 142, 60), 3),
            new EmptyBorder(14, 20, 14, 20)
        ));

        bookBtn.addActionListener(e -> {
            int selectedRow = guideTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Please select a guide from the table first!",
                    "No Guide Selected",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String location = locationField.getText().trim();
            String startDate = startDateField.getText().trim();
            String paymentAmountStr = paymentAmountField.getText().trim();
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();

            if (location.isEmpty() || startDate.isEmpty() || paymentAmountStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Please fill all fields and calculate the fee first!",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String guideName = String.valueOf(guideModel.getValueAt(selectedRow, 0));
                int duration = (int) durationSpinner.getValue();
                double paymentAmount = Double.parseDouble(paymentAmountStr);

                Guide selectedGuide = null;
                List<Guide> allGuides = mainFrame.getGuideService().getAvailableGuides();
                for (Guide g : allGuides) {
                    if (g.getGuideName().equals(guideName)) {
                        selectedGuide = g;
                        break;
                    }
                }

                if (selectedGuide == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Guide not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double totalFee = selectedGuide.getDailyFee() * duration;

                if (paymentAmount < totalFee) {
                    JOptionPane.showMessageDialog(mainFrame,
                        String.format("Payment Insufficient!\n\nRequired: BDT %.2f\nPaid: BDT %.2f\n\nPlease pay the full amount.",
                            totalFee, paymentAmount),
                        "Payment Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = mainFrame.getGuideService().bookGuideWithPayment(
                    mainFrame.getCurrentUserId(),
                    selectedGuide.getGuideId(),
                    location,
                    duration,
                    startDate,
                    totalFee,
                    paymentMethod,
                    paymentAmount
                );

                if (success) {
                    JOptionPane.showMessageDialog(mainFrame,
                        String.format("Guide Booking Confirmed!\n\n" +
                            "Guide: %s\n" +
                            "Specialization: %s\n" +
                            "Location: %s\n" +
                            "Duration: %d days\n" +
                            "Start Date: %s\n" +
                            "Total Fee: BDT %.2f\n" +
                            "Payment: %s (BDT %.2f)\n\n" +
                            "Your guide has been notified!\n" +
                            "Check 'My Bookings' for details.",
                            guideName,
                            selectedGuide.getSpecialization(),
                            location,
                            duration,
                            startDate,
                            totalFee,
                            paymentMethod,
                            paymentAmount),
                        "Booking Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                    locationField.setText("");
                    startDateField.setText(java.time.LocalDate.now().plusDays(2).toString());
                    durationSpinner.setValue(1);
                    totalFeeField.setText("BDT 0.00");
                    paymentAmountField.setText("");
                    guideTable.clearSelection();
                    selectedGuideLabel.setText("No guide selected");
                    guideDetailsLabel.setText("Select a guide from the table");
                    guideFeeLabel.setText("");

                    contentLayout.show(contentPanel, "BOOKINGS");
                } else {
                    JOptionPane.showMessageDialog(mainFrame,
                        "Booking Failed!\n\nPlease try again or contact support.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Invalid input: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        guideTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = guideTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String guideName = String.valueOf(guideModel.getValueAt(selectedRow, 0));
                    String specialization = String.valueOf(guideModel.getValueAt(selectedRow, 1));
                    String division = String.valueOf(guideModel.getValueAt(selectedRow, 2));
                    String experience = String.valueOf(guideModel.getValueAt(selectedRow, 3));
                    String rating = String.valueOf(guideModel.getValueAt(selectedRow, 4));

                    Guide selectedGuide = null;
                    List<Guide> allGuides = mainFrame.getGuideService().getAvailableGuides();
                    for (Guide g : allGuides) {
                        if (g.getGuideName().equals(guideName)) {
                            selectedGuide = g;
                            break;
                        }
                    }

                    selectedGuideLabel.setText(guideName);
                    guideDetailsLabel.setText(String.format("%s | %s | %s | Rating: %s", 
                        specialization, division, experience, rating));
                    
                    if (selectedGuide != null) {
                        guideFeeLabel.setText(String.format("BDT %.2f per day", selectedGuide.getDailyFee()));
                    }
                }
            }
        });

        formPanel.add(headerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(selectedGuideCard);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(locationLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(locationField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        formPanel.add(durationLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(durationSpinner);
        formPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        formPanel.add(startDateLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(startDateField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        formPanel.add(feeDisplayLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(totalFeeField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(calculateBtn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(separator);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(paymentHeader);
        formPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        formPanel.add(paymentMethodLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(paymentMethodCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 14)));
        formPanel.add(paymentAmountLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(paymentAmountField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        formPanel.add(bookBtn);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JLabel createColoredFormLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createColoredTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.BOLD, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(156, 39, 176), 2),
            new EmptyBorder(12, 14, 12, 14)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    // TRANSPORT PANEL
    private JPanel createTransportPanel() {
        JPanel shell = screenShell("Transport Routes & Booking");

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);
        
        JPanel bookingCard = createTransportBookingForm();
        JPanel routesDisplayPanel = createRoutesDisplayPanel();
        
        splitPanel.add(bookingCard);
        splitPanel.add(routesDisplayPanel);

        body.add(splitPanel, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createRoutesDisplayPanel() {
        JPanel mainPanel = createCard();
        mainPanel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("Available Transport Routes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(33, 33, 33));
        title.setBorder(new EmptyBorder(0, 0, 16, 0));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(title, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        List<String> routes = mainFrame.getTransportService().getAllRoutes();
        java.util.Map<String, List<String>> routesByType = new java.util.HashMap<>();
        routesByType.put("Bus", new java.util.ArrayList<>());
        routesByType.put("Train", new java.util.ArrayList<>());
        routesByType.put("Air", new java.util.ArrayList<>());
        routesByType.put("Launch", new java.util.ArrayList<>());

        for (String route : routes) {
            if (route.startsWith("Bus:")) routesByType.get("Bus").add(route);
            else if (route.startsWith("Train:")) routesByType.get("Train").add(route);
            else if (route.startsWith("Air:")) routesByType.get("Air").add(route);
            else if (route.startsWith("Launch:")) routesByType.get("Launch").add(route);
        }

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setOpaque(false);

        if (!routesByType.get("Bus").isEmpty()) {
            cardsPanel.add(createTransportCard("Bus Services", routesByType.get("Bus"), TRANSPORT_BUS));
            cardsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        if (!routesByType.get("Train").isEmpty()) {
            cardsPanel.add(createTransportCard("Train Services", routesByType.get("Train"), TRANSPORT_TRAIN));
            cardsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        if (!routesByType.get("Air").isEmpty()) {
            cardsPanel.add(createTransportCard("Air Services", routesByType.get("Air"), TRANSPORT_AIR));
            cardsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        if (!routesByType.get("Launch").isEmpty()) {
            cardsPanel.add(createTransportCard("Launch Services", routesByType.get("Launch"), TRANSPORT_LAUNCH));
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createTransportCard(String title, List<String> routes, Color themeColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeColor, 3),
            new EmptyBorder(16, 16, 16, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(themeColor);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel countLabel = new JLabel(routes.size() + " routes");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(new Color(120, 120, 120));
        headerPanel.add(countLabel, BorderLayout.EAST);

        JPanel routesPanel = new JPanel();
        routesPanel.setLayout(new BoxLayout(routesPanel, BoxLayout.Y_AXIS));
        routesPanel.setOpaque(false);
        routesPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        for (String route : routes) {
            JPanel routeItem = createRouteItem(route, themeColor);
            routesPanel.add(routeItem);
            routesPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        JScrollPane scrollPane = new JScrollPane(routesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRouteItem(String route, Color themeColor) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 30));
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(themeColor, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String displayRoute = route.substring(route.indexOf(":") + 2);
        JLabel routeLabel = new JLabel(displayRoute);
        routeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        routeLabel.setForeground(new Color(33, 33, 33));
        item.add(routeLabel, BorderLayout.CENTER);
        return item;
    }

    private JPanel createTransportBookingForm() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel formTitle = new JLabel("Book Transport");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(new Color(33, 33, 33));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(formTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel typeLabel = createFormLabel("Transport Type");
        String[] types = {"Bus", "Train", "Air", "Launch"};
        JComboBox<String> typeCombo = createStyledComboBox(types);

        List<String> locationsList = mainFrame.getTransportService().getAllLocations();
        String[] locationsArray = locationsList.toArray(new String[0]);

        JLabel fromLabel = createFormLabel("From Location");
        JComboBox<String> fromCombo = createStyledComboBox(locationsArray);
        fromCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        fromCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel toLabel = createFormLabel("To Location");
        JComboBox<String> toCombo = createStyledComboBox(locationsArray);
        toCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        toCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tripTypeLabel = createFormLabel("Trip Type");
        String[] tripTypes = {"One-way", "Return"};
        JComboBox<String> tripTypeCombo = createStyledComboBox(tripTypes);
        tripTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        tripTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = createFormLabel("Departure Date (YYYY-MM-DD)");
        JTextField dateField = createStyledTextField();
        dateField.setText(java.time.LocalDate.now().plusDays(1).toString());

        JLabel returnDateLabel = createFormLabel("Return Date (YYYY-MM-DD)");
        returnDateLabel.setVisible(false);
        JTextField returnDateField = createStyledTextField();
        returnDateField.setText(java.time.LocalDate.now().plusDays(3).toString());
        returnDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        returnDateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        returnDateField.setVisible(false);

        tripTypeCombo.addActionListener(e -> {
            boolean isReturn = "Return".equals(tripTypeCombo.getSelectedItem());
            returnDateLabel.setVisible(isReturn);
            returnDateField.setVisible(isReturn);
            formPanel.revalidate();
            formPanel.repaint();
        });

        JLabel passLabel = createFormLabel("Number of Passengers");
        JSpinner passSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        passSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel durationLabel = createFormLabel("Journey Duration");
        JTextField durationField = createStyledTextField();
        durationField.setEditable(false);
        durationField.setText("--");
        durationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        durationField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel seatsLabel = createFormLabel("Available Seats");
        JTextField seatsField = createStyledTextField();
        seatsField.setEditable(false);
        seatsField.setText("--");
        seatsField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        seatsField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel farePerPersonLabel = createFormLabel("Fare per Person (BDT)");
        JTextField farePerPersonField = createStyledTextField();
        farePerPersonField.setEditable(false);
        farePerPersonField.setText("0.00");
        farePerPersonField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        farePerPersonField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel totalFareLabel = createFormLabel("Total Fare (BDT)");
        JTextField totalFareField = createStyledTextField();
        totalFareField.setEditable(false);
        totalFareField.setText("0.00");
        totalFareField.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalFareField.setForeground(new Color(76, 175, 80));
        totalFareField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        totalFareField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton checkBtn = createModernButton("Check Availability & Calculate Fare", new Color(255, 152, 0));
        checkBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();
            String date = dateField.getText().trim();
            int passengers = (int) passSpinner.getValue();
            boolean isReturn = "Return".equals(tripTypeCombo.getSelectedItem());

            if (from == null || to == null || date.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please select all fields!", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (from.equals(to)) {
                JOptionPane.showMessageDialog(mainFrame, "From and To locations must be different!", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<String> routes = mainFrame.getTransportService().getAllRoutes();
            String matchingRoute = null;
            for (String route : routes) {
                if (route.toLowerCase().contains(type.toLowerCase() + ":") && 
                    route.toLowerCase().contains(from.toLowerCase()) && 
                    route.toLowerCase().contains(to.toLowerCase())) {
                    matchingRoute = route;
                    break;
                }
            }

            if (matchingRoute != null) {
                TransportService.RouteInfo info = mainFrame.getTransportService().parseRoute(matchingRoute);
                if (info != null) {
                    int availSeats = mainFrame.getTransportService().getAvailableSeats(type, from, to, date);

                    durationField.setText(info.duration);
                    seatsField.setText(String.valueOf(availSeats));
                    seatsField.setForeground(availSeats < 10 ? Color.RED : new Color(76, 175, 80));

                    farePerPersonField.setText(String.format("%.2f", info.fare));
                    
                    double totalFare = info.fare * passengers;
                    if (isReturn) {
                        totalFare = totalFare * 2;
                    }
                    totalFareField.setText(String.format("%.2f", totalFare));

                    if (availSeats < passengers) {
                        JOptionPane.showMessageDialog(mainFrame,
                            String.format("Not enough seats! Only %d seats available.", availSeats),
                            "Insufficient Seats", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame,
                            String.format("Seats Available!\n\nRoute: %s\nDuration: %s\nFare: BDT %.2f%s\n\n%d seats confirmed for %d passengers",
                                from + " to " + to, info.duration, totalFare,
                                isReturn ? " (Round Trip)" : " (One-way)", availSeats, passengers),
                            "Availability Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Could not parse route information!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                    "No direct route found for this combination!\n\nPlease try different locations or transport type.",
                    "Route Not Available", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton bookBtn = createModernButton("Confirm Booking", new Color(76, 175, 80));
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bookBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();
            String date = dateField.getText().trim();
            int passengers = (int) passSpinner.getValue();
            boolean isReturn = "Return".equals(tripTypeCombo.getSelectedItem());
            String returnDate = isReturn ? returnDateField.getText().trim() : null;

            if (from == null || to == null || date.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please select all fields!", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (from.equals(to)) {
                JOptionPane.showMessageDialog(mainFrame, "From and To locations must be different!", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String totalFareStr = totalFareField.getText().trim();
            if (totalFareStr.equals("0.00") || totalFareStr.equals("--")) {
                JOptionPane.showMessageDialog(mainFrame, "Please check availability first!", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isReturn && (returnDate == null || returnDate.isEmpty())) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter return date!", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double totalFare = Double.parseDouble(totalFareStr);
            double farePerPerson = Double.parseDouble(farePerPersonField.getText().trim());

            boolean success = mainFrame.getTransportService().bookTransport(
                mainFrame.getCurrentUserId(),
                type,
                from,
                to,
                date,
                java.time.LocalDate.now().toString(),
                passengers,
                "Auto-Assigned",
                isReturn ? (farePerPerson * passengers) : totalFare,
                "Standard",
                "ComfyGo Transport",
                isReturn,
                returnDate
            );

            if (success) {
                String message = String.format(
                    "Transport Booked Successfully!\n\n" +
                    "Type: %s\n" +
                    "From: %s\n" +
                    "To: %s\n" +
                    "Departure: %s\n" +
                    "%s" +
                    "Passengers: %d\n" +
                    "Duration: %s\n" +
                    "Total Fare: BDT %.2f%s\n\n" +
                    "Tickets have been sent to your bookings!",
                    type, from, to, date,
                    isReturn ? ("Return: " + returnDate + "\n") : "",
                    passengers, durationField.getText(), totalFare,
                    isReturn ? " (Round Trip)" : ""
                );

                JOptionPane.showMessageDialog(mainFrame, message, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);

                fromCombo.setSelectedIndex(0);
                toCombo.setSelectedIndex(0);
                dateField.setText(java.time.LocalDate.now().plusDays(1).toString());
                returnDateField.setText(java.time.LocalDate.now().plusDays(3).toString());
                passSpinner.setValue(1);
                totalFareField.setText("0.00");
                farePerPersonField.setText("0.00");
                durationField.setText("--");
                seatsField.setText("--");
                tripTypeCombo.setSelectedIndex(0);

                contentLayout.show(contentPanel, "BOOKINGS");
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                    "Booking failed!\n\nPossible reasons:\n- Not enough seats available\n- Database error\n\nPlease check seat availability again.",
                    "Booking Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(typeLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(typeCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(fromLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(fromCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(toLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(toCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(tripTypeLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(tripTypeCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(dateLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(dateField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(returnDateLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(returnDateField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(passLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(passSpinner);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(checkBtn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(durationLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(durationField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(seatsLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(seatsField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(farePerPersonLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(farePerPersonField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(totalFareLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(totalFareField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        formPanel.add(bookBtn);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    // BOOKINGS PANEL
    private JPanel createBookingsPanel() {
        JPanel shell = screenShell("My Bookings");

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(CARD_BG);
        tabs.setForeground(new Color(33, 33, 33));

        tabs.addTab("Hotels", createHotelBookingsTable());
        tabs.addTab("Transport", createTransportBookingsTable());
        tabs.addTab("Guides", createGuideBookingsTable());

        body.add(tabs, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createHotelBookingsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);

        String[] columns = {"Booking ID", "Hotel", "Location", "Check-In", "Check-Out", "Rooms", "Total (BDT)", "Status", "Payment", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9;
            }
        };

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
                b.getPaymentId() != null ? "Paid" : "Unpaid",
                "View/Cancel"
            });
        }

        JTable table = createStyledTable(model, new Color(33, 150, 243));
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), mainFrame, model));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        actionPanel.setOpaque(false);

        JButton refreshBtn = createModernButton("Refresh", new Color(33, 150, 243));
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<Booking> updatedBookings = Booking.getBookingsByUser(mainFrame.getCurrentUserId(), mainFrame.getConnection());
            for (Booking b : updatedBookings) {
                model.addRow(new Object[]{
                    b.getBookingId(), b.getHotelName(), b.getHotelLocation(),
                    b.getCheckInDate(), b.getCheckOutDate(), b.getNumberOfRooms(),
                    String.format("%.2f", b.getTotalPrice()), b.getBookingStatus(),
                    b.getPaymentId() != null ? "Paid" : "Unpaid", "View/Cancel"
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
        panel.setBackground(CARD_BG);

        String[] columns = {"Ticket ID", "Type", "From", "To", "Date", "Passengers", "Fare (BDT)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

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

        JTable table = createStyledTable(model, new Color(244, 67, 54));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        actionPanel.setOpaque(false);

        JButton refreshBtn = createModernButton("Refresh", new Color(244, 67, 54));
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
        panel.setBackground(CARD_BG);

        String[] columns = {"Booking ID", "Guide Name", "Duration (days)", "Location", "Fee (BDT)", "Status", "Payment"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

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

        JTable table = createStyledTable(model, new Color(156, 39, 176));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        actionPanel.setOpaque(false);

        JButton refreshBtn = createModernButton("Refresh", new Color(156, 39, 176));
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

    // RATINGS PANEL
    private JPanel createRatingsPanel() {
        JPanel shell = screenShell("Rate & Review");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel card = createCard();
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel formTitle = new JLabel("Submit Your Review");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(new Color(33, 33, 33));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = createFormLabel("Rating Type");
        String[] types = {"Hotel", "Tourist Spot", "Tour Guide"};
        JComboBox<String> typeCombo = createStyledComboBox(types);

        JLabel entityLabel = createFormLabel("Entity Name");
        JTextField entityField = createStyledTextField();

        JLabel ratingLabel = createFormLabel("Rating (1-5)");
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        ratingSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ratingSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        ratingSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel reviewLabel = createFormLabel("Review (Optional)");
        JTextArea reviewArea = new JTextArea(6, 30);
        reviewArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setBackground(new Color(250, 250, 250));
        reviewArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));

        JScrollPane reviewScroll = new JScrollPane(reviewArea);
        reviewScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        reviewScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton submitBtn = createModernButton("Submit Rating", new Color(255, 193, 7));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String entity = entityField.getText().trim();
            int rating = (int) ratingSpinner.getValue();
            String review = reviewArea.getText().trim();

            if (entity.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter entity name!", "Missing Data", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(mainFrame,
                    String.format("Rating Submitted Successfully!\n\nType: %s\nEntity: %s\nRating: %d/5", type, entity, rating),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                entityField.setText("");
                ratingSpinner.setValue(5);
                reviewArea.setText("");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Failed to submit rating!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(formTitle);
        form.add(Box.createRigidArea(new Dimension(0, 24)));
        form.add(typeLabel);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        form.add(typeCombo);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(entityLabel);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        form.add(entityField);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(ratingLabel);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        form.add(ratingSpinner);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(reviewLabel);
        form.add(Box.createRigidArea(new Dimension(0, 8)));
        form.add(reviewScroll);
        form.add(Box.createRigidArea(new Dimension(0, 20)));
        form.add(submitBtn);

        card.add(form);
        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // HELPER METHODS
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        return card;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }

    private JTable createStyledTable(DefaultTableModel model, Color headerColor) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(42);
        table.setBackground(new Color(252, 254, 253));
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(headerColor.getRed(), headerColor.getGreen(), headerColor.getBlue(), 50));
        table.setSelectionForeground(new Color(33, 33, 33));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 48));
        return table;
    }

    // Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBackground(new Color(33, 150, 243));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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
            dialog.setSize(450, 280);
            dialog.setLocationRelativeTo(mainFrame);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(24, 24, 24, 24));
            panel.setBackground(CARD_BG);

            JLabel title = new JLabel("Booking ID: " + bookingId);
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton viewBtn = createModernButton("View Details", new Color(33, 150, 243));
            viewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            viewBtn.addActionListener(e -> {
                showBookingDetails(bookingId);
                dialog.dispose();
            });

            JButton cancelBtn = createModernButton("Cancel Booking", new Color(244, 67, 54));
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
            panel.add(Box.createRigidArea(new Dimension(0, 24)));
            panel.add(viewBtn);
            panel.add(Box.createRigidArea(new Dimension(0, 12)));
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