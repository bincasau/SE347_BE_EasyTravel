package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.HotelRepo;
import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.dto.HotelCard;
import SE347.EasyTravel.entity.Hotel;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class HotelService {

    private HotelRepo hotelRepo;
    private S3Service s3Service;
    private UserRepo userRepo;

    @Autowired
    public HotelService(HotelRepo hotelRepo, S3Service s3Service, UserRepo userRepo) {
        this.hotelRepo = hotelRepo;
        this.s3Service = s3Service;
        this.userRepo = userRepo;
    }

    @Transactional
    public Hotel createHotel(Hotel hotel, MultipartFile imageFile, String username) throws IOException {
        User manager = userRepo.findByUsername(username);
        hotel.setManager(manager);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = uploadAndGetFileName(imageFile, 0);
            hotel.setMainImage(fileName);
        }

        return hotelRepo.save(hotel);
    }

    @Transactional
    public Hotel updateHotel(int hotelId, Hotel hotelDetails, MultipartFile imageFile) throws IOException {
        Hotel existingHotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn"));

        existingHotel.setName(hotelDetails.getName());
        existingHotel.setAddress(hotelDetails.getAddress());
        existingHotel.setPhoneNumber(hotelDetails.getPhoneNumber());
        existingHotel.setEmail(hotelDetails.getEmail());
        existingHotel.setDescription(hotelDetails.getDescription());
        existingHotel.setMinPrice(hotelDetails.getMinPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingHotel.getMainImage() != null) {
                s3Service.deleteImage("hotel", existingHotel.getMainImage());
            }
            String fileName = uploadAndGetFileName(imageFile, hotelId);
            existingHotel.setMainImage(fileName);
        }

        return hotelRepo.save(existingHotel);
    }

    @Transactional
    public void deleteHotel(int hotelId) {
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn"));
        if (hotel.getMainImage() != null) {
            s3Service.deleteImage("hotel", hotel.getMainImage());
        }

        hotelRepo.delete(hotel);
    }

    private String uploadAndGetFileName(MultipartFile file, int id) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";

        // Tạo tên file: hotel_timestamp_random.jpg
        String customNameOnly = "hotel_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        String fullFileName = customNameOnly + extension;

        // Gọi S3Service để upload
        s3Service.uploadImage(file, "hotel", customNameOnly);

        return fullFileName;
    }

    @Transactional
    public Hotel updateHotelByManagerUsername(
            String managerUsername,
            Hotel hotelDetails,
            MultipartFile imageFile
    ) throws IOException {

        Hotel existingHotel = hotelRepo.findByManager_Username(managerUsername)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel không thuộc manager: " + managerUsername
                ));

        existingHotel.setName(hotelDetails.getName());
        existingHotel.setAddress(hotelDetails.getAddress());
        existingHotel.setPhoneNumber(hotelDetails.getPhoneNumber());
        existingHotel.setEmail(hotelDetails.getEmail());
        existingHotel.setDescription(hotelDetails.getDescription());
        existingHotel.setMinPrice(hotelDetails.getMinPrice());

        if (imageFile != null && !imageFile.isEmpty()) {
            if (existingHotel.getMainImage() != null) {
                s3Service.deleteImage("hotel", existingHotel.getMainImage());
            }
            String fileName = uploadAndGetFileName(imageFile, existingHotel.getHotelId());
            existingHotel.setMainImage(fileName);
        }

        return hotelRepo.save(existingHotel);
    }

    public User getManagerByHotelId(int hotelId) {
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn"));
        return hotel.getManager();
    }

}
