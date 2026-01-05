import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel(ComfyGoGUI mainFrame) {
        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.BACKGROUND);

        // ========== Header ==========
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                    0, 0, ComfyGoGUI.PRIMARY,
                    getWidth(), 0, ComfyGoGUI.PRIMARY_DARK
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(25, 35, 25, 35));
        header.setPreferredSize(new Dimension(0, 100));

        JButton backBtn = ComfyGoGUI.createStyledButton("← Back", new Color(255, 255, 255, 30));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> mainFrame.showPanel("WELCOME"));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel hint = new JLabel("Enter your credentials to continue");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hint.setForeground(new Color(255, 255, 255, 200));

        titleBox.add(title);
        titleBox.add(Box.createRigidArea(new Dimension(0, 5)));
        titleBox.add(hint);

        header.add(backBtn, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ========== Center Card ==========
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ComfyGoGUI.BACKGROUND);
        center.setBorder(new EmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(40, 40, 40, 40)
        ));
        card.setMaximumSize(new Dimension(500, 600));

        JLabel emailLabel = ComfyGoGUI.label("Email Address");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField emailField = ComfyGoGUI.createStyledTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel passLabel = ComfyGoGUI.label("Password");
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField passwordField = ComfyGoGUI.createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPass.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        showPass.setBackground(ComfyGoGUI.CARD_BG);
        showPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPass.setFocusPainted(false);

        char defaultEcho = passwordField.getEchoChar();
        showPass.addActionListener(e -> passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : defaultEcho));

        JButton loginBtn = ComfyGoGUI.createStyledButton("Login", ComfyGoGUI.PRIMARY);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JPanel linksRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        linksRow.setOpaque(false);
        linksRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton registerLink = smallLinkButton("Create new account");
        registerLink.addActionListener(e -> mainFrame.showPanel("REGISTER"));

        JButton clearBtn = smallLinkButton("Clear");
        clearBtn.addActionListener(e -> {
            emailField.setText("");
            passwordField.setText("");
            emailField.requestFocusInWindow();
        });

        linksRow.add(registerLink);
        linksRow.add(new JLabel("|"));
        linksRow.add(clearBtn);

        // Login action
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Please fill in all fields!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            var user = mainFrame.getAuthService().login(email, password);
            if (user != null) {
                mainFrame.setCurrentUserId(user.getId());
                mainFrame.setCurrentUserName(user.getName());
                mainFrame.setCurrentUserRole(user.getRole());
                switch (user.getRole()) {
                    case "Tourist" -> mainFrame.showPanel("TOURIST_DASHBOARD");
                    case "Tour Guide" -> mainFrame.showPanel("GUIDE_DASHBOARD");
                    case "Hotel Manager" -> mainFrame.showPanel("MANAGER_DASHBOARD");
                    default -> JOptionPane.showMessageDialog(
                        mainFrame,
                        "Login succeeded but role is unknown: " + user.getRole(),
                        "Role Error",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Invalid email or password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        passwordField.addActionListener(e -> loginBtn.doClick());

        // Build layout
        card.add(emailLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(emailField);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(showPass);
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(new JSeparator());
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(linksRow);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        center.add(card, gbc);

        add(center, BorderLayout.CENTER);
    }

    private JButton smallLinkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(ComfyGoGUI.PRIMARY);
        b.setBackground(new Color(0, 0, 0, 0));
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setForeground(ComfyGoGUI.PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setForeground(ComfyGoGUI.PRIMARY);
            }
        });
        return b;
    }
}
