package SE347.EasyTravel.dao;

import SE347.EasyTravel.dto.MonthlyTourStatsDTO;
import SE347.EasyTravel.entity.TourBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "tour-bookings")
public interface TourBookingRepo extends JpaRepository<TourBooking, Integer> {

    List<TourBooking> findAllByStatusAndBookingDateBefore(String status, LocalDateTime time);

    @Query("SELECT COUNT(tb) > 0 FROM TourBooking tb " +
            "WHERE tb.user.username = :username " +
            "AND tb.tour.title = :title " +
            "AND tb.status = 'Success'")
    boolean hasUserCompletedTour(@Param("username") String username, @Param("title") String title);

    // CHỈNH SỬA: Sử dụng FUNCTION('DATE', ...) để so sánh LocalDateTime với java.sql.Date
    @Query("SELECT tb FROM TourBooking tb " +
            "JOIN FETCH tb.tour t " +
            "LEFT JOIN FETCH tb.user u " +
            "WHERE u.username = :username " +
            "AND (:start IS NULL OR FUNCTION('DATE', tb.bookingDate) >= :start) " +
            "AND (:end IS NULL OR FUNCTION('DATE', tb.bookingDate) <= :end) " +
            "ORDER BY tb.bookingDate DESC")
    Page<TourBooking> findMyTourHistoryFull(
            @Param("username") String username,
            @Param("start") Date start,
            @Param("end") Date end,
            Pageable pageable);

    @Query("SELECT tb FROM TourBooking tb " +
            "JOIN FETCH tb.user u " +
            "WHERE tb.tour.tourId = :tourId " +
            "ORDER BY tb.bookingDate DESC")
    List<TourBooking> findByTourIdWithUser(@Param("tourId") int tourId);

    @Query("SELECT new SE347.EasyTravel.dto.MonthlyTourStatsDTO(" +
            ":month, :year, " +
            "COUNT(DISTINCT t.tourId), " +
            "COUNT(DISTINCT CASE WHEN t.status = 'Activated' THEN t.tourId ELSE NULL END), " +
            "COUNT(DISTINCT CASE WHEN t.status = 'Passed' THEN t.tourId ELSE NULL END), " +
            "COUNT(DISTINCT CASE WHEN t.status = 'Cancelled' THEN t.tourId ELSE NULL END), " +
            "COALESCE(SUM(CASE WHEN tb.status = 'Success' THEN (tb.adults + tb.children) ELSE 0L END), 0L), " +
            "COALESCE(SUM(DISTINCT CASE WHEN t.status = 'Activated' THEN t.availableSeats ELSE 0 END), 0L), " +
            "COALESCE(SUM(CASE WHEN tb.status = 'Success' THEN tb.totalPrice ELSE 0.0 END), 0.0)) " +
            "FROM Tour t LEFT JOIN t.tourBookings tb " +
            "WHERE FUNCTION('MONTH', t.startDate) = :month " +
            "AND FUNCTION('YEAR', t.startDate) = :year")
    MonthlyTourStatsDTO getMonthlyStats(@Param("month") int month, @Param("year") int year);

    long countByBookingDateAfter(java.time.LocalDateTime startDate);
}