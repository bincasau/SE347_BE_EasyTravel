package SE347.EasyTravel.controller;

import SE347.EasyTravel.dao.TourRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tours")
public class TourController {
    @Autowired
    private TourRepo tourRepo;

    @GetMapping("/departure-locations")
    public ResponseEntity<List<String>> getDepartureLocations(){
        List<String> list = this.tourRepo.findDistinctDepartureLocations();
        return ResponseEntity.ok(list);
    }
}
