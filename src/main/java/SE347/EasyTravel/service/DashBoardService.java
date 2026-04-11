package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.*;
import SE347.EasyTravel.dto.AdminDashboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashBoardService {
    private final TourRepo tourRepo;
    private final UserRepo userRepo;
    private final HotelRepo hotelRepo;
    private final PaymentRepo paymentRepo;
    private final TourBookingRepo tourBookingRepo;
    private final NotificationRepo notificationRepo;

    @Autowired
    public DashBoardService(TourRepo tourRepo, UserRepo userRepo, HotelRepo hotelRepo,
                            PaymentRepo paymentRepo, TourBookingRepo tourBookingRepo,
                            NotificationRepo notificationRepo) {
        this.tourRepo = tourRepo;
        this.userRepo = userRepo;
        this.hotelRepo = hotelRepo;
        this.paymentRepo = paymentRepo;
        this.tourBookingRepo = tourBookingRepo;
        this.notificationRepo = notificationRepo;
    }

    public AdminDashboardDTO getAdminStats() {
        AdminDashboardDTO dto = new AdminDashboardDTO();

        // 1. Thống kê Tour
        dto.setTotalToursActive(tourRepo.countByStatus("Activated"));
        dto.setTotalToursCanceled(tourRepo.countByStatus("Cancelled"));
        dto.setTotalToursPassed(tourRepo.countByStatus("Passed"));

        // 2. Thống kê User
        dto.setCountAdmin(userRepo.countByRole("ADMIN"));
        dto.setCountCustomer(userRepo.countByRole("CUSTOMER"));
        dto.setCountTourGuide(userRepo.countByRole("TOUR_GUIDE"));
        dto.setCountHotelManager(userRepo.countByRole("HOTEL_MANAGER"));

        // 3. Khách sạn & Booking tháng này
        dto.setTotalHotels(hotelRepo.count());

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        dto.setTotalBookingsThisMonth(tourBookingRepo.countByBookingDateAfter(startOfMonth));

        // 4. Doanh thu (Sửa key để lấy dữ liệu chính xác từ Map)
        Map<String, Double> revenueMap = calculateRevenueLast6Months();
        dto.setRevenueLast6Months(revenueMap);

        String currentMonthKey = LocalDate.now().getMonth().toString();
        dto.setTotalRevenueThisMonth(revenueMap.getOrDefault(currentMonthKey, 0.0));

        // 5. Tour sắp khởi hành (Active)
        dto.setUpcomingActiveTours(tourRepo.findTop5ByStatusAndStartDateAfterOrderByStartDateAsc(
                "Activated", Date.valueOf(LocalDate.now())));

        // 6. Thông báo Broadcast (Sửa theo trường isBroadCast trong Entity)
        dto.setBroadcastNotifications(notificationRepo.findByIsBroadCastTrueOrderByCreatedAtDesc());

        return dto;
    }

    private Map<String, Double> calculateRevenueLast6Months() {
        Map<String, Double> chartData = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusMonths(i);
            String monthName = date.getMonth().toString();

            // Gọi hàm Native Query trong PaymentRepo
            Double monthlySum = paymentRepo.getRevenueByMonth(date.getMonthValue(), date.getYear());
            chartData.put(monthName, monthlySum != null ? monthlySum : 0.0);
        }
        return chartData;
    }
}