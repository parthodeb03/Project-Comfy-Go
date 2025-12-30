public class ManagerActivities {

    private Booking booking;
    private Payment payment;
    private Hotel hotel;

    public ManagerActivities(Booking booking, Payment payment, Hotel hotel) {
        this.booking = booking;
        this.payment = payment;
        this.hotel = hotel;
    }

    public void approvingUserBookings() {
        booking.approveBooking();
    }

    public int getHotelAvailability(int hotelId) {
        return hotel.hotel_availability;
    }

    public void updatingHotelAvailability(int newAvailability) {
        hotel.hotel_availability = newAvailability;
    }
}