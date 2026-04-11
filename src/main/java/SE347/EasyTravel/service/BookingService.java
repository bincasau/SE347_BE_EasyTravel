package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.*;
import SE347.EasyTravel.dto.BookingHotelRequest;
import SE347.EasyTravel.dto.BookingTourRequest;
import SE347.EasyTravel.dto.MonthlyTourStatsDTO;
import SE347.EasyTravel.entity.*;
import SE347.EasyTravel.exception.BadRequestException;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class BookingService {

    private final UserRepo userRepo;
    private final TourRepo tourRepo;
    private final HotelRepo hotelRepo;
    private final RoomRepo roomRepo;
    private final TourBookingRepo tourBookingRepo;
    private final HotelBookingRepo hotelBookingRepo;

    @Autowired
    public BookingService(UserRepo userRepo, TourRepo tourRepo, HotelRepo hotelRepo, RoomRepo roomRepo, TourBookingRepo tourBookingRepo, HotelBookingRepo hotelBookingRepo) {
        this.userRepo = userRepo;
        this.tourRepo = tourRepo;
        this.hotelRepo = hotelRepo;
        this.roomRepo = roomRepo;
        this.tourBookingRepo = tourBookingRepo;
        this.hotelBookingRepo = hotelBookingRepo;
    }

    public TourBooking createTourBooking(BookingTourRequest bookingTourRequest){
        User user = this.userRepo.findByEmail(bookingTourRequest.getEmail());
        if(user == null) throw new ResourceNotFoundException("User not found!");
        Tour tour = this.tourRepo.findById(bookingTourRequest.getTourId())
            .orElseThrow(() -> new ResourceNotFoundException("Tour not found!"));
        if(bookingTourRequest.getChildren() + bookingTourRequest.getAdults() > tour.getAvailableSeats())
            throw new BadRequestException("Not enough available seats");
        TourBooking tourBooking = new TourBooking();
        tour.setAvailableSeats(tour.getAvailableSeats() - bookingTourRequest.getAdults() - bookingTourRequest.getChildren());
        this.tourRepo.save(tour);
        tourBooking.setTour(tour);
        tourBooking.setAdults(bookingTourRequest.getAdults());
        tourBooking.setChildren(bookingTourRequest.getChildren());
        tourBooking.setUser(user);
        tourBooking.setTotalPrice(bookingTourRequest.getTotalPrice());
        tourBooking.setBookingDate(java.time.LocalDateTime.now());
        tourBooking.setStatus("Pending");

        return this.tourBookingRepo.save(tourBooking);
    }
    public HotelBooking createHotelBooking(BookingHotelRequest bookingHotelRequest){
        User user = this.userRepo.findByEmail(bookingHotelRequest.getGmail());
        if(user == null) throw new ResourceNotFoundException("User not found!");
        Hotel hotel = this.hotelRepo.findById(bookingHotelRequest.getHotelId())
            .orElseThrow(() -> new ResourceNotFoundException("Hotel not found!"));
        Room room = this.roomRepo.findById(bookingHotelRequest.getRoomID())
            .orElseThrow(() -> new ResourceNotFoundException("Room not found!"));

        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotel(hotel);
        hotelBooking.setStatus("Pending");
        hotelBooking.setUser(user);
        hotelBooking.setRoom(room);
        hotelBooking.setTotalPrice(bookingHotelRequest.getTotalPrice());
        hotelBooking.setCheckInDate(bookingHotelRequest.getCheckInDate());
        hotelBooking.setCheckOutDate(bookingHotelRequest.getCheckOutDate());

        hotelBooking.setCreatedAt(java.time.LocalDateTime.now());

        return this.hotelBookingRepo.save(hotelBooking);
    }

    public TourBooking findTourBookingById(int bookingId) {
        return tourBookingRepo.findById(bookingId).orElse(null);
    }

    public HotelBooking findHotelBookingById(int bookingId) {
        return hotelBookingRepo.findById(bookingId).orElse(null);
    }

    public void updateTourBookingStatus(TourBooking booking, String status) {
        booking.setStatus(status);
        tourBookingRepo.save(booking);
    }

    public void updateHotelBookingStatus(HotelBooking booking, String status) {
        booking.setStatus(status);
        hotelBookingRepo.save(booking);
    }
    public Page<HotelBooking> getHotelHistory(String username, Date start, Date end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Date sDate = (start == null) ? Date.valueOf("2000-01-01") : start;
        Date eDate = (end == null) ? Date.valueOf("2100-01-01") : end;

        return hotelBookingRepo.findMyHistoryFullInfo(username, sDate, eDate, pageable);
    }
    public Page<TourBooking> getMyTourHistory(String username, Date start, Date end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tourBookingRepo.findMyTourHistoryFull(username, start, end, pageable);
    }
    public List<TourBooking> getParticipantsByTour(int tourId) {
        return tourBookingRepo.findByTourIdWithUser(tourId);
    }
    public MonthlyTourStatsDTO getMonthlyReport(int month, int year) {
        MonthlyTourStatsDTO stats = tourBookingRepo.getMonthlyStats(month, year);
        if (stats == null || stats.getTotalTours() == 0) {
            return new MonthlyTourStatsDTO(month, year, 0L, 0L, 0L, 0L, 0L, 0L, 0.0);
        }
        return stats;
    }
}
