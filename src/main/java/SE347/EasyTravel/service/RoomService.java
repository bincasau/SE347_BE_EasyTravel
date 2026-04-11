package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.HotelRepo;
import SE347.EasyTravel.dao.RoomRepo;
import SE347.EasyTravel.entity.Hotel;
import SE347.EasyTravel.entity.Room;
import SE347.EasyTravel.exception.ForbiddenException;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class RoomService {
    private RoomRepo roomRepo;
    private HotelRepo hotelRepo;
    private S3Service s3Service;

    @Autowired
    public RoomService(RoomRepo roomRepo, HotelRepo hotelRepo, S3Service s3Service) {
        this.roomRepo = roomRepo;
        this.hotelRepo = hotelRepo;
        this.s3Service = s3Service;
    }
    @Transactional
    public Room saveOrUpdateRoom(int hotelId, Room roomDetails, MultipartFile bedFile, MultipartFile wcFile) throws IOException {
        Room room;

        if (roomDetails.getRoomId() != 0) {
            room = roomRepo.findById(roomDetails.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng"));
        } else {
            room = new Room();
            Hotel hotel = hotelRepo.findById(hotelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Khách sạn không tồn tại"));
            room.setHotel(hotel);
        }

        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setRoomType(roomDetails.getRoomType());
        room.setPrice(roomDetails.getPrice());
        room.setNumberOfGuest(roomDetails.getNumberOfGuest());
        room.setDesc(roomDetails.getDesc());

        if (bedFile != null && !bedFile.isEmpty()) {
            if (room.getImageBed() != null) {
                s3Service.deleteImage("room", room.getImageBed());
            }
            String fileName = processUpload(bedFile, "room", "bed");
            room.setImageBed(fileName);
        }

        if (wcFile != null && !wcFile.isEmpty()) {
            if (room.getImageWC() != null) {
                s3Service.deleteImage("room", room.getImageWC());
            }
            String fileName = processUpload(wcFile, "room", "wc");
            room.setImageWC(fileName);
        }

        return roomRepo.save(room);
    }

    @Transactional
    public void deleteRoom(int roomId) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng không tồn tại"));

        if (room.getImageBed() != null) s3Service.deleteImage("room", room.getImageBed());
        if (room.getImageWC() != null) s3Service.deleteImage("room", room.getImageWC());

        roomRepo.delete(room);
    }

    private String processUpload(MultipartFile file, String folder, String type) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".")) : "";

        String customName = "room_" + type + "_" + System.currentTimeMillis();
        s3Service.uploadImage(file, folder, customName);

        return customName + ext;
    }

    @Transactional
    public Room saveOrUpdateRoomByManager(
            String managerUsername,
            Room roomDetails,
            MultipartFile bedFile,
            MultipartFile wcFile
    ) throws IOException {
        Hotel hotel = hotelRepo.findByManager_Username(managerUsername)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel không thuộc manager: " + managerUsername
                ));

        Room room;
        if (roomDetails.getRoomId() != 0) {
            room = roomRepo.findByRoomIdAndHotel_HotelId(
                            roomDetails.getRoomId(),
                            hotel.getHotelId()
                    )
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Phòng không tồn tại hoặc không thuộc hotel này"
                    ));
        }
        else {
            room = new Room();
            room.setHotel(hotel);
        }
        room.setRoomNumber(roomDetails.getRoomNumber());
        room.setRoomType(roomDetails.getRoomType());
        room.setPrice(roomDetails.getPrice());
        room.setNumberOfGuest(roomDetails.getNumberOfGuest());
        room.setDesc(roomDetails.getDesc());
        if (bedFile != null && !bedFile.isEmpty()) {
            if (room.getImageBed() != null) {
                s3Service.deleteImage("room", room.getImageBed());
            }
            room.setImageBed(processUpload(bedFile, "room", "bed"));
        }
        if (wcFile != null && !wcFile.isEmpty()) {
            if (room.getImageWC() != null) {
                s3Service.deleteImage("room", room.getImageWC());
            }
            room.setImageWC(processUpload(wcFile, "room", "wc"));
        }

        return roomRepo.save(room);
    }

    @Transactional
    public void deleteRoomByManager(String username, int roomId) {

        Hotel hotel = hotelRepo.findByManager_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel không tồn tại"));

        Room room = roomRepo.findByRoomIdAndHotel_HotelId(roomId, hotel.getHotelId())
                .orElseThrow(() -> new ForbiddenException("Không được xóa phòng này"));

        if (room.getImageBed() != null) s3Service.deleteImage("room", room.getImageBed());
        if (room.getImageWC() != null) s3Service.deleteImage("room", room.getImageWC());

        roomRepo.delete(room);
    }


}
