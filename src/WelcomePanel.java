import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WelcomePanel extends JPanel {
    public WelcomePanel(ComfyGoGUI mainFrame) {
        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.BACKGROUND);

        // ========== Modern Header with Gradient ==========
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                    0, 0, ComfyGoGUI.PRIMARY,
                    getWidth(), getHeight(), ComfyGoGUI.PRIMARY_DARK
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(40, 50, 40, 50));
        header.setPreferredSize(new Dimension(0, 180));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("ComfyGo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 56));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Your Perfect Tourism Management Partner");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(255, 255, 255, 230));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ========== Center Content ==========
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ComfyGoGUI.BACKGROUND);
        center.setBorder(new EmptyBorder(50, 50, 50, 50));

        JPanel card = new JPanel();
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(40, 50, 40, 50)
        ));
        card.setMaximumSize(new Dimension(600, 500));

        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcome.setForeground(ComfyGoGUI.TEXT_PRIMARY);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel("<html><div style='text-align: center;'>" +
            "Explore tourist destinations, book hotels, hire guides,<br>" +
            "and manage your tours all in one place." +
            "</div></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        info.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn = ComfyGoGUI.createStyledButton("Login", ComfyGoGUI.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(180, 48));
        loginBtn.addActionListener(e -> mainFrame.showPanel("LOGIN"));

        JButton registerBtn = ComfyGoGUI.createStyledButton("Register", ComfyGoGUI.SUCCESS);
        registerBtn.setPreferredSize(new Dimension(180, 48));
        registerBtn.addActionListener(e -> mainFrame.showPanel("REGISTER"));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        card.add(welcome);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(info);
        card.add(Box.createRigidArea(new Dimension(0, 40)));
        card.add(buttonPanel);

        // Features Panel
        JPanel featuresPanel = new JPanel();
        featuresPanel.setOpaque(false);
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel featuresTitle = new JLabel("Key Features");
        featuresTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        featuresTitle.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        featuresTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] features = {
            "🏨 Book Hotels & Accommodations",
            "🗺️ Discover Tourist Spots",
            "👤 Hire Professional Tour Guides",
            "🚌 Transportation Management"
        };

        featuresPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        featuresPanel.add(featuresTitle);
        featuresPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            featureLabel.setForeground(ComfyGoGUI.TEXT_SECONDARY);
            featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featuresPanel.add(featureLabel);
            featuresPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        card.add(featuresPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        center.add(card, gbc);

        add(center, BorderLayout.CENTER);
    }
}
