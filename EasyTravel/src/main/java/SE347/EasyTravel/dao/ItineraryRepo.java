package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Image;
import SE347.EasyTravel.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "itinerary")
public interface ItineraryRepo extends JpaRepository<Itinerary, Integer> {
}
