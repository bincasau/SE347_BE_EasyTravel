package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "notification")
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

    // Sửa users -> user
    List<Notification> findByUser_UsernameOrderByCreatedAtDesc(String username);

    List<Notification> findByStatusAndIsBroadCast(String status, boolean isBroadCast);

    List<Notification> findByUser_UsernameAndStatus(String username, String status);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.status = :status AND (n.isBroadCast = true OR n.user.username = :username) " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findVisibleNotifications(@Param("username") String username, @Param("status") String status);

    List<Notification> findByIsBroadCastTrueAndStatus(String status);

    @Query("SELECT DISTINCT n FROM Notification n LEFT JOIN n.user u " + // Sửa n.users -> n.user
            "WHERE (:status IS NULL OR n.status = :status) " +
            "AND (:isBroadcast IS NULL OR n.isBroadCast = :isBroadcast) " +
            "AND (:message IS NULL OR n.message LIKE %:message%) " +
            "AND (:targetUsername IS NULL OR u.username LIKE %:targetUsername% OR u.name LIKE %:targetUsername%) " +
            "ORDER BY n.createdAt DESC")
    List<Notification> adminFilter(
            @Param("status") String status,
            @Param("isBroadcast") Boolean isBroadcast,
            @Param("message") String message,
            @Param("targetUsername") String targetUsername);

    List<Notification> findByIsBroadCastTrueOrderByCreatedAtDesc();

    @Query("SELECT n FROM Notification n WHERE n.isBroadCast = true ORDER BY n.createdAt DESC")
    List<Notification> findAllBroadcasts();
}
