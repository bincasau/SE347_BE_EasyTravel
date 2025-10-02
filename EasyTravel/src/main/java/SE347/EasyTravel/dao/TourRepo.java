package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "tour")
public interface TourRepo extends JpaRepository<Tour, Integer> {
}
