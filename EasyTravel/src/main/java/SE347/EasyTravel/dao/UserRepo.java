package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "user")
public interface UserRepo extends JpaRepository<User, Integer> {
}
