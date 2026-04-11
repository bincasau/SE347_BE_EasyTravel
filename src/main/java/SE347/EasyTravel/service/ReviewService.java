package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.*;
import SE347.EasyTravel.dto.ReviewResponseDTO;
import SE347.EasyTravel.entity.Review;
import SE347.EasyTravel.entity.Tour;
import SE347.EasyTravel.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReviewService {
    private ReviewRepo reviewRepo;
    private TourBookingRepo tourBookingRepo;
    private HotelBookingRepo hotelBookingRepo;
    private UserRepo userRepo;
    private TourRepo tourRepo;
    private HotelRepo hotelRepo;

    @Autowired
    public ReviewService(ReviewRepo reviewRepo, TourBookingRepo tourBookingRepo, HotelBookingRepo hotelBookingRepo, UserRepo userRepo, TourRepo tourRepo, HotelRepo hotelRepo) {
        this.reviewRepo = reviewRepo;
        this.tourBookingRepo = tourBookingRepo;
        this.hotelBookingRepo = hotelBookingRepo;
        this.userRepo = userRepo;
        this.tourRepo = tourRepo;
        this.hotelRepo = hotelRepo;
    }
    @Transactional
    public Review createReview(Review review, String username, Integer tourId, Integer hotelId) {
        User user = userRepo.findByUsername(username);

        if (tourId != null) {
            Tour tour = tourRepo.findById(tourId).orElseThrow();
            if (!tourBookingRepo.hasUserCompletedTour(username, tour.getTitle())) {
                throw new RuntimeException("Chưa tham gia tour này không được review!");
            }
            review.setTour(tour);
        } else if (hotelId != null) {
            if (!hotelBookingRepo.hasUserStayedAtHotel(username, hotelId)) {
                throw new RuntimeException("Chưa ở khách sạn này không được review!");
            }
            review.setHotel(hotelRepo.findById(hotelId).orElse(null));
        }
        review.setUser(user);
        return reviewRepo.save(review);
    }

    @Transactional
    public Review updateReview(int reviewId, Review details, String username) {
        Review existing = reviewRepo.findById(reviewId).orElseThrow();

        if (!existing.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Không được sửa review người khác");
        }
        existing.setComment(details.getComment());
        existing.setRating(details.getRating());
        return reviewRepo.save(existing);
    }
    @Transactional
    public void deleteReview(int reviewId, String username, String role) {
        Review review = reviewRepo.findById(reviewId).orElseThrow();

        if (review.getUser().getUsername().equals(username) || "ADMIN".equals(role)) {
            reviewRepo.delete(review);
        } else {
            throw new RuntimeException("Không có quyền xóa review này!");
        }
    }
    public boolean checkUserCanReviewTour(String username, String title) {
        return tourBookingRepo.hasUserCompletedTour(username, title);
    }
    public boolean checkUserCanReviewHotel(String username, int hotelId) {
        return hotelBookingRepo.hasUserStayedAtHotel(username, hotelId);
    }
    public Page<Review> getReviewsByTourTitle(String title, Pageable pageable) {
        return reviewRepo.findByTourTitle(title, pageable);
    }
    public Page<Review> getReviewsByHotelId(int hotelId, Pageable pageable) {
        return reviewRepo.findByHotel_HotelId(hotelId, pageable);
    }

    public List<ReviewResponseDTO> getReviewsByTour(int tourId) {
        return reviewRepo.findReviewsByTourId(tourId);
    }

    public List<ReviewResponseDTO> getReviewsByHotel(int hotelId) {
        return reviewRepo.findReviewsByHotelId(hotelId);
    }

    public List<ReviewResponseDTO> getReviewsByTourTitle(String title) {
        return reviewRepo.findReviewsByTourTitle(title);
    }
}
