package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "image")
public interface ImageRepo extends JpaRepository<Image, Integer> {
}
