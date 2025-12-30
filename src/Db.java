import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Connection Manager (console prototype).
 *
 * Optional config file: config.properties
 * Keys:
 *   db.url=jdbc:mysql://localhost:3306/comfygo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
 *   db.user=root
 *   db.password=
 */
public class Db {

    private static Connection connection;

    private static final String CONFIG_FILE = "config.properties";

    // Fallback credentials
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/comfygo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) return connection;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found: " + e.getMessage(), e);
        }

        Properties props = new Properties();
        String url = DEFAULT_URL;
        String user = DEFAULT_USER;
        String password = DEFAULT_PASSWORD;

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);

            if (props.getProperty("db.url") != null) url = props.getProperty("db.url").trim();
            if (props.getProperty("db.user") != null) user = props.getProperty("db.user").trim();
            if (props.getProperty("db.password") != null) password = props.getProperty("db.password");
        } catch (Exception ignored) {
            System.out.println("Using default database configuration");
        }

        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    public static boolean testConnection() {
        try {
            Connection c = getConnection();
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.out.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}