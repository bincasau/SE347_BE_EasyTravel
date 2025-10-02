package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.TourBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "tour-booking")
public interface TourBookingRepo extends JpaRepository<TourBooking, Integer> {
}
