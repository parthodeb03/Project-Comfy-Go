import java.sql.Connection;

public class ManagerActivities {

    private final Connection conn;

    public ManagerActivities(Connection conn) {
        this.conn = conn;
    }

    public boolean confirmBooking(Booking booking) {
        if (booking == null) return false;
        return booking.updateBookingStatus(conn, Booking.STATUS_CONFIRMED);
    }

    public boolean cancelBooking(Booking booking) {
        if (booking == null) return false;
        return booking.updateBookingStatus(conn, Booking.STATUS_CANCELLED);
    }

    public boolean updatePaymentStatus(Payment payment, String newStatus) {
        if (payment == null) return false;
        return payment.updatePaymentStatus(conn, newStatus);
    }

    // With your "2 statuses only" rule: cancellation is the closest to "refund"
    public boolean refundPayment(Payment payment) {
        if (payment == null) return false;
        return payment.updatePaymentStatus(conn, Payment.STATUS_CANCELLED);
    }

    public boolean updateHotelAvailability(Hotel hotel, int newAvailability) {
        if (hotel == null) return false;
        hotel.setRoomAvailability(newAvailability);
        return hotel.updateRoomAvailability(conn, newAvailability);
    }

    public boolean updateHotelPrice(Hotel hotel, double newPrice) {
        if (hotel == null) return false;
        hotel.setPricePerNight(newPrice);
        return hotel.updatePrice(conn, newPrice);
    }
}