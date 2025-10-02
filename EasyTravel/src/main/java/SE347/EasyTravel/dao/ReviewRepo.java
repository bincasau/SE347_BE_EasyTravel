package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "review")
public interface ReviewRepo extends JpaRepository<Review, Integer> {
}
