import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(ComfyGoGUI mainFrame) {
        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.LIGHT);

        // -------- Header ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ComfyGoGUI.PRIMARY);
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JButton backBtn = ComfyGoGUI.createStyledButton("← Back", ComfyGoGUI.SECONDARY);
        backBtn.addActionListener(e -> mainFrame.showPanel("WELCOME"));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel hint = new JLabel("Use your registered email and password to continue");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(new Color(230, 250, 240));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(hint);

        header.add(backBtn, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // -------- Center card ----------
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ComfyGoGUI.LIGHT);
        center.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel emailLabel = ComfyGoGUI.label("Email");
        JTextField emailField = ComfyGoGUI.createStyledTextField();
        emailField.setMaximumSize(new Dimension(520, 44));

        JLabel passLabel = ComfyGoGUI.label("Password");
        JPasswordField passwordField = ComfyGoGUI.createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(520, 44));

        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPass.setForeground(new Color(60, 80, 70));
        showPass.setBackground(ComfyGoGUI.SURFACE);
        showPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        char defaultEcho = passwordField.getEchoChar();
        showPass.addActionListener(e -> passwordField.setEchoChar(showPass.isSelected() ? (char) 0 : defaultEcho));

        JButton loginBtn = ComfyGoGUI.createStyledButton("Login", ComfyGoGUI.LIME);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Extra access buttons
        JPanel linksRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
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
        linksRow.add(clearBtn);

        // Login action
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

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

        // Enter key triggers login
        passwordField.addActionListener(e -> loginBtn.doClick());

        // Build layout
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(emailLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(emailField);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(passwordField);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(showPass);
        card.add(Box.createRigidArea(new Dimension(0, 18)));
        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 14)));
        card.add(new JSeparator());
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(linksRow);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(card, gbc);

        add(center, BorderLayout.CENTER);
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
}
