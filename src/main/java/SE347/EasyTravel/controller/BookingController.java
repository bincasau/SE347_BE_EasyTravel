package SE347.EasyTravel.controller;

import SE347.EasyTravel.dto.BookingHotelRequest;
import SE347.EasyTravel.dto.BookingResponse;
import SE347.EasyTravel.dto.BookingTourRequest;
import SE347.EasyTravel.entity.HotelBooking;
import SE347.EasyTravel.entity.TourBooking;
import SE347.EasyTravel.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/tour")
    public ResponseEntity<?> tourBooking(@RequestBody BookingTourRequest req){
        TourBooking tourBooking = this.bookingService.createTourBooking(req);
        if (tourBooking == null) {
            return ResponseEntity.badRequest().body("Tour booking failed!");
        }
        return ResponseEntity.ok(new BookingResponse(tourBooking.getBookingId(), "TOUR"));
    }

    @PostMapping("/hotel")
    public ResponseEntity<?> hotelBooking(@RequestBody BookingHotelRequest req){
        HotelBooking hotelBooking  = this.bookingService.createHotelBooking(req);
        if (hotelBooking == null) {
            return ResponseEntity.badRequest().body("Hotel booking failed!");
        }
        return ResponseEntity.ok(new BookingResponse(hotelBooking.getBookingId(), "HOTEL"));
    }
    @GetMapping("/history/hotels")
    public ResponseEntity<Page<HotelBooking>> getHotelHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Date start,
            @RequestParam(required = false) Date end,
            Principal principal) {

        return ResponseEntity.ok(bookingService.getHotelHistory(
                principal.getName(), start, end, page, size));
    }

    @GetMapping("/history/tours")
    public ResponseEntity<Page<TourBooking>> getTourHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Date start,
            @RequestParam(required = false) Date end,
            Principal principal) {

        return ResponseEntity.ok(bookingService.getMyTourHistory(
                principal.getName(), start, end, page, size));
    }
}
