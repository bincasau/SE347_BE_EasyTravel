package SE347.EasyTravel.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "is_read")
    private boolean isRead;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "is_broadcast")
    private boolean isBroadCast;

    @ManyToMany(mappedBy = "notifications", fetch = FetchType.LAZY)
    private List<User> users;
}
