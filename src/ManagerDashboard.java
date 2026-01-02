import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ManagerDashboard extends JPanel {

    private final ComfyGoGUI mainFrame;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;

    public ManagerDashboard(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.LIGHT);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ComfyGoGUI.LIGHT);

        contentPanel.add(createHotelPanel(), "HOTEL");
        contentPanel.add(createBookingsPanel(), "BOOKINGS");
        contentPanel.add(createFoodMenuPanel(), "FOOD");
        contentPanel.add(createSettingsPanel(), "SETTINGS");
        contentPanel.add(createRatingsPanel(), "RATINGS");

        add(contentPanel, BorderLayout.CENTER);
        contentLayout.show(contentPanel, "HOTEL");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ComfyGoGUI.PRIMARY);
        sidebar.setPreferredSize(new Dimension(260, 750));
        sidebar.setBorder(new EmptyBorder(18, 16, 18, 16));

        JLabel userLabel = new JLabel(mainFrame.getCurrentUserName() == null ? "Hotel Manager" : mainFrame.getCurrentUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Hotel Manager");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(220, 245, 235));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(userLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 14)));

        sidebar.add(navBtn("My Hotel", "HOTEL"));
        sidebar.add(navBtn("Bookings", "BOOKINGS"));
        sidebar.add(navBtn("Food Menu", "FOOD"));
        sidebar.add(navBtn("Settings", "SETTINGS"));
        sidebar.add(navBtn("Ratings", "RATINGS"));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = ComfyGoGUI.createStyledButton("Logout", ComfyGoGUI.DANGER);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> mainFrame.logout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton navBtn(String text, String panel) {
        JButton btn = ComfyGoGUI.createStyledButton(text, new Color(34, 120, 82));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 42));
        btn.addActionListener(e -> contentLayout.show(contentPanel, panel));
        return btn;
    }

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

    // -------------------- HOTEL --------------------
    private JPanel createHotelPanel() {
        JPanel shell = screenShell("My Hotel");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        Hotel hotel = mainFrame.getHotelService().getHotelByManagerId(mainFrame.getCurrentUserId());

        if (hotel == null) {
            JPanel card = ComfyGoGUI.cardPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(22, 22, 22, 22));

            JLabel msg = new JLabel("No hotel added yet.");
            msg.setFont(new Font("Segoe UI", Font.BOLD, 18));
            msg.setForeground(new Color(90, 40, 40));
            msg.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel tip = new JLabel("Add your hotel to start receiving bookings.");
            tip.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tip.setForeground(new Color(70, 90, 80));
            tip.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton addHotelBtn = ComfyGoGUI.createStyledButton("Add My Hotel", ComfyGoGUI.LIME);
            addHotelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            addHotelBtn.addActionListener(e -> showAddHotelDialog());

            card.add(msg);
            card.add(Box.createRigidArea(new Dimension(0, 6)));
            card.add(tip);
            card.add(Box.createRigidArea(new Dimension(0, 14)));
            card.add(addHotelBtn);

            body.add(card, BorderLayout.NORTH);
            shell.add(body, BorderLayout.CENTER);
            return shell;
        }

        JPanel infoCard = ComfyGoGUI.cardPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        infoCard.add(infoRow("Hotel ID", hotel.getHotelId()));
        infoCard.add(infoRow("Hotel Name", hotel.getHotelName()));
        infoCard.add(infoRow("Location", hotel.getHotelLocation()));
        infoCard.add(infoRow("Price/Night", "BDT " + hotel.getPricePerNight()));
        infoCard.add(infoRow("Rating", hotel.getRating() + " / 5"));
        infoCard.add(infoRow("Room Category", hotel.getRoomCategory()));
        infoCard.add(infoRow("Total Rooms", String.valueOf(hotel.getTotalRooms())));
        infoCard.add(infoRow("Available Rooms", String.valueOf(hotel.getRoomAvailability())));
        infoCard.add(infoRow("Features", hotel.getFeatures()));
        infoCard.add(infoRow("Description", hotel.getDescription()));

        body.add(ComfyGoGUI.scrollWrap(infoCard), BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel infoRow(String k, String v) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel key = new JLabel(k + ":");
        key.setFont(new Font("Segoe UI", Font.BOLD, 13));
        key.setForeground(ComfyGoGUI.DARK);

        JLabel val = new JLabel(v == null || v.isBlank() ? "N/A" : v);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(new Color(70, 90, 80));

        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private void showAddHotelDialog() {
        // FIX: valid constructor (Frame owner)
        JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(this), "Add Hotel", true);
        dialog.setSize(620, 650);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel wrap = new JPanel();
        wrap.setBackground(ComfyGoGUI.LIGHT);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel title = new JLabel("Add Your Hotel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ComfyGoGUI.PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = dialogField(card, "Hotel Name");
        JTextField locationField = dialogField(card, "Location");
        JTextField priceField = dialogField(card, "Price per Night (BDT)");
        JTextField roomsField = dialogField(card, "Total Rooms");
        JTextField categoryField = dialogField(card, "Room Category");
        JTextField featuresField = dialogField(card, "Features");
        JTextField descField = dialogField(card, "Description");

        JButton addBtn = ComfyGoGUI.createStyledButton("Add Hotel", ComfyGoGUI.LIME);
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        addBtn.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                int rooms = Integer.parseInt(roomsField.getText().trim());

                boolean success = mainFrame.getManagerService().addHotel(
                        mainFrame.getCurrentUserId(),
                        nameField.getText().trim(),
                        locationField.getText().trim(),
                        price,
                        rooms,
                        categoryField.getText().trim(),
                        featuresField.getText().trim(),
                        descField.getText().trim()
                );

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Hotel added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    mainFrame.showPanel("MANAGER_DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add hotel. Check console logs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for price and rooms!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(addBtn);

        wrap.add(card);

        dialog.add(ComfyGoGUI.scrollWrap(wrap));
        dialog.setVisible(true);
    }

    private JTextField dialogField(JPanel parent, String label) {
        parent.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel l = ComfyGoGUI.label(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(l);

        parent.add(Box.createRigidArea(new Dimension(0, 6)));

        JTextField f = ComfyGoGUI.createStyledTextField();
        f.setMaximumSize(new Dimension(520, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(f);

        return f;
    }

    // -------------------- BOOKINGS --------------------
    private JPanel createBookingsPanel() {
        JPanel shell = screenShell("Hotel Bookings");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JTextArea bookingsArea = new JTextArea(22, 70);
        bookingsArea.setEditable(false);
        bookingsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        bookingsArea.setBackground(new Color(252, 254, 253));
        bookingsArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        bookingsArea.setText("Press Refresh to load bookings.\n");

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(ComfyGoGUI.scrollWrap(bookingsArea), BorderLayout.CENTER);

        JButton refreshBtn = ComfyGoGUI.createStyledButton("Refresh", ComfyGoGUI.LIME);
        refreshBtn.addActionListener(e -> {
            Hotel hotel = mainFrame.getHotelService().getHotelByManagerId(mainFrame.getCurrentUserId());
            bookingsArea.setText("");

            if (hotel == null) {
                bookingsArea.setText("No hotel found for this manager.\nAdd your hotel first.");
                return;
            }

            List<String> bookings = mainFrame.getManagerService().getHotelBookings(hotel.getHotelId());
            if (bookings == null || bookings.isEmpty()) {
                bookingsArea.setText("No bookings found.");
                return;
            }

            for (String b : bookings) bookingsArea.append(b + "\n");
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setOpaque(false);
        actions.add(refreshBtn);
        card.add(actions, BorderLayout.SOUTH);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // -------------------- FOOD MENU --------------------
    private JPanel createFoodMenuPanel() {
        JPanel shell = screenShell("Food Menu Management");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JTextArea menuArea = new JTextArea(18, 70);
        menuArea.setEditable(false);
        menuArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        menuArea.setBackground(new Color(252, 254, 253));
        menuArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        menuArea.setText("Food menu items will appear here.\n(You can connect this to DB later.)");

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(ComfyGoGUI.scrollWrap(menuArea), BorderLayout.CENTER);

        JButton addBtn = ComfyGoGUI.createStyledButton("Add Item (Demo)", ComfyGoGUI.LIME);
        addBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(mainFrame, "Food menu UI ready (connect DB logic if needed).", "Info", JOptionPane.INFORMATION_MESSAGE)
        );

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setOpaque(false);
        actions.add(addBtn);
        card.add(actions, BorderLayout.SOUTH);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // -------------------- SETTINGS --------------------
    private JPanel createSettingsPanel() {
        JPanel shell = screenShell("Hotel Settings");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel roomsLabel = ComfyGoGUI.label("Update Room Availability");
        roomsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField roomsField = ComfyGoGUI.createStyledTextField();
        roomsField.setMaximumSize(new Dimension(320, 44));
        roomsField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton updateRoomsBtn = ComfyGoGUI.createStyledButton("Update Rooms", ComfyGoGUI.LIME);
        updateRoomsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateRoomsBtn.addActionListener(e -> {
            try {
                int rooms = Integer.parseInt(roomsField.getText().trim());
                boolean ok = mainFrame.getHotelService().updateRoomAvailabilityForManager(mainFrame.getCurrentUserId(), rooms);

                if (ok) JOptionPane.showMessageDialog(mainFrame, "Room availability updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                else JOptionPane.showMessageDialog(mainFrame, "Failed to update rooms!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(roomsLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(roomsField);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(updateRoomsBtn);

        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(new JSeparator());
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel priceLabel = ComfyGoGUI.label("Update Price per Night");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField priceField = ComfyGoGUI.createStyledTextField();
        priceField.setMaximumSize(new Dimension(320, 44));
        priceField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton updatePriceBtn = ComfyGoGUI.createStyledButton("Update Price", ComfyGoGUI.LIME);
        updatePriceBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePriceBtn.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                boolean ok = mainFrame.getHotelService().updateHotelPriceForManager(mainFrame.getCurrentUserId(), price);

                if (ok) JOptionPane.showMessageDialog(mainFrame, "Price updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                else JOptionPane.showMessageDialog(mainFrame, "Failed to update price!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Please enter a valid price!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(priceField);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(updatePriceBtn);

        body.add(ComfyGoGUI.scrollWrap(card), BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    // -------------------- RATINGS --------------------
    private JPanel createRatingsPanel() {
        JPanel shell = screenShell("Hotel Ratings");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JTextArea ratingsArea = new JTextArea(18, 70);
        ratingsArea.setEditable(false);
        ratingsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ratingsArea.setBackground(new Color(252, 254, 253));
        ratingsArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        ratingsArea.setText("Hotel ratings and reviews will appear here.\n");

        Hotel hotel = mainFrame.getHotelService().getHotelByManagerId(mainFrame.getCurrentUserId());
        if (hotel != null) {
            ratingsArea.append("Current Rating: " + hotel.getRating() + " / 5\n");
        } else {
            ratingsArea.append("No hotel found.\n");
        }

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(ComfyGoGUI.scrollWrap(ratingsArea), BorderLayout.CENTER);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }
}