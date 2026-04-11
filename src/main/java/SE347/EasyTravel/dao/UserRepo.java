package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "users")
public interface UserRepo extends JpaRepository<User, Integer> {

    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);

    public User findByUsername(String username);
    public User findByEmail(String email);

    Page<User> findByRole(String role, Pageable pageable);
    Page<User> findByStatus(String status, Pageable pageable);
    Page<User> findByRoleAndStatus(String role, String status, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.hotelBookings b WHERE b.bookingId = :bookingId")
    Optional<User> findUserByBookingId(@Param("bookingId") int bookingId);

    Page<User> findByUsernameContainingOrNameContaining(String username, String name, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "(u.username LIKE %:keyword% OR u.name LIKE %:keyword%) ")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    long countByRole(String role);
}
