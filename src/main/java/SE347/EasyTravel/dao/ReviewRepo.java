package SE347.EasyTravel.dao;

import SE347.EasyTravel.dto.ReviewResponseDTO;
import SE347.EasyTravel.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "review")
public interface ReviewRepo extends JpaRepository<Review, Integer> {
    @Query("SELECT r FROM Review r WHERE r.tour.title = :title")
    Page<Review> findByTourTitle(@Param("title") String title, Pageable pageable);
    Page<Review> findByHotel_HotelId(int hotelId, Pageable pageable);

    @Query("SELECT new SE347.EasyTravel.dto.ReviewResponseDTO(r.reviewId, r.comment, r.rating, r.createdAt, u.name, u.avatar) " +
            "FROM Review r JOIN r.user u " +
            "WHERE r.tour.tourId = :tourId ORDER BY r.createdAt DESC")
    List<ReviewResponseDTO> findReviewsByTourId(@Param("tourId") int tourId);

    @Query("SELECT new SE347.EasyTravel.dto.ReviewResponseDTO(r.reviewId, r.comment, r.rating, r.createdAt, u.name, u.avatar) " +
            "FROM Review r JOIN r.user u " +
            "WHERE r.hotel.hotelId = :hotelId ORDER BY r.createdAt DESC")
    List<ReviewResponseDTO> findReviewsByHotelId(@Param("hotelId") int hotelId);

    @Query("SELECT new SE347.EasyTravel.dto.ReviewResponseDTO(r.reviewId, r.comment, r.rating, r.createdAt, u.name, u.avatar) " +
            "FROM Review r JOIN r.user u JOIN r.tour t " +
            "WHERE t.title = :title ORDER BY r.createdAt DESC")
    List<ReviewResponseDTO> findReviewsByTourTitle(@Param("title") String title);
}
