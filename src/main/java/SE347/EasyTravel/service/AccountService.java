package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.BadRequestException;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;

    @Autowired
    public AccountService(UserRepo userRepo, BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService) {
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
    }

    public ResponseEntity<?> signUpUser(User user){
        if(userRepo.existsByUsername(user.getUsername())){
            throw new BadRequestException("Username already exists!");
        }
        if(userRepo.existsByEmail(user.getEmail())){
            throw new BadRequestException("Email already exists!");
        }
        String passwordEncode = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(passwordEncode);
        user.setCode(genCode());
        user.setRole("CUSTOMER");
        user.setAvatar("user_default.jpg");
        user.setStatus("Not activated");
        this.userRepo.save(user);
        this.emailService.sendMessage(user.getEmail(), user.getCode());
        return ResponseEntity.ok("Registration successful!");
    }
    private String genCode(){
        return UUID.randomUUID().toString();
    }

    public ResponseEntity<?> activeAccount(String email, String code){
        User user = this.userRepo.findByEmail(email);
        if(user == null){
            throw new ResourceNotFoundException("User not found!");
        }

        if("Activated".equals(user.getStatus())){
            return ResponseEntity.ok("Account already activated");
        }

        if(code.equals(user.getCode())){
            user.setStatus("Activated");
            this.userRepo.save(user);
            return ResponseEntity.ok("Account has been successfully activated");
        } else {
            throw new BadRequestException("Activation code does not match");
        }
    }
    public ResponseEntity<?> changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại!");
        }
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác!");
        }
        if (oldPassword.equals(newPassword)) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ!");
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepo.save(user);
        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }
    // 1. Hàm tạo mật khẩu ngẫu nhiên 8 ký tự (Hoa, thường, số, đặc biệt)
    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "!@#$%^&*";
        String all = upper + lower + digits + specials;

        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();

        // Đảm bảo có ít nhất 1 ký tự mỗi loại
        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(specials.charAt(random.nextInt(specials.length())));

        // Thêm cho đủ 8 ký tự
        for (int i = 4; i < 8; i++) {
            sb.append(all.charAt(random.nextInt(all.length())));
        }

        // Trộn ngẫu nhiên chuỗi
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

//    public ResponseEntity<?> resetPassword(java.security.Principal principal) {
//
//        User user = userRepo.findByUsername(principal.getName());
//        if (user == null) return ResponseEntity.badRequest().body("User not found");
//        String newRawPassword = generateRandomPassword();
//        user.setPassword(bCryptPasswordEncoder.encode(newRawPassword));
//        userRepo.save(user);
//        emailService.sendPasswordEmail(user.getEmail(), newRawPassword);
//
//        return ResponseEntity.ok("Mật khẩu mới đã được gửi về email của bạn!");
//    }
    @Transactional
    public ResponseEntity<?> sendResetCode(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new ResourceNotFoundException("Email không tồn tại!");
        String resetCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        user.setCode(resetCode);
        userRepo.save(user);

        String subject = "Mã xác nhận đặt lại mật khẩu";
        String content = "Mã xác nhận của bạn là: " + resetCode + ". Mã này dùng để đặt lại mật khẩu.";
        emailService.sendPasswordEmail(user.getEmail(), resetCode);

        return ResponseEntity.ok("Mã xác nhận đã được gửi!");
    }
    @Transactional
    public ResponseEntity<?> confirmResetPassword(String email, String code, String newPassword) {
        User user = userRepo.findByEmail(email);
        if (user == null) throw new ResourceNotFoundException("User không tồn tại!");

        if (user.getCode() == null || !user.getCode().equals(code)) {
            throw new BadRequestException("Mã xác nhận không chính xác!");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setCode(null);
        userRepo.save(user);

        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
    }
}
