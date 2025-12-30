import java.sql.*;
import java.util.UUID;

/**
 * Payment Entity - Represents a Payment Transaction
 * Handles payment processing, status tracking, and transaction records
 */
public class Payment {
    private String paymentId;
    private double amount;
    private String paymentMethod;
    private String paymentDate;
    private String paymentStatus;
    private String transactionId;
    private String description;
    
    // Constructor
    public Payment() {
        this.paymentId = UUID.randomUUID().toString().substring(0, 12);
    }
    
    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // Payment CRUD Operations
    public boolean createPayment(Connection conn) {
        String sql = "INSERT INTO payment (payment_id, amount, payment_method, payment_status, transactionId, description) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.paymentId);
            pstmt.setDouble(2, this.amount);
            pstmt.setString(3, this.paymentMethod);
            pstmt.setString(4, this.paymentStatus);
            pstmt.setString(5, this.transactionId);
            pstmt.setString(6, this.description);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error creating payment: " + e.getMessage());
            return false;
        }
    }
    
    public static Payment getPaymentById(String paymentId, Connection conn) {
        String sql = "SELECT * FROM payment WHERE payment_id = ?";
        Payment payment = null;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, paymentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment = new Payment();
                    payment.setPaymentId(rs.getString("payment_id"));
                    payment.setAmount(rs.getDouble("amount"));
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    payment.setPaymentDate(rs.getString("payment_date"));
                    payment.setPaymentStatus(rs.getString("payment_status"));
                    payment.setTransactionId(rs.getString("transactionId"));
                    payment.setDescription(rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching payment: " + e.getMessage());
        }
        
        return payment;
    }
    
    public boolean updatePaymentStatus(Connection conn, String newStatus) {
        String sql = "UPDATE payment SET payment_status = ? WHERE payment_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, this.paymentId);
            
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                this.paymentStatus = newStatus;
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean processPayment(Connection conn) {
        return updatePaymentStatus(conn, "COMPLETED");
    }
    
    public boolean refundPayment(Connection conn) {
        return updatePaymentStatus(conn, "REFUNDED");
    }
    
    public boolean updatePayment(Connection conn) {
        String sql = "UPDATE payment SET amount = ?, description = ? WHERE payment_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, this.amount);
            pstmt.setString(2, this.description);
            pstmt.setString(3, this.paymentId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating payment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deletePayment(Connection conn) {
        String sql = "DELETE FROM payment WHERE payment_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.paymentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting payment: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", paymentDate='" + paymentDate + '\'' +
                '}';
    }
}