package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "hotel")
public interface HotelRepo extends JpaRepository<Hotel, Integer> {
}
