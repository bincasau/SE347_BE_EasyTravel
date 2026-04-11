package SE347.EasyTravel.controller;

import SE347.EasyTravel.dto.JwtResponse;
import SE347.EasyTravel.dto.LoginRequest;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import SE347.EasyTravel.exception.UnauthorizedException;
import SE347.EasyTravel.service.AccountService;
import SE347.EasyTravel.service.JwtService;
import SE347.EasyTravel.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        User user = this.userService.findByUsername(loginRequest.getUsername());
        if(user == null || user.getStatus().equals("Not activated")) {
             throw new UnauthorizedException("Login failed: User not found or not activated");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            if(authentication.isAuthenticated()){
                final String jwt = jwtService.generateToken(user);

                jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", jwt);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(24 * 60 * 60);
                response.addCookie(cookie);

                return ResponseEntity.ok(Map.of("message", "Login successful"));
            }
        } catch (AuthenticationException e){
            throw new UnauthorizedException("Login failed: Invalid credentials");
        }
        throw new UnauthorizedException("Authenticated failed");
    }
    @GetMapping("/detail")
    public ResponseEntity<?> accountDetail(java.security.Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        String username = principal.getName();
        var user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        var result = new java.util.HashMap<String, Object>();
        result.put("avatar", user.getAvatar());
        result.put("username", user.getUsername());
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhoneNumber());
        result.put("address", user.getAddress());
        result.put("gender", user.getGender());
        result.put("birth", user.getDob());
        result.put("role", user.getRole()); // (nếu cần)

        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Validated @RequestBody User user){
        ResponseEntity<?> response = this.accountService.signUpUser(user);
        return response;
    }
    @GetMapping("/active-account")
    public ResponseEntity<?> activeAccount(@RequestParam String email, @RequestParam String code){
        ResponseEntity<?> response = this.accountService.activeAccount(email, code);
        return response;
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> request,
            java.security.Principal principal) {

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("Thiếu thông tin mật khẩu!");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải từ 6 ký tự trở lên!");
        }
        String username = principal.getName();

        return accountService.changePassword(username, oldPassword, newPassword);
    }
//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(java.security.Principal principal) {
//        if (principal == null) return ResponseEntity.status(401).body("Chưa đăng nhập");
//        return accountService.resetPassword(principal);
//    }
// Bước 1: Yêu cầu gửi mã
    @PostMapping("/forgot-password/request")
    public ResponseEntity<?> requestReset(@RequestParam String email) {
        return accountService.sendResetCode(email);
    }
    // Bước 2: Nhập mã và mật khẩu mới
    @PostMapping("/forgot-password/confirm")
    public ResponseEntity<?> confirmReset(
            @RequestParam String email,
            @RequestParam String code,
            @RequestParam String newPassword) {
        return accountService.confirmResetPassword(email, code, newPassword);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out");
    }


}
