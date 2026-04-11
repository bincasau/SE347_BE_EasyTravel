package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.TourRepo;
import SE347.EasyTravel.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class TourGuideService {
    private TourRepo tourRepo;

    public TourGuideService(TourRepo tourRepo) {
        this.tourRepo = tourRepo;
    }

    public Page<Tour> getHistory(String username, Pageable pageable) {
        return tourRepo.findByStatusAndTourGuides_Username("Passed", username, pageable);
    }

    public Page<Tour> getFuture(String username, Pageable pageable) {
        return tourRepo.findByStatusAndTourGuides_Username("Activated", username, pageable);
    }

    public Page<Tour> getUpcoming(String username, Pageable pageable) {
        Date today = new Date(System.currentTimeMillis());
        return tourRepo.findByTourGuides_UsernameAndStartDateAfter(username, today, pageable);
    }

    public Page<Tour> getScheduleByMonth(String username, int month, int year, Pageable pageable) {
        return tourRepo.findToursByMonthAndYearForGuide(username, month, year, pageable);
    }
}
