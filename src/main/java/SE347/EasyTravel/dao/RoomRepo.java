package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "rooms")
public interface RoomRepo extends JpaRepository<Room, Integer> {
    Optional<Room> findByRoomIdAndHotel_HotelId(int roomId, int hotelId);
}
