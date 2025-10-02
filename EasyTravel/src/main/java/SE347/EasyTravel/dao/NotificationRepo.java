package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "notification")
public interface NotificationRepo extends JpaRepository<Notification, Integer> {
}
