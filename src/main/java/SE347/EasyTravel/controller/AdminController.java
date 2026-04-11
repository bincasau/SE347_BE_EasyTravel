package SE347.EasyTravel.controller;

import SE347.EasyTravel.dto.AdminDashboardDTO;
import SE347.EasyTravel.dto.MonthlyTourStatsDTO;
import SE347.EasyTravel.entity.*;
import SE347.EasyTravel.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private HotelService hotelService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private TourService tourService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private DashBoardService dashboardService;

    @PostMapping("/add-hotel")
    public ResponseEntity<?> addHotel(
            @RequestPart("hotel") Hotel hotel,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam("managerUsername") String managerUsername) {
        try {
            Hotel created = hotelService.createHotel(hotel, file, managerUsername);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating hotel", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/update-hotel/{id}")
    public ResponseEntity<?> updateHotel(
            @PathVariable int id,
            @RequestPart("hotel") Hotel hotel,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(hotelService.updateHotel(id, hotel, file));
        } catch (Exception e) {
            logger.error("Error updating hotel with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/rooms/save")
    public ResponseEntity<?> saveRoom(
            @RequestParam("hotelId") int hotelId,
            @RequestPart("room") Room room,
            @RequestPart(value = "bedFile", required = false) MultipartFile bedFile,
            @RequestPart(value = "wcFile", required = false) MultipartFile wcFile) {
        try {
            Room savedRoom = roomService.saveOrUpdateRoom(hotelId, room, bedFile, wcFile);
            return ResponseEntity.ok(savedRoom);
        } catch (Exception e) {
            logger.error("Error saving room for hotel: {}", hotelId, e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/delete-hotel/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable int id) {
        try {
            hotelService.deleteHotel(id);
            return ResponseEntity.ok("Hotel deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting hotel with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/rooms/delete/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable int id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok("Room deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting room with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/blog/save")
    public ResponseEntity<?> saveBlog(
            @RequestPart("blog") Blog blog,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) {
        try {
            Blog savedBlog = blogService.saveOrUpdateBlog(blog, file, principal.getName());
            return ResponseEntity.ok(savedBlog);
        } catch (Exception e) {
            logger.error("Error saving blog for user: {}", principal.getName(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/delete-blog/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable int id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.ok("Blog deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting blog with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/tour/save")
    public ResponseEntity<?> saveTour(
            @RequestPart("tour") Tour tour,
            @RequestParam(value = "guideIds", required = false) List<Integer> guideIds, // Nhận list ID ví dụ: 1,2,5
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(tourService.saveOrUpdateTour(tour, guideIds, file));
        } catch (Exception e) {
            logger.error("Error saving tour", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/tour/{tourId}")
    public ResponseEntity<?> deleteTour(@PathVariable int tourId) {
        try {
            tourService.deleteTour(tourId);
            return ResponseEntity.ok("Tour deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting tour with id: {}", tourId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/tour/{tourId}/itinerary")
    public ResponseEntity<?> addItinerary(
            @PathVariable int tourId,
            @RequestBody Itinerary itinerary) {
        try {
            return ResponseEntity.ok(tourService.addItinerary(tourId, itinerary));
        } catch (Exception e) {
            logger.error("Error adding itinerary to tour: {}", tourId, e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/tour/itinerary/{itineraryId}")
    public ResponseEntity<?> updateItinerary(
            @PathVariable int itineraryId,
            @RequestBody Itinerary itinerary) {
        try {
            return ResponseEntity.ok(
                    tourService.updateItinerary(itineraryId, itinerary)
            );
        } catch (Exception e) {
            logger.error("Error updating itinerary with id: {}", itineraryId, e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/tour/itinerary/{itineraryId}")
    public ResponseEntity<?> deleteItinerary(@PathVariable int itineraryId) {
        try {
            tourService.deleteItinerary(itineraryId);
            return ResponseEntity.ok("Itinerary deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting itinerary with id: {}", itineraryId, e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/tour/{tourId}/participants")
    public ResponseEntity<List<TourBooking>> getParticipants(@PathVariable int tourId) {
        List<TourBooking> participants = bookingService.getParticipantsByTour(tourId);
        return ResponseEntity.ok(participants);
    }
    @GetMapping("/tour/monthly")
    public ResponseEntity<MonthlyTourStatsDTO> getMonthlyStats(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(bookingService.getMonthlyReport(month, year));
    }

    @PostMapping("/tour/copy/{id}")
    public ResponseEntity<?> duplicateTour(@PathVariable int id) {
        try {
            Tour copiedTour = tourService.copyTour(id);
            return ResponseEntity.ok(Map.of("id", copiedTour.getTourId()));
        } catch (Exception e) {
            logger.error("Error copying tour with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/hotel/{hotelId}/manager")
    public ResponseEntity<User> getManager(@PathVariable int hotelId) {
        User manager = hotelService.getManagerByHotelId(hotelId);
        if (manager == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(manager);
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getAdminStats());
    }
}
