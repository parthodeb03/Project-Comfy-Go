import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ManagerDashboard extends JPanel {
    private final ComfyGoGUI mainFrame;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;

    public ManagerDashboard(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.BACKGROUND);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ComfyGoGUI.BACKGROUND);

        contentPanel.add(createHotelPanel(), "HOTEL");
        contentPanel.add(createBookingsPanel(), "BOOKINGS");
        contentPanel.add(createSettingsPanel(), "SETTINGS");
        contentPanel.add(createRatingsPanel(), "RATINGS");

        add(contentPanel, BorderLayout.CENTER);
        contentLayout.show(contentPanel, "HOTEL");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, ComfyGoGUI.ACCENT, 0, getHeight(), new Color(230, 124, 0));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 800));
        sidebar.setBorder(new EmptyBorder(25, 20, 25, 20));

        JPanel profileBox = new JPanel();
        profileBox.setOpaque(false);
        profileBox.setLayout(new BoxLayout(profileBox, BoxLayout.Y_AXIS));

        JLabel userIcon = new JLabel("🏨");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel(mainFrame.getCurrentUserName() == null ? "Hotel Manager" : mainFrame.getCurrentUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Hotel Manager");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(255, 255, 255, 200));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        profileBox.add(userIcon);
        profileBox.add(Box.createRigidArea(new Dimension(0, 12)));
        profileBox.add(userLabel);
        profileBox.add(Box.createRigidArea(new Dimension(0, 5)));
        profileBox.add(roleLabel);

        sidebar.add(profileBox);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        sidebar.add(navBtn("🏨 My Hotel", "HOTEL"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("📋 Bookings", "BOOKINGS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("⚙️ Settings", "SETTINGS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("⭐ Ratings", "RATINGS"));

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

    private JPanel createHotelPanel() {
        JPanel shell = screenShell("My Hotel");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        Hotel hotel = mainFrame.getHotelService().getHotelByManagerId(mainFrame.getCurrentUserId());

        if (hotel == null) {
            JPanel card = new JPanel();
            card.setBackground(ComfyGoGUI.CARD_BG);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
                new EmptyBorder(40, 40, 40, 40)
            ));

            JLabel msg = new JLabel("No hotel added yet");
            msg.setFont(new Font("Segoe UI", Font.BOLD, 22));
            msg.setForeground(ComfyGoGUI.TEXT_PRIMARY);
            msg.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel tip = new JLabel("Add your hotel to start receiving bookings.");
            tip.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            tip.setForeground(ComfyGoGUI.TEXT_SECONDARY);
            tip.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton addHotelBtn = ComfyGoGUI.createStyledButton("Add My Hotel", ComfyGoGUI.PRIMARY);
            addHotelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            addHotelBtn.setMaximumSize(new Dimension(250, 50));
            addHotelBtn.addActionListener(e -> showAddHotelDialog());

            card.add(msg);
            card.add(Box.createRigidArea(new Dimension(0, 10)));
            card.add(tip);
            card.add(Box.createRigidArea(new Dimension(0, 25)));
            card.add(addHotelBtn);

            body.add(card, BorderLayout.NORTH);
            shell.add(body, BorderLayout.CENTER);
            return shell;
        }

        JPanel infoCard = new JPanel();
        infoCard.setBackground(ComfyGoGUI.CARD_BG);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

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
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel key = new JLabel(k + ":");
        key.setFont(new Font("Segoe UI", Font.BOLD, 15));
        key.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        key.setPreferredSize(new Dimension(150, 25));

        JLabel val = new JLabel(v == null || v.isBlank() ? "N/A" : v);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        val.setForeground(ComfyGoGUI.TEXT_SECONDARY);

        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private void showAddHotelDialog() {
        JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(this), "Add Hotel", true);
        dialog.setSize(700, 700);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel wrap = new JPanel();
        wrap.setBackground(ComfyGoGUI.BACKGROUND);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel card = new JPanel();
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel title = new JLabel("Add Your Hotel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField nameField = dialogField(card, "Hotel Name");
        JTextField locationField = dialogField(card, "Location");
        JTextField priceField = dialogField(card, "Price per Night (BDT)");
        JTextField roomsField = dialogField(card, "Total Rooms");
        JTextField categoryField = dialogField(card, "Room Category");
        JTextField featuresField = dialogField(card, "Features");
        JTextField descField = dialogField(card, "Description");

        JButton addBtn = ComfyGoGUI.createStyledButton("Add Hotel", ComfyGoGUI.SUCCESS);
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

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
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(addBtn);

        wrap.add(card);
        dialog.add(ComfyGoGUI.scrollWrap(wrap));
        dialog.setVisible(true);
    }

    private JTextField dialogField(JPanel parent, String label) {
        parent.add(Box.createRigidArea(new Dimension(0, 12)));
        JLabel l = ComfyGoGUI.label(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(l);
        parent.add(Box.createRigidArea(new Dimension(0, 8)));

        JTextField f = ComfyGoGUI.createStyledTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(f);
        return f;
    }

    private JPanel createBookingsPanel() {
        JPanel shell = screenShell("Hotel Bookings");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextArea bookingsArea = new JTextArea(25, 80);
        bookingsArea.setEditable(false);
        bookingsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingsArea.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        bookingsArea.setBackground(ComfyGoGUI.SURFACE);
        bookingsArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        bookingsArea.setText("Press Refresh to load bookings.\n");

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1));
        card.add(ComfyGoGUI.scrollWrap(bookingsArea), BorderLayout.CENTER);

        JButton refreshBtn = ComfyGoGUI.createStyledButton("Refresh", ComfyGoGUI.PRIMARY);
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

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actions.setBackground(ComfyGoGUI.CARD_BG);
        actions.add(refreshBtn);
        card.add(actions, BorderLayout.SOUTH);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createSettingsPanel() {
        JPanel shell = screenShell("Hotel Settings");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel card = new JPanel();
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel roomsLabel = ComfyGoGUI.label("Update Room Availability");
        roomsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField roomsField = ComfyGoGUI.createStyledTextField();
        roomsField.setMaximumSize(new Dimension(350, 45));
        roomsField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton updateRoomsBtn = ComfyGoGUI.createStyledButton("Update Rooms", ComfyGoGUI.PRIMARY);
        updateRoomsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateRoomsBtn.setMaximumSize(new Dimension(250, 50));

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
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(roomsField);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(updateRoomsBtn);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel priceLabel = ComfyGoGUI.label("Update Price per Night");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField priceField = ComfyGoGUI.createStyledTextField();
        priceField.setMaximumSize(new Dimension(350, 45));
        priceField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton updatePriceBtn = ComfyGoGUI.createStyledButton("Update Price", ComfyGoGUI.PRIMARY);
        updatePriceBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePriceBtn.setMaximumSize(new Dimension(250, 50));

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
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(priceField);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(updatePriceBtn);

        body.add(ComfyGoGUI.scrollWrap(card), BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createRatingsPanel() {
        JPanel shell = screenShell("Hotel Ratings");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextArea ratingsArea = new JTextArea(20, 80);
        ratingsArea.setEditable(false);
        ratingsArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        ratingsArea.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        ratingsArea.setBackground(ComfyGoGUI.SURFACE);
        ratingsArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        ratingsArea.setText("Hotel ratings and reviews will appear here.\n");

        Hotel hotel = mainFrame.getHotelService().getHotelByManagerId(mainFrame.getCurrentUserId());
        if (hotel != null) {
            ratingsArea.append("Current Rating: " + hotel.getRating() + " / 5\n");
        } else {
            ratingsArea.append("No hotel found.\n");
        }

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1));
        card.add(ComfyGoGUI.scrollWrap(ratingsArea), BorderLayout.CENTER);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }
}