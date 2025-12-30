import java.sql.Connection;

public class ManagerActivities {

    private final Connection conn;

    public ManagerActivities(Connection conn) {
        this.conn = conn;
    }

    public boolean confirmBooking(Booking booking) {
        if (booking == null) return false;
        return booking.updateBookingStatus(conn, Booking.STATUSCONFIRMED);
    }

    public boolean cancelBooking(Booking booking) {
        if (booking == null) return false;
        return booking.updateBookingStatus(conn, Booking.STATUSCANCELLED);
    }

    public boolean updatePaymentStatus(Payment payment, String newStatus) {
        if (payment == null) return false;
        return payment.updatePaymentStatus(conn, newStatus);
    }

    public boolean refundPayment(Payment payment) {
        if (payment == null) return false;
        return payment.updatePaymentStatus(conn, Payment.STATUSREFUNDED);
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