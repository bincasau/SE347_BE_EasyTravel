package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.BlogRepo;
import SE347.EasyTravel.dao.HotelRepo;
import SE347.EasyTravel.dao.ImageRepo;
import SE347.EasyTravel.dao.TourRepo;
import SE347.EasyTravel.entity.Image;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageService {
    private ImageRepo imageRepo;
    private HotelRepo hotelRepo;
    private TourRepo tourRepo;
    private BlogRepo blogRepo;
    private S3Service s3Service;

    @Autowired
    public ImageService(ImageRepo imageRepo, HotelRepo hotelRepo, TourRepo tourRepo, BlogRepo blogRepo, S3Service s3Service) {
        this.imageRepo = imageRepo;
        this.hotelRepo = hotelRepo;
        this.tourRepo = tourRepo;
        this.blogRepo = blogRepo;
        this.s3Service = s3Service;
    }
    @Transactional
    public Image addImage(MultipartFile file, String type, int ownerId) throws IOException {
        Image image = new Image();
        String folder = type;

        String customName = type + "_extra_" + ownerId + "_" + System.currentTimeMillis();
        String fileName = s3Service.uploadImage(file, "image", customName);

        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        image.setUrl(customName + ext);

        if (type.equals("hotel")) {
            image.setHotel(hotelRepo.findById(ownerId).orElseThrow());
        } else if (type.equals("tour")) {
            image.setTour(tourRepo.findById(ownerId).orElseThrow());
        } else if (type.equals("blog")) {
            image.setBlog(blogRepo.findById(ownerId).orElseThrow());
        }

        return imageRepo.save(image);
    }

    @Transactional
    public Image addURLImage(String type, String name, int ownerId) throws IOException {
        Image image = new Image();

        image.setUrl(name);

        if (type.equals("hotel")) {
            image.setHotel(hotelRepo.findById(ownerId).orElseThrow());
        } else if (type.equals("tour")) {
            image.setTour(tourRepo.findById(ownerId).orElseThrow());
        } else if (type.equals("blog")) {
            image.setBlog(blogRepo.findById(ownerId).orElseThrow());
        }

        return imageRepo.save(image);
    }


    @Transactional
    public void deleteImage(int imageId) {
        Image image = imageRepo.findById(imageId).orElseThrow();

        String folder = "image";

        s3Service.deleteImage(folder, image.getUrl());
        imageRepo.delete(image);
    }
}
