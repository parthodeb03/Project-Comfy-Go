import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Payment {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_REFUNDED = "REFUNDED";
    public static final String STATUSREFUNDED = null;

    private String paymentId;
    private double amount;
    private String paymentMethod;
    private String paymentStatus = STATUS_PENDING;
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

        try {
            if (paymentId == null || paymentId.trim().isEmpty()) {
                paymentId = IdGenerator.uniqueNumericId(conn, "payment", "paymentid", 12, 60);
            }

            if (paymentStatus == null || paymentStatus.trim().isEmpty()) paymentStatus = STATUS_PENDING;

            // payment.transactionid is UNIQUE in DB (per your schema intent)
            if (transactionId == null || transactionId.trim().isEmpty()) {
                transactionId = "TXN" + System.currentTimeMillis() + "-" + paymentId.substring(Math.max(0, paymentId.length() - 4));
            }

            String sql = "INSERT INTO payment (paymentid, amount, paymentmethod, paymentstatus, transactionid, description) " +
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
        if (paymentId == null || paymentId.trim().isEmpty()) return false;
        if (newStatus == null || newStatus.trim().isEmpty()) return false;

        String sql = "UPDATE payment SET paymentstatus = ? WHERE paymentid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.trim());
            ps.setString(2, paymentId);

            boolean ok = ps.executeUpdate() > 0;
            if (ok) paymentStatus = newStatus.trim();
            return ok;

        } catch (SQLException e) {
            System.out.println("Payment status update failed: " + e.getMessage());
            return false;
        }
    }

    /** Sets COMPLETED if paidAmount >= totalAmount else FAILED. */
    public boolean markCompletedOrFailed(Connection conn, double totalAmount, double paidAmount) {
        if (paidAmount >= totalAmount) return updatePaymentStatus(conn, STATUS_COMPLETED);
        return updatePaymentStatus(conn, STATUS_FAILED);
    }

    public static String getPaymentStatusById(Connection conn, String paymentId) {
        if (conn == null) return null;
        if (paymentId == null || paymentId.trim().isEmpty()) return null;

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

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}