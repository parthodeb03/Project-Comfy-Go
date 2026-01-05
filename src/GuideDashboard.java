import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class GuideDashboard extends JPanel {
    private final ComfyGoGUI mainFrame;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;

    public GuideDashboard(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.BACKGROUND);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ComfyGoGUI.BACKGROUND);

        contentPanel.add(createProfilePanel(), "PROFILE");
        contentPanel.add(createBookingsPanel(), "BOOKINGS");
        contentPanel.add(createAvailabilityPanel(), "AVAILABILITY");
        contentPanel.add(createRatingsPanel(), "RATINGS");

        add(contentPanel, BorderLayout.CENTER);
        contentLayout.show(contentPanel, "PROFILE");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, ComfyGoGUI.SECONDARY, 0, getHeight(), new Color(46, 125, 50));
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

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel(mainFrame.getCurrentUserName() == null ? "Tour Guide" : mainFrame.getCurrentUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Tour Guide");
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

        sidebar.add(navBtn("📋 My Profile", "PROFILE"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("📅 My Bookings", "BOOKINGS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("✅ Availability", "AVAILABILITY"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(navBtn("⭐ My Ratings", "RATINGS"));

        sidebar.add(Box.createVerticalGlue());

        JButton logout = ComfyGoGUI.createStyledButton("Logout", ComfyGoGUI.DANGER);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.setMaximumSize(new Dimension(240, 50));
        logout.addActionListener(e -> mainFrame.logout());
        sidebar.add(logout);

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

    private JPanel createProfilePanel() {
        JPanel shell = screenShell("My Profile");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel info = new JPanel();
        info.setBackground(ComfyGoGUI.CARD_BG);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        Guide guide = Guide.getGuideById(mainFrame.getConnection(), mainFrame.getCurrentUserId());

        if (guide == null) {
            JLabel msg = new JLabel("Guide profile not found.");
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            msg.setForeground(ComfyGoGUI.TEXT_SECONDARY);
            info.add(msg);
        } else {
            info.add(infoRow("Guide ID", guide.getGuideId()));
            info.add(infoRow("Name", guide.getGuideName()));
            info.add(infoRow("Email", guide.getGuideEmail()));
            info.add(infoRow("Phone", guide.getGuidePhone()));
            info.add(infoRow("Division", guide.getGuideDivision()));
            info.add(infoRow("District", guide.getGuideDistrict()));
            info.add(infoRow("Languages", guide.getGuideLanguage()));
            info.add(infoRow("Specialization", guide.getSpecialization()));
            info.add(infoRow("Experience", guide.getYearExperience() + " years"));
            info.add(infoRow("Rating", guide.getRating() + " / 5"));
            info.add(infoRow("Status", guide.isAvailable() ? "AVAILABLE" : "NOT AVAILABLE"));
        }

        body.add(ComfyGoGUI.scrollWrap(info), BorderLayout.CENTER);
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

        JLabel val = new JLabel(v == null ? "N/A" : v);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        val.setForeground(ComfyGoGUI.TEXT_SECONDARY);

        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private JPanel createBookingsPanel() {
        JPanel shell = screenShell("My Guide Bookings");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] cols = {"Booking ID", "Tourist", "Days", "Fee", "Status", "Payment", "Location"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<GuideService.GuideBookingInfo> bookings =
            mainFrame.getGuideService().getBookingsForGuide(mainFrame.getCurrentUserId());

        for (GuideService.GuideBookingInfo b : bookings) {
            model.addRow(new Object[]{
                b.getBookingId(),
                b.getTouristName(),
                b.getTourDurationDays(),
                "BDT " + b.getGuideFee(),
                b.getTourStatus(),
                b.getPaymentStatus(),
                b.getTourLocation()
            });
        }

        JTable table = createStyledTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1));
        card.add(sp, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actions.setBackground(ComfyGoGUI.CARD_BG);

        JButton updateBtn = ComfyGoGUI.createStyledButton("Update Status", ComfyGoGUI.PRIMARY);
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a booking!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bookingId = String.valueOf(model.getValueAt(row, 0));
            showUpdateStatusDialog(bookingId);
        });

        actions.add(updateBtn);
        card.add(actions, BorderLayout.SOUTH);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private void showUpdateStatusDialog(String bookingId) {
        String[] statuses = {"PENDING", "CONFIRMED", "REJECTED", "COMPLETED", "CANCELLED"};
        String status = (String) JOptionPane.showInputDialog(
            mainFrame,
            "Select new tour status:",
            "Update Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            statuses[0]
        );

        if (status == null) return;

        boolean ok = mainFrame.getGuideService().updateTourStatusForGuide(mainFrame.getCurrentUserId(), bookingId, status);
        if (ok) {
            JOptionPane.showMessageDialog(mainFrame, "Status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showPanel("GUIDE_DASHBOARD");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update status!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createAvailabilityPanel() {
        JPanel shell = screenShell("Manage Availability");
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

        // DYNAMIC STATUS LABEL - Will refresh on button click
        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Method to refresh status display
        Runnable refreshStatus = () -> {
            Guide guide = Guide.getGuideById(mainFrame.getConnection(), mainFrame.getCurrentUserId());
            boolean available = guide != null && guide.isAvailable();
            statusLabel.setText("Current Status: " + (available ? "AVAILABLE" : "NOT AVAILABLE"));
            statusLabel.setForeground(available ? ComfyGoGUI.SUCCESS : ComfyGoGUI.DANGER);
        };

        // Initial status load
        refreshStatus.run();

        JButton setAvail = ComfyGoGUI.createStyledButton("Set AVAILABLE", ComfyGoGUI.SUCCESS);
        JButton setUnavail = ComfyGoGUI.createStyledButton("Set NOT AVAILABLE", ComfyGoGUI.DANGER);

        setAvail.setAlignmentX(Component.LEFT_ALIGNMENT);
        setUnavail.setAlignmentX(Component.LEFT_ALIGNMENT);
        setAvail.setMaximumSize(new Dimension(300, 50));
        setUnavail.setMaximumSize(new Dimension(300, 50));

        setAvail.addActionListener(e -> {
            boolean ok = mainFrame.getGuideService().setGuideAvailability(mainFrame.getCurrentUserId(), true);
            if (ok) {
                refreshStatus.run(); // REFRESH STATUS IMMEDIATELY
                JOptionPane.showMessageDialog(mainFrame, "You are now AVAILABLE for bookings!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Cannot set AVAILABLE! You may have active tours.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setUnavail.addActionListener(e -> {
            boolean ok = mainFrame.getGuideService().setGuideAvailability(mainFrame.getCurrentUserId(), false);
            if (ok) {
                refreshStatus.run(); // REFRESH STATUS IMMEDIATELY
                JOptionPane.showMessageDialog(mainFrame, "You are now NOT AVAILABLE for bookings!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Failed to update availability!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(statusLabel);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(setAvail);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(setUnavail);

        body.add(card, BorderLayout.NORTH);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createRatingsPanel() {
        JPanel shell = screenShell("My Ratings & Reviews");
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 0, 0, 0));

        JTextArea area = new JTextArea(20, 70);
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        area.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        area.setBackground(ComfyGoGUI.SURFACE);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));

        Guide guide = Guide.getGuideById(mainFrame.getConnection(), mainFrame.getCurrentUserId());
        if (guide != null) {
            area.setText("Current Rating: " + guide.getRating() + " / 5\n"
                + "Total Ratings: " + guide.getTotalRatings() + "\n\n"
                + "Detailed reviews can be displayed here (if stored in DB).");
        } else {
            area.setText("No rating data found.");
        }

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1));
        card.add(ComfyGoGUI.scrollWrap(area), BorderLayout.CENTER);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        table.setBackground(ComfyGoGUI.SURFACE);
        table.setRowHeight(40);
        table.setGridColor(ComfyGoGUI.BORDER_LIGHT);
        table.setSelectionBackground(new Color(26, 115, 232, 30));
        table.setSelectionForeground(ComfyGoGUI.TEXT_PRIMARY);

        // FIXED TABLE HEADER - NOW VISIBLE WITH CUSTOM RENDERER
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(ComfyGoGUI.PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setReorderingAllowed(false);
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value == null ? "" : value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                label.setForeground(Color.WHITE);
                label.setBackground(ComfyGoGUI.PRIMARY);
                label.setOpaque(true);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                return label;
            }
        });

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }
}