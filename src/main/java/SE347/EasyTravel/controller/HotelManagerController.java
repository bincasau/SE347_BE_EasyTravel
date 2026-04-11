package SE347.EasyTravel.controller;

import SE347.EasyTravel.entity.Hotel;
import SE347.EasyTravel.entity.Room;
import SE347.EasyTravel.service.HotelManagerService;
import SE347.EasyTravel.service.HotelService;
import SE347.EasyTravel.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/hotel_manager")
public class HotelManagerController {

    private static final Logger logger = LoggerFactory.getLogger(HotelManagerController.class);

    @Autowired
    private HotelManagerService managerService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/my-hotel")
    public ResponseEntity<?> getMyHotel(Principal principal) {
        return ResponseEntity.ok(managerService.getHotelByManager(principal.getName()));
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(
            Principal principal,
            @RequestParam int month,
            @RequestParam int year,
            Pageable pageable) {
        return ResponseEntity.ok(managerService.getMonthlyBookings(principal.getName(), month, year, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Principal principal, @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(managerService.getMonthlyStats(principal.getName(), month, year));
    }

    @PutMapping("/update-hotel")
    public ResponseEntity<?> updateHotelByManager(
            @RequestPart("hotel") Hotel hotel,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal
    ) {
        try {
            Hotel updated = hotelService.updateHotelByManagerUsername(
                    principal.getName(),
                    hotel,
                    file
            );
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/rooms")
    public ResponseEntity<?> saveOrUpdateRoom(
            @RequestPart("room") Room room,
            @RequestPart(value = "bedFile", required = false) MultipartFile bedFile,
            @RequestPart(value = "wcFile", required = false) MultipartFile wcFile,
            Principal principal
    ) {
        try {
            return ResponseEntity.ok(
                    roomService.saveOrUpdateRoomByManager(
                            principal.getName(),
                            room,
                            bedFile,
                            wcFile
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @PathVariable int roomId,
            Principal principal
    ) {
        roomService.deleteRoomByManager(principal.getName(), roomId);
        return ResponseEntity.ok("Xóa phòng thành công");
    }
}
