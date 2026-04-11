package SE347.EasyTravel.controller;

import SE347.EasyTravel.entity.TourBooking;
import SE347.EasyTravel.exception.UnauthorizedException;
import SE347.EasyTravel.service.BookingService;
import SE347.EasyTravel.service.TourGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/tour_guide")
public class TourGuideController {

    @Autowired
    private TourGuideService tourGuideService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/history")
    public ResponseEntity<?> getMyHistory(Principal principal, Pageable pageable) {
        if (principal == null) throw new UnauthorizedException("Unauthorized");
        return ResponseEntity.ok(tourGuideService.getHistory(principal.getName(), pageable));
    }

    @GetMapping("/future")
    public ResponseEntity<?> getMyFuture(Principal principal, Pageable pageable) {
         if (principal == null) throw new UnauthorizedException("Unauthorized");
        return ResponseEntity.ok(tourGuideService.getFuture(principal.getName(), pageable));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getMyUpcoming(Principal principal, Pageable pageable) {
        if (principal == null) throw new UnauthorizedException("Unauthorized");
        return ResponseEntity.ok(tourGuideService.getUpcoming(principal.getName(), pageable));
    }

    @GetMapping("/schedule")
    public ResponseEntity<?> getMySchedule(
            Principal principal,
            @RequestParam int month,
            @RequestParam int year,
            Pageable pageable) {
         if (principal == null) throw new UnauthorizedException("Unauthorized");
        return ResponseEntity.ok(tourGuideService.getScheduleByMonth(principal.getName(), month, year, pageable));
    }

    @GetMapping("/tour/{tourId}/participants")
    public ResponseEntity<List<TourBooking>> getParticipants(@PathVariable int tourId) {
        List<TourBooking> participants = bookingService.getParticipantsByTour(tourId);
        return ResponseEntity.ok(participants);
    }

}
