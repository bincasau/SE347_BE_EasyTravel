package SE347.EasyTravel.controller;

import SE347.EasyTravel.entity.Comment;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.ForbiddenException;
import SE347.EasyTravel.exception.UnauthorizedException;
import SE347.EasyTravel.service.CommentService;
import SE347.EasyTravel.service.UserControlService;
import SE347.EasyTravel.service.UserInterfaceService;
import SE347.EasyTravel.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserControlService userService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/admin/users/save")
    public ResponseEntity<?> saveOrUpdate(
            @RequestPart("user") User user,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(userService.saveOrUpdate(user, file));
        } catch (Exception e) {
            logger.error("Error saving user", e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/admin/users/update/{id}")
    public ResponseEntity<?> updateByAdmin(
            @PathVariable int id,
            @RequestPart("user") User user,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return ResponseEntity.ok(userService.updateByAdmin(id, user, file));
        } catch (Exception e) {
            logger.error("Error updating user with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/account/update/my-profile")
    public ResponseEntity<?> updateOwnProfile(
            @RequestPart("user") User user,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal) {
        try {
            return ResponseEntity.ok(userService.updateOwnProfile(principal.getName(), user, file));
        } catch (Exception e) {
            logger.error("Error updating profile for user: {}", principal.getName(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/admin/users/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @DeleteMapping("/account/delete-mine")
    public ResponseEntity<?> deleteOwnAccount(Principal principal) {
        try {
            userService.deleteOwnAccount(principal.getName());
            return ResponseEntity.ok("Account deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting account for user: {}", principal.getName(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/auth/comments/add")
    public ResponseEntity<?> addComment(
            @RequestParam String content,
            @RequestParam int blogId,
            Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Bạn cần đăng nhập để bình luận");
        }
        Comment savedComment = commentService.saveComment(content, blogId, principal.getName());
        return ResponseEntity.ok(savedComment);
    }
    @PutMapping("/auth/comments/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable int commentId,
            @RequestBody String content,
            Principal principal) {
        Comment updated = commentService.updateComment(commentId, content, principal.getName());
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/auth/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable int commentId,
            Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.ok("Đã xóa bình luận thành công");
    }

}
