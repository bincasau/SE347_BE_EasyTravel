package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import SE347.EasyTravel.entity.User;

import java.util.List;

@Service
public class UserService implements UserInterfaceService{

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public User findByUsername(String username){
        return this.userRepo.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return this.userRepo.findByEmail(email);
    }

    @Override
    public Page<User> findByRole(String role, Pageable pageable) {
        return this.userRepo.findByRole(role, pageable);
    }

    @Override
    public Page<User> findByStatus(String status, Pageable pageable) {
        return this.userRepo.findByStatus(status, pageable);
    }

    @Override
    public Page<User> findByRoleAndStatus(String role, String status, Pageable pageable) {
        return this.userRepo.findByRoleAndStatus(role, status, pageable);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Account is not found!");
        }

        String authority = user.getRole();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(authority))
        );
    }

    /**
     * Find user by username, throws ResourceNotFoundException if not found
     * This is an example of using custom exceptions
     */
    public User findUserByUsernameOrThrow(String username) {
        User user = this.userRepo.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User " + username + " not found");
        }
        return user;
    }

}
