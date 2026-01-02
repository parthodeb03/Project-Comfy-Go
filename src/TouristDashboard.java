import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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

        sidebar.add(navBtn("Dashboard", "HOME"));
        sidebar.add(navBtn("Hotels", "HOTELS"));
        sidebar.add(navBtn("Tourist Spots", "SPOTS"));
        sidebar.add(navBtn("Tour Guides", "GUIDES"));
        sidebar.add(navBtn("Transport", "TRANSPORT"));
        sidebar.add(navBtn("My Bookings", "BOOKINGS"));
        sidebar.add(navBtn("Rate & Review", "RATINGS"));

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
        btn.setMaximumSize(new Dimension(220, 42));
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

        JTextArea bookingsArea = new JTextArea(20, 60);
        bookingsArea.setEditable(false);
        bookingsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        bookingsArea.setBackground(new Color(252, 254, 253));
        bookingsArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        bookingsArea.setText("Your hotel and transport bookings will appear here.");

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(ComfyGoGUI.scrollWrap(bookingsArea), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));
        body.add(card, BorderLayout.CENTER);

        shell.add(body, BorderLayout.CENTER);
        return shell;
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

        JButton submitBtn = ComfyGoGUI.createStyledButton("Submit Rating (Demo)", ComfyGoGUI.LIME);
        submitBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(mainFrame, "Rating submitted (demo UI).", "Success", JOptionPane.INFORMATION_MESSAGE)
        );

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
        // FIX: Use Frame owner (valid constructor everywhere)
        JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(this), "Book Hotel", true);
        dialog.setSize(560, 560);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel panel = new JPanel();
        panel.setBackground(ComfyGoGUI.LIGHT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Booking: " + hotelName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ComfyGoGUI.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField checkInField = dialogField(card, "Check-in (YYYY-MM-DD)");
        JTextField checkOutField = dialogField(card, "Check-out (YYYY-MM-DD)");
        JTextField roomsField = dialogField(card, "Number of Rooms");
        JTextField paymentField = dialogField(card, "Payment Amount");

        JLabel methodLabel = ComfyGoGUI.label("Payment Method");
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] methods = {"Cash", "Bkash", "Nagad", "Card"};
        JComboBox<String> methodCombo = ComfyGoGUI.createStyledComboBox(methods);
        methodCombo.setMaximumSize(new Dimension(420, 44));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton confirm = ComfyGoGUI.createStyledButton("Confirm Booking", ComfyGoGUI.LIME);
        confirm.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirm.addActionListener(e -> {
            String checkIn = checkInField.getText().trim();
            String checkOut = checkOutField.getText().trim();
            String roomsTxt = roomsField.getText().trim();
            String amountTxt = paymentField.getText().trim();
            String method = String.valueOf(methodCombo.getSelectedItem());

            if (checkIn.isEmpty() || checkOut.isEmpty() || roomsTxt.isEmpty() || amountTxt.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int rooms;
            double amount;
            try {
                rooms = Integer.parseInt(roomsTxt);
                amount = Double.parseDouble(amountTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Rooms and amount must be numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dialog.dispose();
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Booking submitted!\nHotel: " + hotelName
                            + "\nCheck-in: " + checkIn
                            + "\nCheck-out: " + checkOut
                            + "\nRooms: " + rooms
                            + "\nPayment: " + amount + " (" + method + ")",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(methodLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(methodCombo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(confirm);

        panel.add(card);

        dialog.add(ComfyGoGUI.scrollWrap(panel));
        dialog.setVisible(true);
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
}