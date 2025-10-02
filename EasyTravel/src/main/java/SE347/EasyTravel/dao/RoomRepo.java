package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "room")
public interface RoomRepo extends JpaRepository<Room, Integer> {
}
