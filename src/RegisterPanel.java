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
        setBackground(ComfyGoGUI.LIGHT);

        // -------- Header ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ComfyGoGUI.PRIMARY);
        header.setBorder(new EmptyBorder(18, 22, 18, 22));

        JButton backBtn = ComfyGoGUI.createStyledButton("← Back", ComfyGoGUI.SECONDARY);
        backBtn.addActionListener(e -> mainFrame.showPanel("WELCOME"));

        JLabel title = new JLabel("Create an account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Register as Tourist, Tour Guide, or Hotel Manager");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(230, 250, 240));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBox.add(subtitle);

        header.add(backBtn, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // -------- Center (scrollable) ----------
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ComfyGoGUI.LIGHT);
        center.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel card = ComfyGoGUI.cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Role selector row
        JLabel roleLabel = ComfyGoGUI.label("Register as");
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] roles = {"Tourist", "Tour Guide", "Hotel Manager"};
        roleCombo = ComfyGoGUI.createStyledComboBox(roles);
        roleCombo.setMaximumSize(new Dimension(520, 44));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Forms container
        formLayout = new CardLayout();
        formContainer = new JPanel(formLayout);
        formContainer.setOpaque(false);
        formContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(createTouristForm(), "Tourist");
        formContainer.add(createGuideForm(), "Tour Guide");
        formContainer.add(createManagerForm(), "Hotel Manager");

        roleCombo.addActionListener(e -> switchForm());

        // Build card
        card.add(roleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(roleCombo);
        card.add(Box.createRigidArea(new Dimension(0, 16)));
        card.add(formContainer);

        // Scroll wrapper (so no overflowing / no cut UI)
        JScrollPane scroll = ComfyGoGUI.scrollWrap(card);
        scroll.setPreferredSize(new Dimension(760, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(scroll, gbc);

        add(center, BorderLayout.CENTER);

        // Show initial
        switchForm();
    }

    private void switchForm() {
        String role = (String) roleCombo.getSelectedItem();
        if (role == null) role = "Tourist";
        formLayout.show(formContainer, role);
        revalidate();
        repaint();
    }

    // -------------------- Forms --------------------

    private JPanel createTouristForm() {
        JPanel panel = formSectionPanel();

        JTextField nameField = createField(panel, "Full Name");
        JTextField emailField = createField(panel, "Email");
        JTextField phoneField = createField(panel, "Phone Number");
        JTextField countryField = createField(panel, "Country");
        JTextField nidField = createField(panel, "NID/Passport");
        JTextField dobField = createField(panel, "Date of Birth (YYYY-MM-DD)");
        JTextField addressField = createField(panel, "Address");
        JPasswordField passwordField = createPasswordField(panel, "Password");

        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton registerBtn = ComfyGoGUI.createStyledButton("Register as Tourist", ComfyGoGUI.LIME);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        registerBtn.addActionListener(e -> {
            boolean success = mainFrame.getAuthService().registerTourist(
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    nidField.getText().trim(),
                    "", // passport field (your previous code passes "")
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
                        "Registration failed! Please check your inputs (and console logs).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JButton goLogin = smallLinkButton("Already have an account? Login");
        goLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        goLogin.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        panel.add(goLogin);

        return panel;
    }

    private JPanel createGuideForm() {
        JPanel panel = formSectionPanel();

        JTextField nameField = createField(panel, "Full Name");
        JTextField emailField = createField(panel, "Email");
        JTextField phoneField = createField(panel, "Phone Number");

        JLabel divLabel = ComfyGoGUI.label("Division");
        divLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(divLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        String[] divisions = {"Dhaka", "Chittagong", "Khulna", "Rajshahi", "Barisal", "Sylhet", "Rangpur", "Mymensingh"};
        JComboBox<String> divisionCombo = ComfyGoGUI.createStyledComboBox(divisions);
        divisionCombo.setMaximumSize(new Dimension(520, 44));
        divisionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(divisionCombo);

        JTextField districtField = createField(panel, "District");
        JTextField languagesField = createField(panel, "Languages (comma-separated)");
        JTextField specializationField = createField(panel, "Specialization");
        JTextField experienceField = createField(panel, "Years of Experience");
        JPasswordField passwordField = createPasswordField(panel, "Password");

        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton registerBtn = ComfyGoGUI.createStyledButton("Register as Tour Guide", ComfyGoGUI.LIME);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

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
                        "Registration failed! Please check inputs (and console logs).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JButton goLogin = smallLinkButton("Already have an account? Login");
        goLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        goLogin.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        panel.add(goLogin);

        return panel;
    }

    private JPanel createManagerForm() {
        JPanel panel = formSectionPanel();

        JTextField nameField = createField(panel, "Full Name");
        JTextField emailField = createField(panel, "Email");
        JTextField phoneField = createField(panel, "Phone Number");
        JTextField managerNidField = createField(panel, "Manager NID");
        JTextField hotelNameField = createField(panel, "Hotel Name");
        JTextField hotelNidField = createField(panel, "Hotel NID/License");
        JTextField registrationField = createField(panel, "Registration Number");
        JPasswordField passwordField = createPasswordField(panel, "Password");

        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton registerBtn = ComfyGoGUI.createStyledButton("Register as Hotel Manager", ComfyGoGUI.LIME);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

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
                        "Registration failed! Please check inputs (and console logs).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(registerBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JButton goLogin = smallLinkButton("Already have an account? Login");
        goLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        goLogin.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        panel.add(goLogin);

        return panel;
    }

    // -------------------- UI helpers --------------------

    private JPanel formSectionPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(4, 0, 0, 0));
        return p;
    }

    private JTextField createField(JPanel panel, String labelText) {
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel label = ComfyGoGUI.label(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JTextField field = ComfyGoGUI.createStyledTextField();
        field.setMaximumSize(new Dimension(520, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        return field;
    }

    private JPasswordField createPasswordField(JPanel panel, String labelText) {
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel label = ComfyGoGUI.label(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        JPasswordField field = ComfyGoGUI.createStyledPasswordField();
        field.setMaximumSize(new Dimension(520, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);

        JCheckBox show = new JCheckBox("Show password");
        show.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        show.setForeground(new Color(60, 80, 70));
        show.setBackground(ComfyGoGUI.SURFACE);
        show.setAlignmentX(Component.LEFT_ALIGNMENT);

        char defaultEcho = field.getEchoChar();
        show.addActionListener(e -> field.setEchoChar(show.isSelected() ? (char) 0 : defaultEcho));

        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(show);

        return field;
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