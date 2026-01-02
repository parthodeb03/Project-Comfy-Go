import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Numeric ID generator (digits only).
 * Optionally checks DB uniqueness for a given table/column.
 *
 * NOTE: table/column are concatenated into SQL. Only pass trusted constants.
 */
public final class IdGenerator {

    private static final SecureRandom RAND = new SecureRandom();

    private IdGenerator() {}

    /** Returns a numeric string with exactly {@code digits} digits. */
    public static String randomNumericId(int digits) {
        if (digits < 2 || digits > 18) {
            throw new IllegalArgumentException("digits must be between 2 and 18");
        }

        long min = pow10(digits - 1);     // e.g. 1000 for 4 digits
        long maxExclusive = pow10(digits); // e.g. 10000 for 4 digits

        long value = nextLongBounded(min, maxExclusive);
        return String.valueOf(value);
    }

    /**
     * Generates a numeric ID and checks DB uniqueness against table(column).
     * Falls back to a random numeric ID if maxAttempts is exhausted.
     */
    public static String uniqueNumericId(Connection conn, String table, String column,
                                         int digits, int maxAttempts) throws SQLException {
        if (conn == null) throw new IllegalArgumentException("Connection cannot be null");
        if (table == null || table.trim().isEmpty()) throw new IllegalArgumentException("table cannot be empty");
        if (column == null || column.trim().isEmpty()) throw new IllegalArgumentException("column cannot be empty");
        if (maxAttempts < 1) maxAttempts = 1;

        String sql = "SELECT 1 FROM " + table + " WHERE " + column + " = ? LIMIT 1";

        for (int i = 0; i < maxAttempts; i++) {
            String id = randomNumericId(digits);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return id; // unique
                }
            }
        }

        // Fallback still returns valid digits
        return randomNumericId(digits);
    }

    private static long pow10(int exp) {
        long result = 1L;
        for (int i = 0; i < exp; i++) result *= 10L;
        return result;
    }

    /** Returns random long in [origin, bound). */
    private static long nextLongBounded(long origin, long bound) {
        if (origin >= bound) throw new IllegalArgumentException("origin must be < bound");

        long n = bound - origin;
        long m = n - 1;

        // Power-of-two optimization
        if ((n & m) == 0L) {
            long r = RAND.nextLong() & m;
            return origin + r;
        }

        long u, r;
        do {
            u = RAND.nextLong() >>> 1;
            r = u % n;
        } while (u + m - r < 0L);

        return origin + r;
    }
}