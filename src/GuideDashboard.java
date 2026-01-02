import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuideDashboard extends JPanel {

    private final ComfyGoGUI mainFrame;
    private final JPanel contentPanel;
    private final CardLayout contentLayout;

    public GuideDashboard(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.LIGHT);

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ComfyGoGUI.LIGHT);

        contentPanel.add(createProfilePanel(), "PROFILE");
        contentPanel.add(createBookingsPanel(), "BOOKINGS");
        contentPanel.add(createAvailabilityPanel(), "AVAILABILITY");
        contentPanel.add(createRatingsPanel(), "RATINGS");

        add(contentPanel, BorderLayout.CENTER);
        contentLayout.show(contentPanel, "PROFILE");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ComfyGoGUI.PRIMARY);
        sidebar.setPreferredSize(new Dimension(260, 750));
        sidebar.setBorder(new EmptyBorder(18, 16, 18, 16));

        JLabel userLabel = new JLabel(mainFrame.getCurrentUserName() == null ? "Tour Guide" : mainFrame.getCurrentUserName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel("Tour Guide");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(220, 245, 235));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(userLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 18)));
        sidebar.add(new JSeparator());
        sidebar.add(Box.createRigidArea(new Dimension(0, 14)));

        sidebar.add(navBtn("My Profile", "PROFILE"));
        sidebar.add(navBtn("My Bookings", "BOOKINGS"));
        sidebar.add(navBtn("Availability", "AVAILABILITY"));
        sidebar.add(navBtn("My Ratings", "RATINGS"));

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

    private JPanel createProfilePanel() {
        JPanel shell = screenShell("My Profile");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel info = ComfyGoGUI.cardPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(16, 16, 16, 16));

        Guide guide = Guide.getGuideById(mainFrame.getConnection(), mainFrame.getCurrentUserId());

        if (guide == null) {
            JLabel msg = new JLabel("Guide profile not found.");
            msg.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            msg.setForeground(new Color(80, 95, 88));
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
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel key = new JLabel(k + ":");
        key.setFont(new Font("Segoe UI", Font.BOLD, 13));
        key.setForeground(ComfyGoGUI.DARK);

        JLabel val = new JLabel(v == null ? "N/A" : v);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(new Color(70, 90, 80));

        row.add(key, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private JPanel createBookingsPanel() {
        JPanel shell = screenShell("My Guide Bookings");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        String[] cols = {"Booking ID", "Tourist", "Days", "Fee", "Status", "Payment", "Location"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

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

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setBackground(new Color(252, 254, 253));
        table.setGridColor(new Color(225, 235, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(ComfyGoGUI.PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(ComfyGoGUI.scrollWrap(table), BorderLayout.CENTER);

        JButton updateBtn = ComfyGoGUI.createStyledButton("Update Status", ComfyGoGUI.LIME);
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a booking!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bookingId = String.valueOf(model.getValueAt(row, 0));
            showUpdateStatusDialog(bookingId);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setOpaque(false);
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
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        Guide guide = Guide.getGuideById(mainFrame.getConnection(), mainFrame.getCurrentUserId());
        boolean available = guide != null && guide.isAvailable();

        JLabel statusLabel = new JLabel("Current Status: " + (available ? "AVAILABLE" : "NOT AVAILABLE"));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setForeground(available ? ComfyGoGUI.SUCCESS : ComfyGoGUI.DANGER);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton setAvail = ComfyGoGUI.createStyledButton("Set AVAILABLE", ComfyGoGUI.LIME);
        JButton setUnavail = ComfyGoGUI.createStyledButton("Set NOT AVAILABLE", ComfyGoGUI.DANGER);
        setAvail.setAlignmentX(Component.LEFT_ALIGNMENT);
        setUnavail.setAlignmentX(Component.LEFT_ALIGNMENT);

        setAvail.addActionListener(e -> {
            boolean ok = mainFrame.getGuideService().setGuideAvailability(mainFrame.getCurrentUserId(), true);
            if (ok) {
                JOptionPane.showMessageDialog(mainFrame, "Availability updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPanel("GUIDE_DASHBOARD");
            }
        });

        setUnavail.addActionListener(e -> {
            boolean ok = mainFrame.getGuideService().setGuideAvailability(mainFrame.getCurrentUserId(), false);
            if (ok) {
                JOptionPane.showMessageDialog(mainFrame, "Availability updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showPanel("GUIDE_DASHBOARD");
            }
        });

        card.add(statusLabel);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(setAvail);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(setUnavail);

        body.add(card, BorderLayout.NORTH);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }

    private JPanel createRatingsPanel() {
        JPanel shell = screenShell("My Ratings & Reviews");

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 0, 0, 0));

        JTextArea area = new JTextArea(18, 60);
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setBackground(new Color(252, 254, 253));
        area.setBorder(new EmptyBorder(12, 12, 12, 12));

        Guide guide = Guide.getGuideById(mainFrame.getConnection(), mainFrame.getCurrentUserId());
        if (guide != null) {
            area.setText("Current Rating: " + guide.getRating() + " / 5\n"
                    + "Total Ratings: " + guide.getTotalRatings() + "\n\n"
                    + "Detailed reviews can be displayed here (if stored in DB).");
        } else {
            area.setText("No rating data found.");
        }

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BorderLayout());
        card.add(ComfyGoGUI.scrollWrap(area), BorderLayout.CENTER);

        body.add(card, BorderLayout.CENTER);
        shell.add(body, BorderLayout.CENTER);
        return shell;
    }
}