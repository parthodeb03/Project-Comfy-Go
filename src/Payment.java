import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Payment {

    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private String paymentId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus = STATUS_CANCELLED;
    private String transactionId;
    private String description;

    public Payment() {}

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean createPayment(Connection conn) {
        if (conn == null) return false;
        if (amount <= 0) return false;

        try {
            if (isBlank(paymentId)) {
                paymentId = IdGenerator.uniqueNumericId(conn, "payment", "paymentid", 12, 60);
            }

            if (isBlank(paymentStatus)) paymentStatus = STATUS_CANCELLED;
            paymentStatus = paymentStatus.trim().toUpperCase();
            if (!isAllowedStatus(paymentStatus)) {
                System.out.println("Invalid payment status: " + paymentStatus);
                return false;
            }

            if (isBlank(paymentMethod)) paymentMethod = "CASH";
            paymentMethod = paymentMethod.trim();

            if (isBlank(transactionId)) {
                transactionId = "TXN" + System.currentTimeMillis() + "-" +
                        paymentId.substring(Math.max(0, paymentId.length() - 4));
            }

            String sql =
                    "INSERT INTO payment (paymentid, amount, paymentmethod, paymentstatus, transactionid, description) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, paymentId);
                ps.setDouble(2, amount);
                ps.setString(3, paymentMethod);
                ps.setString(4, paymentStatus);
                ps.setString(5, transactionId);
                ps.setString(6, description);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.out.println("Payment create failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePaymentStatus(Connection conn, String newStatus) {
        if (conn == null) return false;
        if (isBlank(paymentId)) return false;
        if (isBlank(newStatus)) return false;

        String status = newStatus.trim().toUpperCase();
        if (!isAllowedStatus(status)) {
            System.out.println("Invalid payment status: " + status);
            return false;
        }

        String sql = "UPDATE payment SET paymentstatus = ? WHERE paymentid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, paymentId.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) paymentStatus = status;
            return ok;
        } catch (SQLException e) {
            System.out.println("Payment status update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean markCompleted(Connection conn) {
        return updatePaymentStatus(conn, STATUS_COMPLETED);
    }

    public boolean cancel(Connection conn) {
        return updatePaymentStatus(conn, STATUS_CANCELLED);
    }

    public static String getPaymentStatusById(Connection conn, String paymentId) {
        if (conn == null) return null;
        if (isBlank(paymentId)) return null;

        String sql = "SELECT paymentstatus FROM payment WHERE paymentid = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("paymentstatus");
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch payment status: " + e.getMessage());
        }
        return null;
    }

    private static boolean isAllowedStatus(String s) {
        return STATUS_COMPLETED.equals(s) || STATUS_CANCELLED.equals(s);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}