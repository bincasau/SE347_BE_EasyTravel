package SE347.EasyTravel.controller;

import SE347.EasyTravel.dao.BlogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blogs")
public class BlogController {
    @Autowired
    private BlogRepo blogRepo;

    @GetMapping("/tags")
    public ResponseEntity<?> getBlogTag(){
        List<String> res = this.blogRepo.findAllTags();
        return ResponseEntity.ok(res);
    }
}
