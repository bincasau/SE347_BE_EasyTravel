package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "hotel-booking")
public interface HotelBookingRepo extends JpaRepository<HotelBooking, Integer> {
}
