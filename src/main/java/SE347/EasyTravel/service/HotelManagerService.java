package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.HotelBookingRepo;
import SE347.EasyTravel.dao.HotelRepo;
import SE347.EasyTravel.entity.Hotel;
import SE347.EasyTravel.entity.HotelBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelManagerService {
    private HotelBookingRepo hotelBookingRepo;
    private HotelRepo hotelRepo;

    @Autowired
    public HotelManagerService(HotelBookingRepo hotelBookingRepo, HotelRepo hotelRepo) {
        this.hotelBookingRepo = hotelBookingRepo;
        this.hotelRepo = hotelRepo;
    }

    public Hotel getHotelByManager(String username) {
        return hotelRepo.findByManager_Username(username).orElse(null);
    }

    public Page<HotelBooking> getMonthlyBookings(String username, int month, int year, Pageable pageable) {
        return hotelBookingRepo.findMonthlyBookings(username, month, year, pageable);
    }

    public Map<String, Object> getMonthlyStats(String username, int month, int year) {
        List<Object[]> statsList = hotelBookingRepo.getStatsByRoomType(username, month, year);
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();

        double grandTotalRevenue = 0;
        long grandTotalBookings = 0;

        for (Object[] stats : statsList) {
            Map<String, Object> item = new HashMap<>();
            item.put("roomType", stats[0]);
            item.put("count", stats[1]);
            item.put("revenue", stats[2]);

            details.add(item);

            grandTotalBookings += (Long) stats[1];
            grandTotalRevenue += (Double) stats[2];
        }

        response.put("details", details); 
        response.put("allTypeBookings", grandTotalBookings); // Tổng tất cả đơn
        response.put("allTypeRevenue", grandTotalRevenue);   // Tổng tất cả tiền

        return response;
    }
}
