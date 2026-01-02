import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WelcomePanel extends JPanel {

    public WelcomePanel(ComfyGoGUI mainFrame) {
        setLayout(new BorderLayout(0, 0));
        setBackground(ComfyGoGUI.LIGHT);

        // ---------- Header (nature gradient) ----------
        JPanel header = new GradientHeader(
                ComfyGoGUI.FOREST,
                new Color(40, 160, 95)
        );
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(26, 28, 26, 28));

        JLabel title = new JLabel("ComfyGo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 44));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Tourism Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(235, 255, 245));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createRigidArea(new Dimension(0, 6)));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);

        JLabel badge = new JLabel("Nature Edition");
        badge.setOpaque(true);
        badge.setBackground(new Color(255, 255, 255, 40));
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setBorder(new EmptyBorder(8, 12, 8, 12));
        header.add(badge, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ---------- Center content ----------
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ComfyGoGUI.LIGHT);
        center.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(26, 30, 26, 30));

        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setForeground(ComfyGoGUI.DARK);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info = new JLabel("<html>"
                + "<div style='width:520px;'>"
                + "Sign in to access dashboards for tourists, tour guides, and hotel managers. "
                + "Create a new account if you're new to ComfyGo."
                + "</div></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(new Color(70, 90, 80));
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actions = new JPanel(new GridLayout(1, 2, 14, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = ComfyGoGUI.createStyledButton("Login", ComfyGoGUI.PRIMARY);
        JButton registerBtn = ComfyGoGUI.createStyledButton("Register", ComfyGoGUI.LIME);

        loginBtn.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        registerBtn.addActionListener(e -> mainFrame.showPanel("REGISTER"));

        actions.add(loginBtn);
        actions.add(registerBtn);

        // Quick links row (small buttons)
        JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quick.setOpaque(false);
        quick.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton aboutBtn = smallLinkButton("About");
        JButton tipsBtn = smallLinkButton("Tips");
        JButton exitBtn = smallLinkButton("Exit");

        aboutBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                mainFrame,
                "ComfyGo is a tourism management system demo project.\n"
                        + "Use Register/Login to continue.",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        ));

        tipsBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                mainFrame,
                "Tip: Ensure MySQL is running and the comfygo database is imported.\n"
                        + "Then login with a registered account.",
                "Tips",
                JOptionPane.INFORMATION_MESSAGE
        ));

        exitBtn.addActionListener(e -> System.exit(0));

        quick.add(aboutBtn);
        quick.add(tipsBtn);
        quick.add(exitBtn);

        // Build card layout
        card.add(welcome);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(info);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(actions);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(new JSeparator());
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(quick);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        center.add(card, gbc);

        add(center, BorderLayout.CENTER);

        // ---------- Footer ----------
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(ComfyGoGUI.LIGHT);
        footer.setBorder(new EmptyBorder(10, 24, 14, 24));

        JLabel footLeft = new JLabel("© ComfyGo");
        footLeft.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footLeft.setForeground(new Color(90, 110, 100));

        JLabel footRight = new JLabel("Dhaka • Tourism • Hotels • Guides");
        footRight.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footRight.setForeground(new Color(90, 110, 100));

        footer.add(footLeft, BorderLayout.WEST);
        footer.add(footRight, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private JButton smallLinkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(ComfyGoGUI.PRIMARY);
        b.setBackground(new Color(0, 0, 0, 0));
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Simple gradient panel for header
    private static class GradientHeader extends JPanel {
        private final Color left;
        private final Color right;

        GradientHeader(Color left, Color right) {
            this.left = left;
            this.right = right;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, left, w, h, right);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 22, 22);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}