package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.List;

@RepositoryRestResource(path = "tours")
public interface TourRepo extends JpaRepository<Tour, Integer> {
    Page<Tour> findByTitleContainingIgnoreCase(@RequestParam("keyword") String keyword, Pageable pageable);
    Page<Tour> findByStartDateGreaterThanEqual(@RequestParam("startDate") Date startDate, Pageable pageable);
    Page<Tour> findByDurationDays(@RequestParam("durationDay") int durationDays, Pageable pageable);
    Page<Tour> findByDepartureLocation(@RequestParam("departureLocation") String departureLocation, Pageable pageable);
    @Query("""
       select t
       from Tour t
       where (:keyword is null or :keyword = ''
                or lower(t.title) like lower(concat('%', :keyword, '%'))
             )
         and (:startDate is null or t.startDate >= :startDate)
         and (:durationDay is null or t.durationDays = :durationDay)
         and (:departureLocation is null or :departureLocation = ''
                or lower(t.departureLocation) like lower(concat('%', :departureLocation, '%'))
             )
         and (:status is null or :status = '' or t.status = :status)
       """)
    Page<Tour> filterTours(
            @Param("keyword") String keyword,
            @Param("startDate") Date startDate,
            @Param("durationDay") Integer durationDay,
            @Param("departureLocation") String departureLocation,
            @Param("status") String status,
            Pageable pageable
    );
    @Query("""
       SELECT DISTINCT t.departureLocation
       FROM Tour t
       WHERE t.departureLocation IS NOT NULL 
         AND t.departureLocation <> ''
       """)
    @RestResource(exported = false)
    List<String> findDistinctDepartureLocations();

    Page<Tour> findByStatusAndTourGuides_Username(String status, String username, Pageable pageable);

    Page<Tour> findByTourGuides_UsernameAndStartDateAfter(String username, Date date, Pageable pageable);

    @Query("""
        SELECT t
        FROM Tour t
        JOIN t.tourGuides g
        WHERE g.username = :username
          AND FUNCTION('MONTH', t.startDate) = :month
          AND FUNCTION('YEAR', t.startDate) = :year
    """)
    Page<Tour> findToursByMonthAndYearForGuide(
            @Param("username") String username,
            @Param("month") int month,
            @Param("year") int year,
            Pageable pageable
    );
    Page<Tour> findByStatus(@RequestParam("status") String status, Pageable pageable);

    @Query("SELECT t FROM Tour t WHERE FUNCTION('DATEDIFF', t.startDate, CURRENT_DATE) = 3 AND t.status = 'Activated'")
    List<Tour> findToursStartingInThreeDays();
    @Query("SELECT t FROM Tour t WHERE FUNCTION('DATEDIFF', t.startDate, CURRENT_DATE) = 4 AND t.status = 'Activated'")
    List<Tour> findToursStartingInFourDays();

    long countByStatus(String status);

    List<Tour> findTop5ByStatusAndStartDateAfterOrderByStartDateAsc(String status, java.sql.Date startDate);

}
