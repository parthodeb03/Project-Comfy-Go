import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;

public class ComfyGoGUI extends JFrame {
    private Connection conn;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    // Services
    private AuthService authService;
    private HotelService hotelService;
    private ManagerService managerService;
    private GuideService guideService;
    private TouristSpotService spotService;
    private TransportService transportService;
    private RatingService ratingService;

    // Session
    private String currentUserId;
    private String currentUserName;
    private String currentUserRole;

    // ========== UPGRADED MODERN COLOR PALETTE ==========
    public static final Color PRIMARY = new Color(26, 115, 232);      // Modern Blue
    public static final Color PRIMARY_DARK = new Color(23, 92, 186);   // Darker Blue
    public static final Color SECONDARY = new Color(52, 168, 83);      // Modern Green
    public static final Color ACCENT = new Color(251, 140, 0);         // Vibrant Orange
    public static final Color SUCCESS = new Color(67, 160, 71);        // Success Green
    public static final Color WARNING = new Color(255, 179, 0);        // Warning Amber
    public static final Color DANGER = new Color(244, 67, 54);         // Danger Red
    public static final Color INFO = new Color(33, 150, 243);          // Info Blue
    
    // Backgrounds
    public static final Color BACKGROUND = new Color(248, 249, 250);   // Light Gray
    public static final Color SURFACE = new Color(255, 255, 255);      // Pure White
    public static final Color CARD_BG = new Color(255, 255, 255);      // Card White
    
    // Text
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);    // Dark Gray
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117); // Medium Gray
    public static final Color TEXT_HINT = new Color(158, 158, 158);    // Light Gray
    
    // Borders
    public static final Color BORDER_LIGHT = new Color(224, 224, 224);
    public static final Color BORDER_MEDIUM = new Color(189, 189, 189);
    
    // Legacy compatibility
    public static final Color LIME = SUCCESS;
    public static final Color FOREST = PRIMARY_DARK;
    public static final Color LEAF = SECONDARY;
    public static final Color MOSS = new Color(120, 163, 100);
    public static final Color BACKDROP = BACKGROUND;
    public static final Color INK = TEXT_PRIMARY;
    public static final Color MUTED = TEXT_SECONDARY;
    public static final Color DARK = TEXT_PRIMARY;
    public static final Color LIGHT = BACKGROUND;
    public static final Color WHITE = Color.WHITE;

    public ComfyGoGUI() {
        setTitle("ComfyGo - Tourism Management System");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        mainPanel.setBackground(BACKGROUND);
        add(mainPanel);
        
        initDatabaseAndServices();
        buildInitialUI();
    }

    private void initDatabaseAndServices() {
        try {
            conn = Db.getConnection();
            if (!Db.testConnection()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to connect to database!",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }

            authService = new AuthService(conn);
            hotelService = new HotelService(conn);
            managerService = new ManagerService(conn);
            guideService = new GuideService(conn);
            spotService = new TouristSpotService(conn);
            transportService = new TransportService(conn);
            ratingService = new RatingService(conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Database initialization failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }

    private void buildInitialUI() {
        showPanel("WELCOME");
    }

    public void showPanel(String panelName) {
        mainPanel.removeAll();
        switch (panelName) {
            case "WELCOME" -> mainPanel.add(wrapRoot(new WelcomePanel(this)), "WELCOME");
            case "LOGIN" -> mainPanel.add(wrapRoot(new LoginPanel(this)), "LOGIN");
            case "REGISTER" -> mainPanel.add(wrapRoot(new RegisterPanel(this)), "REGISTER");
            case "TOURIST_DASHBOARD" -> mainPanel.add(wrapRoot(new TouristDashboard(this)), "TOURIST_DASHBOARD");
            case "GUIDE_DASHBOARD" -> mainPanel.add(wrapRoot(new GuideDashboard(this)), "GUIDE_DASHBOARD");
            case "MANAGER_DASHBOARD" -> mainPanel.add(wrapRoot(new ManagerDashboard(this)), "MANAGER_DASHBOARD");
            default -> mainPanel.add(wrapRoot(new WelcomePanel(this)), "WELCOME");
        }

        cardLayout.show(mainPanel, panelName);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel wrapRoot(JComponent content) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));
        root.add(content, BorderLayout.CENTER);
        return root;
    }

    public void logout() {
        currentUserId = null;
        currentUserName = null;
        currentUserRole = null;
        showPanel("WELCOME");
        JOptionPane.showMessageDialog(
            this,
            "Logged out successfully!",
            "Logout",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========== UI HELPER METHODS (UPGRADED) ==========
    
    public static JLabel h1(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 32));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel h2(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 24));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel h3(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    public static JLabel subtitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(24, 24, 24, 24)
        ));
        return p;
    }

    public static JPanel modernCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(BORDER_LIGHT, 1, 12),
            new EmptyBorder(24, 24, 24, 24)
        ));
        return p;
    }

    public static JScrollPane scrollWrap(JComponent content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        sp.getViewport().setBackground(SURFACE);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        return btn;
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(SURFACE);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        return field;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(SURFACE);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        return field;
    }

    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(SURFACE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return combo;
    }

    // Services getters
    public AuthService getAuthService() { return authService; }
    public HotelService getHotelService() { return hotelService; }
    public ManagerService getManagerService() { return managerService; }
    public GuideService getGuideService() { return guideService; }
    public TouristSpotService getSpotService() { return spotService; }
    public TransportService getTransportService() { return transportService; }
    public RatingService getRatingService() { return ratingService; }
    public Connection getConnection() { return conn; }

    // Session getters/setters
    public String getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(String id) { this.currentUserId = id; }
    public String getCurrentUserName() { return currentUserName; }
    public void setCurrentUserName(String name) { this.currentUserName = name; }
    public String getCurrentUserRole() { return currentUserRole; }
    public void setCurrentUserRole(String role) { this.currentUserRole = role; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Enable anti-aliasing for better text rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ComfyGoGUI gui = new ComfyGoGUI();
            gui.setVisible(true);
        });
    }
}

// Custom rounded border class
class RoundedBorder extends javax.swing.border.AbstractBorder {
    private Color color;
    private int thickness;
    private int radius;

    public RoundedBorder(Color color, int thickness, int radius) {
        this.color = color;
        this.thickness = thickness;
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }
}
