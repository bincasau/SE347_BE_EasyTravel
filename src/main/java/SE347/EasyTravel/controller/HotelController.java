package SE347.EasyTravel.controller;

import SE347.EasyTravel.dao.HotelRepo;
import SE347.EasyTravel.dto.HotelCard;
import SE347.EasyTravel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {
    @Autowired
    private HotelRepo hotelRepo;

    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getProvinces(){
        List<String> provinces = this.hotelRepo.findAllProvinces();
        return ResponseEntity.ok(provinces);
    }
}
