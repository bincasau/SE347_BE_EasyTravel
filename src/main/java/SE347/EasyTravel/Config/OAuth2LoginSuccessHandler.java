package SE347.EasyTravel.Config;

import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        User user = userRepo.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setName(name);
            user.setPassword("$2a$12$zBVwuSN0PKkX4S3CMoVLLehgM20FFOFZTEUCN92Tt9fidEulDiJie");
            user.setRole("CUSTOMER");
            user.setStatus("Activated");
            user.setAvatar("user_default.jpg");
            userRepo.save(user);
        }
        String token = jwtService.generateToken(user);
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/");
    }
}
