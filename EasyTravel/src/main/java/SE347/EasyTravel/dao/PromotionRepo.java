package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "promotion")
public interface PromotionRepo extends JpaRepository<Promotion, Integer> {
}
