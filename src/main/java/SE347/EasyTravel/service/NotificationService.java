package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.NotificationRepo;
import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.entity.Notification;
import SE347.EasyTravel.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private NotificationRepo notificationRepo;
    private UserRepo userRepo;

    @Autowired
    public NotificationService(UserRepo userRepo, NotificationRepo notificationRepo) {
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
    }
    public List<Notification> getMyNotifications(String username) {
        // Sử dụng hàm visible để lấy cả thông báo cá nhân lẫn broadcast chung
        return notificationRepo.findVisibleNotifications(username, "ACTIVE");
    }
    @Transactional
    public Notification createBroadcast(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setBroadCast(true);
        notification.setRead(false);
        notification.setStatus("ACTIVE");
        return notificationRepo.save(notification);
    }

    @Transactional
    public void markAsRead(int notificationId, String name) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        notification.setRead(true);
        notificationRepo.save(notification);
    }
    @Transactional
    public void createPrivateNotification(String message, List<Integer> userIds) {
        List<User> targetUsers = userRepo.findAllById(userIds);

        if (targetUsers.isEmpty()) {
            throw new RuntimeException("Không tìm thấy người dùng nào hợp lệ để gửi thông báo");
        }

        for (User user : targetUsers) {
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setBroadCast(false);
            notification.setRead(false);
            notification.setStatus("ACTIVE");

            notification.setUser(user);

            notificationRepo.save(notification);
        }
    }
    public List<Notification> filterNotifications(String username, String status, Boolean isBroadCast) {
        if (username != null && !username.isEmpty()) {
            return notificationRepo.findVisibleNotifications(username, status != null ? status : "ACTIVE");
        }
        if (isBroadCast != null) {
            return notificationRepo.findByStatusAndIsBroadCast(status, isBroadCast);
        }
        return notificationRepo.findAll();
    }
    @Transactional
    public void deleteNotification(int id, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Chỉ Admin mới có quyền xóa thông báo hệ thống");
        }
        notificationRepo.deleteById(id);
    }
    @Transactional
    public List<Notification> getVisibleNotifications() {
        return notificationRepo.findByIsBroadCastTrueAndStatus("ACTIVE");
    }
    public List<Notification> adminGetNotifications(String status, Boolean isBroadcast, String search, String targetUser) {
        return notificationRepo.adminFilter(status, isBroadcast, search, targetUser);
    }
    @Transactional
    public Notification updateStatus(int id, String status) throws Exception {
        Notification notification = this.notificationRepo.findById(id).orElse(null);
        if(notification == null) throw new Exception("Id không tồn tại");
        else notification.setStatus(status);
        return notification;
    }
}
