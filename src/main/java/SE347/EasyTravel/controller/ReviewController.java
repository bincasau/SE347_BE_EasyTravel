package SE347.EasyTravel.controller;

import SE347.EasyTravel.dto.ReviewResponseDTO;
import SE347.EasyTravel.entity.Review;
import SE347.EasyTravel.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/custom-reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Review review,
                                    @RequestParam(required = false) Integer tourId,
                                    @RequestParam(required = false) Integer hotelId,
                                    Principal principal) {
        try {
            return ResponseEntity.ok(reviewService.createReview(review, principal.getName(), tourId, hotelId));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Review review, Principal principal) {
        try {
            return ResponseEntity.ok(reviewService.updateReview(id, review, principal.getName()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, Principal principal, Authentication auth) {
        try {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            reviewService.deleteReview(id, principal.getName(), role);
            return ResponseEntity.ok("Đã xóa review!");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
//    @GetMapping("/list")
//    public ResponseEntity<Page<Review>> getReviewList(
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) Integer hotelId,
//            Pageable pageable) {
//        if (title != null) {
//            return ResponseEntity.ok(reviewService.getReviewsByTourTitle(title, pageable));
//        }
//        if (hotelId != null) {
//            return ResponseEntity.ok(reviewService.getReviewsByHotelId(hotelId, pageable));
//        }
//        return ResponseEntity.badRequest().build();
//    }
    @GetMapping("/check-can-review-tour")
    public ResponseEntity<Boolean> checkCanReviewTour(@RequestParam String title, Principal principal) {
        if (principal == null) return ResponseEntity.ok(false);
        try {
            boolean canReview = reviewService.checkUserCanReviewTour(principal.getName(), title);
            return ResponseEntity.ok(canReview);
        } catch (Exception e) {
            logger.debug("Error checking if user can review tour: {}", title, e);
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/check-can-review-hotel")
    public ResponseEntity<Boolean> checkCanReviewHotel(@RequestParam int hotelId, Principal principal) {
        if (principal == null) return ResponseEntity.ok(false);
        try {
            boolean canReview = reviewService.checkUserCanReviewHotel(principal.getName(), hotelId);
            return ResponseEntity.ok(canReview);
        } catch (Exception e) {
            logger.debug("Error checking if user can review hotel: {}", hotelId, e);
            return ResponseEntity.ok(false);
        }
    }
    @GetMapping("/{tourId}/tour")
    public List<ReviewResponseDTO> getTourReviews(@PathVariable int tourId) {
        return reviewService.getReviewsByTour(tourId);
    }

    @GetMapping("/{hotelId}/hotel")
    public List<ReviewResponseDTO> getHotelReviews(@PathVariable int hotelId) {
        return reviewService.getReviewsByHotel(hotelId);
    }

    @GetMapping("/tour")
    public List<ReviewResponseDTO> getTourReviewsByTitle(@RequestParam String title) {
        return reviewService.getReviewsByTourTitle(title);
    }
}
