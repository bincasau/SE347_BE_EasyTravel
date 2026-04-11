package SE347.EasyTravel.controller;

import SE347.EasyTravel.entity.Notification;
import SE347.EasyTravel.exception.ForbiddenException;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import SE347.EasyTravel.exception.UnauthorizedException;
import SE347.EasyTravel.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @GetMapping("/notifications/my")
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam(required = false, defaultValue = "ACTIVE") String status,
            Principal principal) {
        if (principal == null) throw new UnauthorizedException("Unauthorized");

        List<Notification> notifications = notificationService.filterNotifications(
                principal.getName(), status, null);
        return ResponseEntity.ok(notifications);
    }
    @GetMapping("/notifications/public/list")
    public ResponseEntity<List<Notification>> getAllVisibleNotifications() {
        List<Notification> notifications = notificationService.getVisibleNotifications();
        return ResponseEntity.ok(notifications);
    }
    @PostMapping("/admin/notif/broadcast")
    public ResponseEntity<Notification> sendBroadcast(@RequestParam String message) {
        return ResponseEntity.ok(notificationService.createBroadcast(message));
    }
    @PostMapping("/admin/notif/send-to-specific")
    public ResponseEntity<?> sendToUsers(
            @RequestParam String message,
            @RequestParam List<Integer> userIds) {
        try {
            notificationService.createPrivateNotification(message, userIds);
            return ResponseEntity.ok("Đã gửi thông báo đến " + userIds.size() + " người dùng.");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable int id, Principal principal) {
        if (principal == null) throw new UnauthorizedException("Unauthorized");
        notificationService.markAsRead(id, principal.getName());
        return ResponseEntity.ok("Đã đánh dấu là đã đọc");
    }

    @PatchMapping("/admin/notif/{id}/status")
    public Notification updateStatus(
            @PathVariable int id,
            @RequestParam String status) throws Exception {
        return this.notificationService.updateStatus(id, status);
    }
    @DeleteMapping("/admin/notif/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, Principal principal) {
        try {
            notificationService.deleteNotification(id, principal.getName());
            return ResponseEntity.ok("Đã xóa thông báo thành công");
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }
    }
    @GetMapping("/admin/notif/all")
    public ResponseEntity<List<Notification>> adminGetAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isBroadcast,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String targetUser) {
        List<Notification> list = notificationService.adminGetNotifications(status, isBroadcast, search, targetUser);
        return ResponseEntity.ok(list);
    }
}
