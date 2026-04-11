package SE347.EasyTravel.dto;

import SE347.EasyTravel.entity.Notification;
import SE347.EasyTravel.entity.Tour;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AdminDashboardDTO {
    private long totalToursActive;
    private long totalToursCanceled;
    private long totalToursPassed;

    private long countAdmin;
    private long countCustomer;
    private long countTourGuide;
    private long countHotelManager;

    private long totalHotels;
    private long totalBookingsThisMonth;
    private double totalRevenueThisMonth;

    private Map<String, Double> revenueLast6Months;

    private List<Tour> upcomingActiveTours;
    private List<Notification> broadcastNotifications;
}
