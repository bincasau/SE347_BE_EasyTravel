package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.HotelBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "hotel-bookings")
public interface HotelBookingRepo extends JpaRepository<HotelBooking, Integer> {

    List<HotelBooking> findAllByStatusAndCreatedAtBefore(String status, LocalDateTime time);

    List<HotelBooking> findByHotelHotelIdAndRoomRoomId(@Param("hotelId") int hotelId, @Param("roomId") int roomId);

    @Query("""
        SELECT b
        FROM HotelBooking b
        JOIN FETCH b.user
        JOIN FETCH b.room
        WHERE b.hotel.manager.username = :username
          AND FUNCTION('MONTH', b.checkInDate) = :month
          AND FUNCTION('YEAR', b.checkInDate) = :year
    """)
    Page<HotelBooking> findMonthlyBookings(
            @Param("username") String username,
            @Param("month") int month,
            @Param("year") int year,
            Pageable pageable
    );

    @Query("""
        SELECT
            b.room.roomType, 
            COUNT(b), 
            COALESCE(SUM(b.totalPrice), 0)
        FROM HotelBooking b
        WHERE b.hotel.manager.username = :username
          AND FUNCTION('MONTH', b.checkInDate) = :month
          AND FUNCTION('YEAR', b.checkInDate) = :year
          AND b.status = 'Success'
        GROUP BY b.room.roomType
    """)
    List<Object[]> getStatsByRoomType(
            @Param("username") String username,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT COUNT(hb) > 0 FROM HotelBooking hb " +
            "WHERE hb.user.username = :username " +
            "AND hb.hotel.hotelId = :hotelId " +
            "AND hb.status = 'Success'")
    boolean hasUserStayedAtHotel(@Param("username") String username, @Param("hotelId") int hotelId);

    @Query("SELECT h FROM HotelBooking h " +
            "JOIN FETCH h.hotel " +
            "JOIN FETCH h.room " +
            "LEFT JOIN FETCH h.payment " +
            "WHERE h.user.username = :username " +
            "AND (h.checkInDate BETWEEN :start AND :end) " +
            "ORDER BY h.checkInDate DESC")
    Page<HotelBooking> findMyHistoryFullInfo(
            @Param("username") String username,
            @Param("start") Date start,
            @Param("end") Date end,
            Pageable pageable);
}