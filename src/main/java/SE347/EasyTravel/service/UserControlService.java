package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserControlService {

    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private S3Service s3Service;

    @Autowired
    public UserControlService(UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, S3Service s3Service) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
    }

    @Transactional
    public User saveOrUpdate(User user, MultipartFile file) throws IOException {
        if (user.getUserId() != 0) {
            User existing = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User id " + user.getUserId() + " not found"));
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(existing.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            if (file == null || file.isEmpty()) {
                user.setAvatar(existing.getAvatar());
            } else {
                if (existing.getAvatar() != null) s3Service.deleteImage("user", existing.getAvatar());
                user.setAvatar(uploadAvatar(file));
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword().trim()));
            if (file != null) user.setAvatar(uploadAvatar(file));
        }
        return userRepo.save(user);
    }
    @Transactional
    public User updateByAdmin(int id, User details, MultipartFile file) throws IOException {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không thấy User ID: " + id));
        return applyChanges(user, details, file);
    }

    @Transactional
    public User updateOwnProfile(String username, User details, MultipartFile file) throws IOException {
        User user = userRepo.findByUsername(username);
        if (user == null) throw new ResourceNotFoundException("Không thấy User: " + username);
        return applyChanges(user, details, file);
    }

    public Page<User> searchUsers(String role, String status, Pageable pageable) {
        if (role != null && status != null) return userRepo.findByRoleAndStatus(role, status, pageable);
        if (role != null) return userRepo.findByRole(role, pageable);
        if (status != null) return userRepo.findByStatus(status, pageable);
        return userRepo.findAll(pageable);
    }
    @Transactional
    public void deleteUser(int id) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found"));
        if (user.getAvatar() != null) s3Service.deleteImage("user", user.getAvatar());
        userRepo.delete(user);
    }
    private User applyChanges(User target, User source, MultipartFile file) throws IOException {
        target.setName(source.getName());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setGender(source.getGender());
        target.setDob(source.getDob());
        target.setAddress(source.getAddress());
        if (source.getRole() != null) target.setRole(source.getRole());
        if (source.getStatus() != null) target.setStatus(source.getStatus());

        if (file != null && !file.isEmpty()) {
            if (target.getAvatar() != null) s3Service.deleteImage("user", target.getAvatar());
            target.setAvatar(uploadAvatar(file));
        }
        return userRepo.save(target);
    }
    private String uploadAvatar(MultipartFile file) throws IOException {
        String name = "avatar_" + System.currentTimeMillis();
        s3Service.uploadImage(file, "user", name);
        return name + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
    }

    @Transactional
    public void deleteOwnAccount(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy tài khoản để xóa!");
        }
        if (user.getAvatar() != null) {
            s3Service.deleteImage("user", user.getAvatar());
        }

        userRepo.delete(user);
    }
}
