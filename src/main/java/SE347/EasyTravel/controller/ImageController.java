package SE347.EasyTravel.controller;

import SE347.EasyTravel.service.ImageService;
import SE347.EasyTravel.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/image")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam("folder") String folder,
                                         @RequestParam("name") String name) {
        try {
            String url = s3Service.uploadImage(file, folder, name);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            logger.error("Error uploading image to folder: {}", folder, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(
            @RequestParam("folder") String folder,
            @RequestParam("name") String fileName) {
        try {
            s3Service.deleteImage(folder, fileName);
            return ResponseEntity.ok("File deleted successfully: " + fileName);
        } catch (Exception e) {
            logger.error("Error deleting image from folder: {}", folder, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/auth/upload")
    public ResponseEntity<?> uploadExtra(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("id") int id) {
        try {
            return ResponseEntity.ok(imageService.addImage(file, type, id));
        } catch (Exception e) {
            logger.error("Error uploading image of type: {} for id: {}", type, id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/auth/uploadURL")
    public ResponseEntity<?> uploadExtra(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("id") int id) {
        try {
            return ResponseEntity.ok(imageService.addURLImage(type, name, id));
        } catch (Exception e) {
            logger.error("Error uploading URL image of type: {} for id: {}", type, id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/auth/{id}")
    public ResponseEntity<?> deleteExtra(@PathVariable int id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting image with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
