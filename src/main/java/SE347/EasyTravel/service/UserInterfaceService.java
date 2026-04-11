package SE347.EasyTravel.service;
import SE347.EasyTravel.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserInterfaceService extends UserDetailsService{
    public User findByUsername(String username);
    public User findByEmail(String email);
    Page<User> findByRole(String role, Pageable pageable);
    Page<User> findByStatus(String status, Pageable pageable);
    Page<User> findByRoleAndStatus(String role, String status, Pageable pageable);
}
