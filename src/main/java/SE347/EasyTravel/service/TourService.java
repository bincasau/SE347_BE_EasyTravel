package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.ItineraryRepo;
import SE347.EasyTravel.dao.TourRepo;
import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.entity.Image;
import SE347.EasyTravel.entity.Itinerary;
import SE347.EasyTravel.entity.Tour;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TourService {
    private TourRepo tourRepo;
    private ItineraryRepo itineraryRepo;
    private S3Service s3Service;
    private UserRepo userRepo;

    @Autowired
    public TourService(TourRepo tourRepo, ItineraryRepo itineraryRepo, S3Service s3Service, UserRepo userRepo) {
        this.tourRepo = tourRepo;
        this.itineraryRepo = itineraryRepo;
        this.s3Service = s3Service;
        this.userRepo = userRepo;
    }

    @Transactional
    public Tour saveOrUpdateTour(Tour tourDetails, List<Integer> guideIds, MultipartFile mainImageFile) throws IOException {
        Tour tour;

        if (tourDetails.getTourId() != 0) {
            tour = tourRepo.findById(tourDetails.getTourId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour với ID: " + tourDetails.getTourId()));
        } else {
            tour = new Tour();
        }
        tour.setTitle(tourDetails.getTitle());
        tour.setDescription(tourDetails.getDescription());
        tour.setPriceAdult(tourDetails.getPriceAdult());
        tour.setPriceChild(tourDetails.getPriceChild());
        tour.setPercentDiscount(tourDetails.getPercentDiscount());
        tour.setDurationDays(tourDetails.getDurationDays());
        tour.setStartDate(tourDetails.getStartDate());
        tour.setEndDate(tourDetails.getEndDate());
        tour.setDepartureLocation(tourDetails.getDepartureLocation());
        tour.setDestination(tourDetails.getDestination());
        tour.setAvailableSeats(tourDetails.getAvailableSeats());
        tour.setLimitSeats(tourDetails.getLimitSeats());
        tour.setStatus(tourDetails.getStatus());

        if(tourDetails.getMainImage() != null){
            tour.setMainImage(tour.getMainImage());
        }

        if (guideIds != null && !guideIds.isEmpty()) {
            List<User> selectedGuides = userRepo.findAllById(guideIds);
            tour.setTourGuides(selectedGuides);
        } else {
            tour.getTourGuides().clear();
        }
        if (mainImageFile != null && !mainImageFile.isEmpty()) {
            if (tour.getMainImage() != null) {
                s3Service.deleteImage("tour", tour.getMainImage());
            }
            String customName = "tour_main_" + System.currentTimeMillis();
            s3Service.uploadImage(mainImageFile, "tour", customName);

            String originalFilename = mainImageFile.getOriginalFilename();
            String ext = (originalFilename != null) ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            tour.setMainImage(customName + ext);
        }

        return tourRepo.save(tour);
    }

    @Transactional
    public Itinerary addItinerary(int tourId, Itinerary itiDetails) {
        Tour tour = tourRepo.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour với ID: " + tourId));

        itiDetails.setTour(tour);
        itiDetails.setTitle(itiDetails.getTitle());
        itiDetails.setDayNumber(itiDetails.getDayNumber());
        itiDetails.setActivities(itiDetails.getActivities());

        return itineraryRepo.save(itiDetails);
    }

    @Transactional
    public void deleteTour(int tourId) {
        Tour tour = tourRepo.findById(tourId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour với ID: " + tourId));
        if (tour.getMainImage() != null) {
            s3Service.deleteImage("tour", tour.getMainImage());
        }
        tourRepo.delete(tour);
    }

    @Transactional
    public Itinerary updateItinerary(int itineraryId, Itinerary details) {
        Itinerary iti = itineraryRepo.findById(itineraryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch trình"));

        iti.setTitle(details.getTitle());
        iti.setDayNumber(details.getDayNumber());
        iti.setActivities(details.getActivities());

        return itineraryRepo.save(iti);
    }

    @Transactional
    public void deleteItinerary(int itineraryId) {
        itineraryRepo.deleteById(itineraryId);
    }

    @Transactional
    public Tour copyTour(int originalTourId) {
        Tour originalTour = tourRepo.findById(originalTourId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Tour gốc để copy"));

        Tour newTour = new Tour();
        newTour.setTitle(originalTour.getTitle() + " (Bản sao)");
        newTour.setDescription(originalTour.getDescription());
        newTour.setPriceAdult(originalTour.getPriceAdult());
        newTour.setPriceChild(originalTour.getPriceChild());
        newTour.setPercentDiscount(originalTour.getPercentDiscount());
        newTour.setDurationDays(originalTour.getDurationDays());
        newTour.setStartDate(originalTour.getStartDate());
        newTour.setEndDate(originalTour.getEndDate());
        newTour.setDepartureLocation(originalTour.getDepartureLocation());
        newTour.setDestination(originalTour.getDestination());
        newTour.setLimitSeats(originalTour.getLimitSeats());
        newTour.setAvailableSeats(originalTour.getLimitSeats()); // Reset số ghế trống về tối đa
        newTour.setMainImage(originalTour.getMainImage());
        newTour.setStatus("Draft"); // Đặt trạng thái nháp để chỉnh sửa trước khi công khai

        // 3. Sao chép danh sách Itinerary (Lịch trình)
        if (originalTour.getItineraries() != null) {
            List<Itinerary> newItineraries = new ArrayList<>();
            for (Itinerary originalItinerary : originalTour.getItineraries()) {
                Itinerary newItinerary = new Itinerary();
                newItinerary.setTitle(originalItinerary.getTitle());
                newItinerary.setDayNumber(originalItinerary.getDayNumber());
                newItinerary.setActivities(originalItinerary.getActivities());
                newItinerary.setTour(newTour); // Gán vào tour mới
                newItineraries.add(newItinerary);
            }
            newTour.setItineraries(newItineraries);
        }

        if (originalTour.getImages() != null) {
            List<Image> newImages = new ArrayList<>();
            for (Image originalImage : originalTour.getImages()) {
                Image newImage = new Image();
                newImage.setUrl(originalImage.getUrl());
                newImage.setTitle(originalImage.getTitle());
                newImage.setAltText(originalImage.getAltText());
                newImage.setTour(newTour);
                newImages.add(newImage);
            }
            newTour.setImages(newImages);
        }
        return tourRepo.save(newTour);
    }
}
