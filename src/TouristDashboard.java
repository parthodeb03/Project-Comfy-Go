import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TouristDashboard extends JPanel {
    private final ComfyGoGUI mainFrame;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;

    public TouristDashboard(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.BACKGROUND);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ComfyGoGUI.BACKGROUND);

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
                GradientPaint gp = new GradientPaint(0, 0, new Color(67, 160, 71), 0, getHeight(), new Color(46, 125, 50));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 800));
        sidebar.setBorder(new EmptyBorder(25, 20, 25, 20));

        // User profile
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setOpaque(false);

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        userIcon.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(mainFrame.getCurrentUserName() == null ? "Tourist" : mainFrame.getCurrentUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Tourist Account");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(255, 255, 255, 200));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profilePanel.add(userIcon);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 12)));
        profilePanel.add(userLabel);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        profilePanel.add(roleLabel);

        sidebar.add(profilePanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Navigation buttons
        sidebar.add(navBtn("🏠 Dashboard", "HOME"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("🏨 Hotels", "HOTELS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("🗺️ Tourist Spots", "SPOTS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("🧑‍🤝‍🧑 Tour Guides", "GUIDES"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("🚌 Transport", "TRANSPORT"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("📋 My Bookings", "BOOKINGS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("⭐ Rate & Review", "RATINGS"));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = ComfyGoGUI.createStyledButton("Logout", ComfyGoGUI.DANGER);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(240, 50));
        logoutBtn.addActionListener(e -> mainFrame.logout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton navBtn(String text, String panel) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 255, 255, 20));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(240, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 255, 255, 40));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 255, 255, 20));
            }
        });

        btn.addActionListener(e -> contentLayout.show(contentPanel, panel));
        return btn;
    }

    private JPanel screenShell(String titleText) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(ComfyGoGUI.BACKGROUND);
        shell.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ComfyGoGUI.CARD_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        shell.add(header, BorderLayout.NORTH);
        return shell;
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1));
        return card;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setGridColor(new Color(224, 224, 224));
        table.setSelectionBackground(new Color(33, 150, 243, 50));
        table.setSelectionForeground(new Color(33, 33, 33));
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(33, 33, 33));

        // Custom header renderer to force colors
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                label.setBackground(new Color(33, 150, 243));  // Blue background
                label.setForeground(Color.WHITE);              // White text
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(21, 101, 192), 1),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)
                ));
                return label;
            }
        };
        
        // Apply custom renderer to all columns
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Center align cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // ========== HOME PANEL ==========
    private JPanel createHomePanel() {
        JPanel shell = screenShell("Tourist Dashboard");
        JPanel body = new JPanel(new GridLayout(2, 2, 20, 20));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        body.add(statCard("🏨 Hotels", "Browse & book hotels", ComfyGoGUI.PRIMARY));
        body.add(statCard("🗺️ Tourist Spots", "Explore top places", ComfyGoGUI.SUCCESS));
        body.add(statCard("🧑‍🤝‍🧑 Tour Guides", "Hire available guides", new Color(156, 39, 176)));
        body.add(statCard("🚌 Transport", "View routes & options", ComfyGoGUI.DANGER));

        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel statCard(String title, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ComfyGoGUI.CARD_BG);
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
        s.setForeground(ComfyGoGUI.TEXT_SECONDARY);

        textPanel.add(t);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(s);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // ========== HOTELS PANEL ==========
    private JPanel createHotelsPanel() {
        JPanel shell = screenShell("Search & Book Hotels");
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel searchCard = createCard();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JLabel locLabel = new JLabel("Location:");
        locLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField locationField = ComfyGoGUI.createStyledTextField();
        locationField.setPreferredSize(new Dimension(320, 45));

        JButton searchBtn = ComfyGoGUI.createStyledButton("Search", ComfyGoGUI.SUCCESS);
        JButton showAllBtn = ComfyGoGUI.createStyledButton("Show All", ComfyGoGUI.PRIMARY);

        searchCard.add(locLabel);
        searchCard.add(locationField);
        searchCard.add(searchBtn);
        searchCard.add(showAllBtn);

        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);

        JPanel leftPanel = createCard();
        leftPanel.setLayout(new BorderLayout());

        String[] columns = {"Hotel Name", "Location", "Price/Night", "Rating", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createStyledTable(model);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        JLabel tableTitle = new JLabel("Available Hotels");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(tableTitle, BorderLayout.WEST);

        leftPanel.add(tableHeader, BorderLayout.NORTH);
        leftPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel bookingFormPanel = createHotelBookingForm(table, model);

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

    private JPanel createHotelBookingForm(JTable hotelTable, DefaultTableModel hotelModel) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel formTitle = new JLabel("Book Your Hotel");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel selectedHotelLabel = new JLabel("Select a hotel from the table");
        selectedHotelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectedHotelLabel.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        selectedHotelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel("");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(ComfyGoGUI.PRIMARY);
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel checkInLabel = createFormLabel("Check-In Date (YYYY-MM-DD)");
        JTextField checkInField = ComfyGoGUI.createStyledTextField();
        checkInField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        checkInField.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkInField.setText(java.time.LocalDate.now().plusDays(1).toString());

        JLabel checkOutLabel = createFormLabel("Check-Out Date (YYYY-MM-DD)");
        JTextField checkOutField = ComfyGoGUI.createStyledTextField();
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
        totalLabel.setForeground(ComfyGoGUI.SUCCESS);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton calculateBtn = ComfyGoGUI.createStyledButton("Calculate Total", ComfyGoGUI.ACCENT);
        calculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

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

                    String priceStr = String.valueOf(hotelModel.getValueAt(selectedRow, 2)).replace("BDT ", "");
                    double pricePerNight = Double.parseDouble(priceStr);
                    int rooms = (int) roomsSpinner.getValue();
                    double total = pricePerNight * rooms * days;

                    totalLabel.setText(String.format("Total: BDT %.2f (%d nights, %d rooms)", total, days, rooms));
                } catch (Exception ex) {
                    totalLabel.setText("Invalid dates!");
                    JOptionPane.showMessageDialog(mainFrame, "Invalid date format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel paymentHeader = new JLabel("Payment Information");
        paymentHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        paymentHeader.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        paymentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel methodLabel = createFormLabel("Payment Method");
        String[] methods = {"Cash", "Credit Card", "Debit Card", "bKash", "Nagad", "Rocket"};
        JComboBox<String> methodCombo = ComfyGoGUI.createStyledComboBox(methods);
        methodCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel amountLabel = createFormLabel("Payment Amount (BDT)");
        JTextField amountField = ComfyGoGUI.createStyledTextField();
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton bookBtn = ComfyGoGUI.createStyledButton("Confirm Booking & Pay", ComfyGoGUI.SUCCESS);
        bookBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
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
                    JOptionPane.showMessageDialog(mainFrame, "Booking failed! Please check console for details.", "Booking Failed", JOptionPane.ERROR_MESSAGE);
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

    // ========== SPOTS PANEL - COLORFUL CARD LAYOUT ==========
    private JPanel createSpotsPanel() {
        JPanel shell = screenShell("Explore Tourist Spots");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        List<TouristSpot> spots = mainFrame.getSpotService().getAllSpots();

        // Create grid panel for cards (3 columns)
        JPanel cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setOpaque(false);

        // Color palette for divisions
        Color[] divisionColors = {
            new Color(26, 188, 156),    // Turquoise
            new Color(46, 204, 113),    // Emerald
            new Color(52, 152, 219),    // Blue
            new Color(155, 89, 182),    // Purple
            new Color(241, 196, 15),    // Yellow
            new Color(230, 126, 34),    // Orange
            new Color(231, 76, 60),     // Red
            new Color(149, 165, 166)    // Gray
        };

        int colorIndex = 0;
        for (TouristSpot spot : spots) {
            JPanel spotCard = createSpotCard(spot, divisionColors[colorIndex % divisionColors.length]);
            cardsPanel.add(spotCard);
            colorIndex++;
        }

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        body.add(scrollPane, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createSpotCard(TouristSpot spot, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 3),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(350, 220));

        // Top section with icon and name
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🗺️");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(spot.getSpotName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(accentColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(iconLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(nameLabel);

        // Middle section with location info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(15, 0, 10, 0));

        JLabel divisionLabel = new JLabel("📍 " + spot.getDivision() + " - " + spot.getDistrict());
        divisionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        divisionLabel.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        divisionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel feeLabel = new JLabel("Entry: BDT " + spot.getEntryFee());
        feeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        feeLabel.setForeground(new Color(39, 174, 96));
        feeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(divisionLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(feeLabel);

        // Bottom section with rating
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        ratingPanel.setOpaque(false);

        double rating = spot.getRating();
        int fullStars = (int) rating;
        
        for (int i = 0; i < fullStars; i++) {
            JLabel star = new JLabel("⭐");
            star.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            ratingPanel.add(star);
        }
        
        JLabel ratingText = new JLabel(String.format("%.1f/5", rating));
        ratingText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ratingText.setForeground(new Color(243, 156, 18));
        ratingPanel.add(ratingText);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(ratingPanel, BorderLayout.SOUTH);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 4),
                    new EmptyBorder(19, 19, 19, 19)
                ));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 3),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showSpotDetails(spot);
            }
        });

        return card;
    }

    private void showSpotDetails(TouristSpot spot) {
        String details = String.format(
            "🗺️ TOURIST SPOT DETAILS\n\n" +
            "Spot Name: %s\n\n" +
            "Location:\n" +
            "  Division: %s\n" +
            "  District: %s\n\n" +
            "Entry Fee: BDT %.2f\n" +
            "Rating: %.1f/5 ⭐\n\n" +
            "Plan your visit to this amazing destination!",
            spot.getSpotName(),
            spot.getDivision(),
            spot.getDistrict(),
            spot.getEntryFee(),
            spot.getRating()
        );

        JOptionPane.showMessageDialog(
            mainFrame,
            details,
            spot.getSpotName() + " - Details",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========== GUIDES PANEL ==========
    private JPanel createGuidesPanel() {
        JPanel shell = screenShell("Hire Tour Guides");
        JPanel body = new JPanel(new BorderLayout(0, 20));
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

        JTable table = createStyledTable(model);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        JLabel tableTitle = new JLabel("Available Tour Guides");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableTitle.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

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
            BorderFactory.createLineBorder(new Color(156, 39, 176), 2),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel formTitle = new JLabel("Hire Tour Guide");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        formTitle.setForeground(new Color(156, 39, 176));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Select a guide and complete booking");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel selectedGuideCard = new JPanel();
        selectedGuideCard.setLayout(new BoxLayout(selectedGuideCard, BoxLayout.Y_AXIS));
        selectedGuideCard.setBackground(new Color(156, 39, 176, 30));
        selectedGuideCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(156, 39, 176), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        selectedGuideCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectedGuideCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel selectedGuideLabel = new JLabel("No guide selected");
        selectedGuideLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        selectedGuideLabel.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        selectedGuideLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel guideDetailsLabel = new JLabel("Click on a guide from the table");
        guideDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        guideDetailsLabel.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        guideDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        selectedGuideCard.add(selectedGuideLabel);
        selectedGuideCard.add(Box.createRigidArea(new Dimension(0, 8)));
        selectedGuideCard.add(guideDetailsLabel);

        JLabel locationLabel = createFormLabel("Tour Location");
        JTextField locationField = ComfyGoGUI.createStyledTextField();
        locationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        locationField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel startDateLabel = createFormLabel("Start Date (YYYY-MM-DD)");
        JTextField startDateField = ComfyGoGUI.createStyledTextField();
        startDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        startDateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        startDateField.setText(java.time.LocalDate.now().plusDays(2).toString());

        JLabel daysLabel = createFormLabel("Tour Duration (Days)");
        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        daysSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        daysSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        daysSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel feeLabel = createFormLabel("Guide Fee (BDT per day)");
        JTextField feeField = ComfyGoGUI.createStyledTextField();
        feeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        feeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        feeField.setText("500");

        JLabel totalFeeLabel = new JLabel("Total Fee: BDT 0.00");
        totalFeeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalFeeLabel.setForeground(new Color(156, 39, 176));
        totalFeeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton calculateFeeBtn = ComfyGoGUI.createStyledButton("Calculate Total Fee", ComfyGoGUI.ACCENT);
        calculateFeeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateFeeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        calculateFeeBtn.addActionListener(e -> {
            try {
                int days = (int) daysSpinner.getValue();
                double feePerDay = Double.parseDouble(feeField.getText().trim());
                double total = days * feePerDay;
                totalFeeLabel.setText(String.format("Total Fee: BDT %.2f (%d days)", total, days));
            } catch (NumberFormatException ex) {
                totalFeeLabel.setText("Invalid fee amount!");
            }
        });

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel paymentLabel = createFormLabel("Payment Method");
        String[] methods = {"Cash", "Credit Card", "Debit Card", "bKash", "Nagad", "Rocket"};
        JComboBox<String> paymentCombo = ComfyGoGUI.createStyledComboBox(methods);
        paymentCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        paymentCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton bookGuideBtn = ComfyGoGUI.createStyledButton("Confirm & Hire Guide", new Color(156, 39, 176));
        bookGuideBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookGuideBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        bookGuideBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        bookGuideBtn.addActionListener(e -> {
            int selectedRow = guideTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a guide first!", "No Guide Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String location = locationField.getText().trim();
            String startDate = startDateField.getText().trim();
            
            if (location.isEmpty() || startDate.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter tour location and start date!", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String guideName = String.valueOf(guideModel.getValueAt(selectedRow, 0));
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

                int days = (int) daysSpinner.getValue();
                double feePerDay = Double.parseDouble(feeField.getText().trim());
                double totalFee = days * feePerDay;
                String paymentMethod = (String) paymentCombo.getSelectedItem();

                boolean success = mainFrame.getGuideService().bookGuideWithPayment(
                    mainFrame.getCurrentUserId(),
                    selectedGuide.getGuideId(),
                    location,
                    days,
                    startDate,
                    totalFee,
                    paymentMethod,
                    totalFee
                );

                if (success) {
                    JOptionPane.showMessageDialog(mainFrame,
                        String.format("Guide Booking Confirmed!\n\nGuide: %s\nLocation: %s\nDuration: %d days\nStart Date: %s\nTotal Fee: BDT %.2f\n\nThank you for booking with ComfyGo!",
                            guideName, location, days, startDate, totalFee),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    locationField.setText("");
                    startDateField.setText(java.time.LocalDate.now().plusDays(2).toString());
                    daysSpinner.setValue(1);
                    feeField.setText("500");
                    totalFeeLabel.setText("Total Fee: BDT 0.00");
                    guideTable.clearSelection();
                    selectedGuideLabel.setText("No guide selected");
                    guideDetailsLabel.setText("Click on a guide from the table");

                    contentLayout.show(contentPanel, "BOOKINGS");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Booking failed! Please try again.", "Booking Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        guideTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = guideTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String guideName = String.valueOf(guideModel.getValueAt(selectedRow, 0));
                    String specialization = String.valueOf(guideModel.getValueAt(selectedRow, 1));
                    String rating = String.valueOf(guideModel.getValueAt(selectedRow, 4));
                    selectedGuideLabel.setText("Selected: " + guideName);
                    guideDetailsLabel.setText(specialization + " | Rating: " + rating);
                }
            }
        });

        formPanel.add(formTitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(subtitle);
        formPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        formPanel.add(selectedGuideCard);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(locationLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(locationField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(startDateLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(startDateField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(daysLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(daysSpinner);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(feeLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(feeField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        formPanel.add(calculateFeeBtn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        formPanel.add(totalFeeLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(sep);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(paymentLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(paymentCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        formPanel.add(bookGuideBtn);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    // ========== TRANSPORT PANEL ==========
    private JPanel createTransportPanel() {
        JPanel shell = screenShell("Transportation Options");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel("🚌 Transportation Services Available");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        infoLabel.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        infoLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JTextArea routesArea = new JTextArea(20, 60);
        routesArea.setEditable(false);
        routesArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        routesArea.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        routesArea.setBackground(ComfyGoGUI.SURFACE);
        routesArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        routesArea.setLineWrap(true);
        routesArea.setWrapStyleWord(true);

        List<String> routes = mainFrame.getTransportService().getAllRoutes();
        StringBuilder sb = new StringBuilder();
        sb.append("Available Transport Routes:\n\n");
        
        if (routes != null && !routes.isEmpty()) {
            for (String route : routes) {
                sb.append("  • ").append(route).append("\n");
            }
        } else {
            sb.append("No routes available at the moment.\n");
        }
        
        sb.append("\n");
        sb.append("Available Locations:\n\n");
        List<String> locations = mainFrame.getTransportService().getAllLocations();
        if (locations != null && !locations.isEmpty()) {
            for (String loc : locations) {
                sb.append("  • ").append(loc).append("\n");
            }
        }

        routesArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(routesArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ComfyGoGUI.BORDER_MEDIUM, 1));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(infoLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = ComfyGoGUI.createStyledButton("Refresh Routes", ComfyGoGUI.PRIMARY);
        refreshBtn.addActionListener(e -> {
            List<String> updatedRoutes = mainFrame.getTransportService().getAllRoutes();
            List<String> updatedLocations = mainFrame.getTransportService().getAllLocations();
            
            StringBuilder newSb = new StringBuilder();
            newSb.append("Available Transport Routes:\n\n");
            
            if (updatedRoutes != null && !updatedRoutes.isEmpty()) {
                for (String route : updatedRoutes) {
                    newSb.append("  • ").append(route).append("\n");
                }
            } else {
                newSb.append("No routes available.\n");
            }
            
            newSb.append("\n");
            newSb.append("Available Locations:\n\n");
            if (updatedLocations != null && !updatedLocations.isEmpty()) {
                for (String loc : updatedLocations) {
                    newSb.append("  • ").append(loc).append("\n");
                }
            }
            
            routesArea.setText(newSb.toString());
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshBtn);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        card.add(contentPanel, BorderLayout.CENTER);
        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // ========== BOOKINGS PANEL ==========
    private JPanel createBookingsPanel() {
        JPanel shell = screenShell("My Bookings");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columns = {"Booking ID", "Type", "Name", "Date", "Rooms/Days", "Price", "Status", "Payment"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Booking> hotelBookings = Booking.getBookingsByUser(
            mainFrame.getCurrentUserId(), 
            mainFrame.getConnection()
        );
        
        for (Booking b : hotelBookings) {
            model.addRow(new Object[]{
                b.getBookingId(),
                "Hotel",
                b.getHotelName(),
                b.getCheckInDate(),
                b.getNumberOfRooms() + " rooms",
                "BDT " + String.format("%.2f", b.getTotalPrice()),
                b.getBookingStatus(),
                b.getPaymentId() != null ? "Paid" : "Pending"
            });
        }

        try {
            String sql = "SELECT gb.bookingid, g.guidename, gb.tourlocation, gb.tourdurationdays, " +
                        "gb.guidefee, gb.tourstatus, gb.paymentstatus " +
                        "FROM guidebooking gb " +
                        "LEFT JOIN guides g ON gb.guideid = g.guideid " +
                        "WHERE gb.userid = ? " +
                        "ORDER BY gb.bookingdate DESC";
            
            PreparedStatement ps = mainFrame.getConnection().prepareStatement(sql);
            ps.setString(1, mainFrame.getCurrentUserId());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("bookingid"),
                        "Guide",
                        rs.getString("guidename"),
                        rs.getString("tourlocation"),
                        rs.getInt("tourdurationdays") + " days",
                        "BDT " + String.format("%.2f", rs.getDouble("guidefee")),
                        rs.getString("tourstatus"),
                        rs.getString("paymentstatus")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading guide bookings: " + e.getMessage());
        }

        JTable table = createStyledTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        card.add(sp, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actions.setBackground(ComfyGoGUI.CARD_BG);

        JButton refreshBtn = ComfyGoGUI.createStyledButton("Refresh", ComfyGoGUI.PRIMARY);
        JButton viewBtn = ComfyGoGUI.createStyledButton("View Details", ComfyGoGUI.INFO);
        JButton cancelBtn = ComfyGoGUI.createStyledButton("Cancel Booking", ComfyGoGUI.DANGER);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            
            List<Booking> updated = Booking.getBookingsByUser(
                mainFrame.getCurrentUserId(), 
                mainFrame.getConnection()
            );
            
            for (Booking b : updated) {
                model.addRow(new Object[]{
                    b.getBookingId(), "Hotel", b.getHotelName(), b.getCheckInDate(),
                    b.getNumberOfRooms() + " rooms", "BDT " + String.format("%.2f", b.getTotalPrice()),
                    b.getBookingStatus(), b.getPaymentId() != null ? "Paid" : "Pending"
                });
            }
            
            try {
                String sql = "SELECT gb.bookingid, g.guidename, gb.tourlocation, gb.tourdurationdays, " +
                            "gb.guidefee, gb.tourstatus, gb.paymentstatus " +
                            "FROM guidebooking gb " +
                            "LEFT JOIN guides g ON gb.guideid = g.guideid " +
                            "WHERE gb.userid = ? " +
                            "ORDER BY gb.bookingdate DESC";
                
                PreparedStatement ps = mainFrame.getConnection().prepareStatement(sql);
                ps.setString(1, mainFrame.getCurrentUserId());
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("bookingid"), "Guide", rs.getString("guidename"),
                            rs.getString("tourlocation"), rs.getInt("tourdurationdays") + " days",
                            "BDT " + String.format("%.2f", rs.getDouble("guidefee")),
                            rs.getString("tourstatus"), rs.getString("paymentstatus")
                        });
                    }
                }
            } catch (Exception ex) {
                System.out.println("Error refreshing guide bookings: " + ex.getMessage());
            }
        });

        viewBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a booking!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bookingId = String.valueOf(model.getValueAt(row, 0));
            String type = String.valueOf(model.getValueAt(row, 1));
            showBookingDetails(bookingId, type);
        });

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a booking!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bookingId = String.valueOf(model.getValueAt(row, 0));
            String type = String.valueOf(model.getValueAt(row, 1));

            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to cancel this booking?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = false;
                
                if (type.equals("Hotel")) {
                    success = mainFrame.getHotelService().cancelHotelBookingForUser(
                        mainFrame.getCurrentUserId(), 
                        bookingId
                    );
                } else if (type.equals("Guide")) {
                    try {
                        String sql = "UPDATE guidebooking SET tourstatus = 'CANCELLED', paymentstatus = 'CANCELLED' " +
                                    "WHERE bookingid = ? AND userid = ?";
                        PreparedStatement ps = mainFrame.getConnection().prepareStatement(sql);
                        ps.setString(1, bookingId);
                        ps.setString(2, mainFrame.getCurrentUserId());
                        success = ps.executeUpdate() > 0;
                    } catch (Exception ex) {
                        System.out.println("Error cancelling guide booking: " + ex.getMessage());
                    }
                }

                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Booking cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshBtn.doClick();
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Failed to cancel booking!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actions.add(refreshBtn);
        actions.add(viewBtn);
        actions.add(cancelBtn);
        card.add(actions, BorderLayout.SOUTH);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private void showBookingDetails(String bookingId, String type) {
        if (type.equals("Hotel")) {
            Booking booking = Booking.getBookingById(bookingId, mainFrame.getConnection());
            if (booking == null) {
                JOptionPane.showMessageDialog(mainFrame, "Booking not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String details = String.format(
                "Hotel Booking Details\n\n" +
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
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Guide booking details feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ========== RATINGS PANEL ==========
    private JPanel createRatingsPanel() {
        JPanel shell = screenShell("Rate & Review");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel card = createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel title = new JLabel("Rate Your Experience");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Help others by sharing your feedback");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = createFormLabel("Select Type");
        String[] types = {"Hotel", "Tour Guide"};
        JComboBox<String> typeCombo = ComfyGoGUI.createStyledComboBox(types);
        typeCombo.setMaximumSize(new Dimension(400, 45));
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idLabel = createFormLabel("Enter ID (Hotel/Guide)");
        JTextField idField = ComfyGoGUI.createStyledTextField();
        idField.setMaximumSize(new Dimension(400, 45));
        idField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ratingLabel = createFormLabel("Rating (1-5 stars)");
        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        ratingSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        ratingSpinner.setMaximumSize(new Dimension(400, 45));
        ratingSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel reviewLabel = createFormLabel("Review (Optional)");
        JTextArea reviewArea = new JTextArea(5, 40);
        reviewArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reviewArea.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        reviewArea.setBackground(ComfyGoGUI.SURFACE);
        reviewArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_MEDIUM, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        reviewArea.setLineWrap(true);
        reviewArea.setWrapStyleWord(true);
        reviewArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JScrollPane reviewScroll = new JScrollPane(reviewArea);
        reviewScroll.setMaximumSize(new Dimension(400, 120));
        reviewScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        reviewScroll.setBorder(BorderFactory.createLineBorder(ComfyGoGUI.BORDER_MEDIUM, 1));

        JButton submitBtn = ComfyGoGUI.createStyledButton("Submit Rating", ComfyGoGUI.PRIMARY);
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(300, 55));
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        submitBtn.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String id = idField.getText().trim();
            int rating = (int) ratingSpinner.getValue();
            String review = reviewArea.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter ID!", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = false;
            if (type.equals("Hotel")) {
                success = mainFrame.getRatingService().rateHotel(
                    mainFrame.getCurrentUserId(),
                    id,
                    rating,
                    review
                );
            } else {
                success = mainFrame.getRatingService().rateGuide(
                    mainFrame.getCurrentUserId(),
                    id,
                    rating,
                    review
                );
            }

            if (success) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Thank you for your rating!\n\nYour feedback helps improve our services.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                idField.setText("");
                ratingSpinner.setValue(5);
                reviewArea.setText("");
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                    "Failed to submit rating!\n\nPlease check the ID and try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(subtitle);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(typeLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(typeCombo);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(idLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(idField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(ratingLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(ratingSpinner);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(reviewLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(reviewScroll);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(submitBtn);

        body.add(card, BorderLayout.NORTH);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }
}