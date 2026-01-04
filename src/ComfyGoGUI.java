import java.awt.*;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

    // ---------- Nature / Lime theme ----------
    public static final Color LIME = new Color(50, 205, 50);
    public static final Color FOREST = new Color(19, 87, 45);
    public static final Color LEAF = new Color(46, 204, 113);
    public static final Color MOSS = new Color(120, 163, 100);

    public static final Color SURFACE = new Color(245, 250, 246);
    public static final Color BACKDROP = new Color(233, 244, 236);
    public static final Color INK = new Color(30, 40, 35);
    public static final Color MUTED = new Color(90, 110, 100);

    // Add these new modern colors after your existing color definitions
public static final Color VIBRANT_BLUE = new Color(52, 152, 219);
public static final Color VIBRANT_PURPLE = new Color(155, 89, 182);
public static final Color VIBRANT_ORANGE = new Color(230, 126, 34);
public static final Color VIBRANT_TEAL = new Color(26, 188, 156);
public static final Color SOFT_RED = new Color(231, 76, 60);
public static final Color GOLD = new Color(241, 196, 15);

// Enhanced gradients
public static final Color GRADIENT_START = new Color(46, 204, 113);
public static final Color GRADIENT_END = new Color(52, 152, 219);


    // Keep old names (other files reference these)
    public static final Color PRIMARY = FOREST;
    public static final Color SECONDARY = new Color(45, 111, 87);
    public static final Color SUCCESS = LEAF;
    public static final Color WARNING = new Color(241, 196, 15);
    public static final Color DANGER = new Color(231, 76, 60);
    public static final Color DARK = INK;
    public static final Color LIGHT = BACKDROP;
    public static final Color WHITE = Color.WHITE;

    public static JButton createModernButton(String text, Color bgColor, Color hoverColor) {
    JButton btn = new JButton(text);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setOpaque(true);
    
    // Add hover effect
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseEntered(java.awt.event.MouseEvent evt) {
            btn.setBackground(hoverColor);
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btn.setBackground(bgColor);
        }
    });
    
    return btn;
}

public static JPanel createModernCard() {
    JPanel p = new JPanel();
    p.setBackground(SURFACE);
    p.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 220, 210), 1),
        new EmptyBorder(20, 20, 20, 20)
    ));
    // Add shadow effect
    p.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 3, 3, new Color(0, 0, 0, 20)),
        p.getBorder()
    ));
    return p;
}

    public ComfyGoGUI() {
        setTitle("ComfyGo - Tourism Management System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel.setBackground(BACKDROP);
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

    // Consistent app background around every screen
    private JPanel wrapRoot(JComponent content) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKDROP);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
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

    // -------- Services getters --------
    public AuthService getAuthService() { return authService; }
    public HotelService getHotelService() { return hotelService; }
    public ManagerService getManagerService() { return managerService; }
    public GuideService getGuideService() { return guideService; }
    public TouristSpotService getSpotService() { return spotService; }
    public TransportService getTransportService() { return transportService; }
    public RatingService getRatingService() { return ratingService; }
    public Connection getConnection() { return conn; }

    // -------- Session getters/setters --------
    public String getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(String id) { this.currentUserId = id; }

    public String getCurrentUserName() { return currentUserName; }
    public void setCurrentUserName(String name) { this.currentUserName = name; }

    public String getCurrentUserRole() { return currentUserRole; }
    public void setCurrentUserRole(String role) { this.currentUserRole = role; }

    // -------- UI Helpers --------
    public static JLabel h1(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 30));
        l.setForeground(FOREST);
        return l;
    }

    public static JLabel h2(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setForeground(FOREST);
        return l;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(INK);
        return l;
    }

    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 227, 217), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return p;
    }

    public static JScrollPane scrollWrap(JComponent content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BACKDROP);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(252, 254, 253));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 210), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }

    public static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(252, 254, 253));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 210), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }

    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(252, 254, 253));
        return combo;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ComfyGoGUI gui = new ComfyGoGUI();
            gui.setVisible(true);
        });
    }
}