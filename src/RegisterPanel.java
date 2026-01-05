import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private final ComfyGoGUI mainFrame;
    private JComboBox<String> roleCombo;
    private JPanel formContainer;
    private CardLayout formLayout;

    public RegisterPanel(ComfyGoGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ComfyGoGUI.BACKGROUND);

        // Header
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                    0, 0, ComfyGoGUI.SUCCESS,
                    getWidth(), 0, new Color(56, 142, 60)
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

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Join as Tourist, Tour Guide, or Hotel Manager");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        titleBox.add(title);
        titleBox.add(Box.createRigidArea(new Dimension(0, 5)));
        titleBox.add(subtitle);

        header.add(backBtn, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Center content
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(ComfyGoGUI.BACKGROUND);
        center.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel();
        card.setBackground(ComfyGoGUI.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ComfyGoGUI.BORDER_LIGHT, 1),
            new EmptyBorder(30, 35, 30, 35)
        ));

        // Role selector
        JLabel roleLabel = ComfyGoGUI.label("Select Account Type");
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] roles = {"Tourist", "Tour Guide", "Hotel Manager"};
        roleCombo = ComfyGoGUI.createStyledComboBox(roles);
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Forms container with CardLayout
        formLayout = new CardLayout();
        formContainer = new JPanel(formLayout);
        formContainer.setOpaque(false);
        formContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(createTouristForm(), "Tourist");
        formContainer.add(createGuideForm(), "Tour Guide");
        formContainer.add(createManagerForm(), "Hotel Manager");

        roleCombo.addActionListener(e -> switchForm());

        card.add(roleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(roleCombo);
        card.add(Box.createRigidArea(new Dimension(0, 25)));
        card.add(formContainer);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        center.add(scroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Show initial form
        switchForm();
    }

    private void switchForm() {
        String role = (String) roleCombo.getSelectedItem();
        if (role == null) role = "Tourist";
        formLayout.show(formContainer, role);
        revalidate();
        repaint();
    }

    private JPanel createTouristForm() {
        JPanel panel = formSectionPanel();

        JTextField nameField = createField(panel, "Full Name");
        JTextField emailField = createField(panel, "Email Address");
        JTextField phoneField = createField(panel, "Phone Number");
        JTextField countryField = createField(panel, "Country");
        JTextField nidField = createField(panel, "NID/Passport Number");
        JTextField dobField = createField(panel, "Date of Birth (YYYY-MM-DD)");
        JTextField addressField = createField(panel, "Full Address");
        JPasswordField passwordField = createPasswordField(panel, "Password");

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton registerBtn = ComfyGoGUI.createStyledButton("Register as Tourist", ComfyGoGUI.PRIMARY);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        registerBtn.addActionListener(e -> {
            boolean success = mainFrame.getAuthService().registerTourist(
                nameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                nidField.getText().trim(),
                "",
                dobField.getText().trim(),
                countryField.getText().trim(),
                addressField.getText().trim(),
                new String(passwordField.getPassword())
            );

            if (success) {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                mainFrame.showPanel("LOGIN");
            } else {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Registration failed! Please check your inputs.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton goLogin = smallLinkButton("Already have an account? Login here");
        goLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        goLogin.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        panel.add(goLogin);

        return panel;
    }

    private JPanel createGuideForm() {
        JPanel panel = formSectionPanel();

        JTextField nameField = createField(panel, "Full Name");
        JTextField emailField = createField(panel, "Email Address");
        JTextField phoneField = createField(panel, "Phone Number");

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel divLabel = ComfyGoGUI.label("Division");
        divLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(divLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        String[] divisions = {"Dhaka", "Chittagong", "Khulna", "Rajshahi", "Barisal", "Sylhet", "Rangpur", "Mymensingh"};
        JComboBox<String> divisionCombo = ComfyGoGUI.createStyledComboBox(divisions);
        divisionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        divisionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(divisionCombo);

        JTextField districtField = createField(panel, "District");
        JTextField languagesField = createField(panel, "Languages (comma-separated)");
        JTextField specializationField = createField(panel, "Specialization");
        JTextField experienceField = createField(panel, "Years of Experience");
        JPasswordField passwordField = createPasswordField(panel, "Password");

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton registerBtn = ComfyGoGUI.createStyledButton("Register as Tour Guide", ComfyGoGUI.SECONDARY);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        registerBtn.addActionListener(e -> {
            int experience;
            try {
                experience = Integer.parseInt(experienceField.getText().trim());
            } catch (NumberFormatException ex) {
                experience = 0;
            }

            boolean success = mainFrame.getAuthService().registerGuide(
                nameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                (String) divisionCombo.getSelectedItem(),
                districtField.getText().trim(),
                languagesField.getText().trim(),
                specializationField.getText().trim(),
                experience,
                new String(passwordField.getPassword())
            );

            if (success) {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                mainFrame.showPanel("LOGIN");
            } else {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Registration failed! Please check inputs.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton goLogin = smallLinkButton("Already have an account? Login here");
        goLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        goLogin.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        panel.add(goLogin);

        return panel;
    }

    private JPanel createManagerForm() {
        JPanel panel = formSectionPanel();

        JTextField nameField = createField(panel, "Full Name");
        JTextField emailField = createField(panel, "Email Address");
        JTextField phoneField = createField(panel, "Phone Number");
        JTextField managerNidField = createField(panel, "Manager NID");
        JTextField hotelNameField = createField(panel, "Hotel Name");
        JTextField hotelNidField = createField(panel, "Hotel NID/License");
        JTextField registrationField = createField(panel, "Registration Number");
        JPasswordField passwordField = createPasswordField(panel, "Password");

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton registerBtn = ComfyGoGUI.createStyledButton("Register as Hotel Manager", ComfyGoGUI.ACCENT);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        registerBtn.addActionListener(e -> {
            boolean success = mainFrame.getAuthService().registerManager(
                nameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                managerNidField.getText().trim(),
                hotelNameField.getText().trim(),
                hotelNidField.getText().trim(),
                registrationField.getText().trim(),
                new String(passwordField.getPassword())
            );

            if (success) {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                mainFrame.showPanel("LOGIN");
            } else {
                JOptionPane.showMessageDialog(
                    mainFrame,
                    "Registration failed! Please check inputs.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton goLogin = smallLinkButton("Already have an account? Login here");
        goLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        goLogin.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        panel.add(goLogin);

        return panel;
    }

    private JPanel formSectionPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(0, 0, 0, 0));
        return p;
    }

    private JTextField createField(JPanel panel, String labelText) {
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        JLabel label = ComfyGoGUI.label(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JTextField field = ComfyGoGUI.createStyledTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        return field;
    }

    private JPasswordField createPasswordField(JPanel panel, String labelText) {
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        JLabel label = ComfyGoGUI.label(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JPasswordField field = ComfyGoGUI.createStyledPasswordField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);

        JCheckBox show = new JCheckBox("Show password");
        show.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        show.setForeground(ComfyGoGUI.TEXT_SECONDARY);
        show.setBackground(ComfyGoGUI.CARD_BG);
        show.setAlignmentX(Component.LEFT_ALIGNMENT);
        show.setFocusPainted(false);

        char defaultEcho = field.getEchoChar();
        show.addActionListener(e -> field.setEchoChar(show.isSelected() ? (char) 0 : defaultEcho));

        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(show);

        return field;
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