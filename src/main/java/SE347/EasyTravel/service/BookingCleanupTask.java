package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.HotelBookingRepo;
import SE347.EasyTravel.dao.TourBookingRepo;
import SE347.EasyTravel.dao.TourRepo;
import SE347.EasyTravel.entity.HotelBooking;
import SE347.EasyTravel.entity.Tour;
import SE347.EasyTravel.entity.TourBooking;
import SE347.EasyTravel.entity.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BookingCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(BookingCleanupTask.class);

    @Autowired
    private TourBookingRepo tourBookingRepo;

    @Autowired
    private HotelBookingRepo hotelBookingRepo;

    @Autowired
    private TourRepo tourRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PaymentService paymentService;

    /**
     * Tự động hủy các đơn hàng Pending quá 15 phút (Chạy mỗi phút 1 lần)
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime limitTime = LocalDateTime.now().minusMinutes(15);

        // Xử lý Tour Booking
        List<TourBooking> expiredTours = tourBookingRepo.findAllByStatusAndBookingDateBefore("Pending", limitTime);
        for (TourBooking booking : expiredTours) {
            booking.setStatus("Failed");
            Tour tour = booking.getTour();
            int seatsToReturn = booking.getAdults() + booking.getChildren();
            tour.setAvailableSeats(tour.getAvailableSeats() + seatsToReturn);
            tourRepo.save(tour);
            tourBookingRepo.save(booking);

            notificationService.createPrivateNotification(
                    "Đơn đặt Tour #" + booking.getBookingId() + " đã bị hủy do quá 15 phút chưa thanh toán.",
                    List.of(booking.getUser().getUserId())
            );
        }

        // Xử lý Hotel Booking
        List<HotelBooking> expiredHotels = hotelBookingRepo.findAllByStatusAndCreatedAtBefore("Pending", limitTime);
        for (HotelBooking booking : expiredHotels) {
            booking.setStatus("Failed");
            hotelBookingRepo.save(booking);
            notificationService.createPrivateNotification(
                    "Đơn đặt khách sạn #" + booking.getBookingId() + " đã bị hủy do quá hạn thanh toán.",
                    List.of(booking.getUser().getUserId())
            );
        }
    }

    /**
     * MỐC 4 NGÀY TRƯỚC KHỞI HÀNH: Gửi thông báo/Cảnh báo
     * Chạy vào 08:00 sáng hàng ngày
     */
    @Scheduled(cron = "0 45 22 * * ?")
    @Transactional
    public void notifyFourDaysBefore() {
        // Cần đảm bảo TourRepo có hàm findToursStartingInNDays(4)
        List<Tour> upcomingTours = tourRepo.findToursStartingInFourDays();

        for (Tour tour : upcomingTours) {
            sendTourNotifications(tour, 4);
        }
    }

    /**
     * MỐC 3 NGÀY TRƯỚC KHỞI HÀNH: Hủy Tour & Refund nếu thiếu người
     * Chạy vào 09:00 sáng hàng ngày
     */
    @Scheduled(cron = "0 45 22 * * ?")
    @Transactional
    public void processThreeDaysBefore() {
        // Cần đảm bảo TourRepo có hàm findToursStartingInNDays(3)
        List<Tour> upcomingTours = tourRepo.findToursStartingInThreeDays();

        for (Tour tour : upcomingTours) {
            List<TourBooking> confirmedBookings = tour.getTourBookings().stream()
                    .filter(b -> "Success".equalsIgnoreCase(b.getStatus()))
                    .toList();

            int totalParticipants = confirmedBookings.stream()
                    .mapToInt(b -> b.getAdults() + b.getChildren())
                    .sum();

            // ĐIỀU KIỆN HỦY: Nếu số người tham gia < 50% số lượng tối đa (Bạn có thể sửa lại logic này)
            boolean isEnoughPeople = totalParticipants >= (tour.getLimitSeats() / 2);

            if (isEnoughPeople) {
                // Đủ người: Gửi thông báo xác nhận khởi hành cuối cùng
                sendTourNotifications(tour, 3);
            } else {
                // Thiếu người: Thực hiện hủy toàn bộ đơn đặt và Refund
                cancelTourAndRefund(tour, confirmedBookings);
            }
        }
    }

    /**
     * Hàm dùng chung để gửi thông báo/email
     */
    private void sendTourNotifications(Tour tour, int daysLeft) {
        List<TourBooking> confirmedBookings = tour.getTourBookings().stream()
                .filter(b -> "Success".equalsIgnoreCase(b.getStatus()))
                .toList();

        int totalParticipants = confirmedBookings.stream()
                .mapToInt(b -> b.getAdults() + b.getChildren())
                .sum();

        boolean isEnoughPeople = totalParticipants >= (tour.getLimitSeats() / 2);

        for (TourBooking booking : confirmedBookings) {
            User user = booking.getUser();
            String content = "";
            String subject = "Cập nhật lịch trình Tour: " + tour.getTitle();

            if (isEnoughPeople) {
                String address = (user.getAddress() != null) ? user.getAddress().toLowerCase() : "";
                String transport = (address.contains("hà nội") || address.contains("hồ chí minh") || address.contains("tp hcm"))
                        ? "xe công ty đưa đón bạn tận nơi."
                        : "vui lòng có mặt tại trụ sở chính để khởi hành.";

                content = "Chào " + user.getName() + "! Chuyến đi [" + tour.getTitle() + "] khởi hành sau " + daysLeft + " ngày nữa. " + transport;
            } else {
                // Chỉ cảnh báo ở mốc 4 ngày, mốc 3 ngày sẽ vào hàm cancelTourAndRefund
                if (daysLeft == 4) {
                    content = "Cảnh báo: Tour [" + tour.getTitle() + "] hiện chưa đủ số lượng người tối thiểu. Chuyến đi có thể bị hủy nếu không đủ người trong 24h tới.";
                }
            }

            if (!content.isEmpty()) {
                notificationService.createPrivateNotification(content, List.of(user.getUserId()));
                emailService.sendTourNotificationEmail(user.getEmail(), subject, content);
            }
        }
    }

    /**
     * Hàm xử lý hủy tour và refund tiền
     */
    private void cancelTourAndRefund(Tour tour, List<TourBooking> bookings) {
        for (TourBooking booking : bookings) {
            try {
                // Gọi logic Refund đã viết trong PaymentService
                paymentService.processRefundByBooking("TOUR", booking.getBookingId(), null, "SYSTEM_AUTO");

                String content = "THÔNG BÁO HỦY TOUR: Rất tiếc, chuyến đi [" + tour.getTitle() + "] đã bị hủy do không đủ số lượng thành viên. Tiền thanh toán đã được hoàn trả lại cho bạn.";
                notificationService.createPrivateNotification(content, List.of(booking.getUser().getUserId()));
                emailService.sendTourNotificationEmail(booking.getUser().getEmail(), "Thông báo hủy Tour và Hoàn tiền", content);
                booking.setStatus("Failed");
            } catch (Exception e) {
                logger.error("Error auto-refunding booking #: {}", booking.getBookingId(), e);
            }
        }
        tour.setStatus("Cancelled");
        tourRepo.save(tour);
        logger.info("Cancelled tour ID: {} due to insufficient participants.", tour.getTourId());
    }
}