package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "payment")
public interface PaymentRepo extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTourBooking_BookingId(Integer bookingId);
    Optional<Payment> findByHotelBooking_BookingId(Integer bookingId);

    @Query(value = """
        SELECT COALESCE(SUM(total_price), 0)
        FROM payment p
        WHERE p.tour_booking_id IS NOT NULL
          AND p.status = 'Success'
          AND MONTH(p.payment_date) = :month
          AND YEAR(p.payment_date) = :year
    """, nativeQuery = true)
    Double getRevenueByMonth(@Param("month") int month,
                             @Param("year") int year);


}
