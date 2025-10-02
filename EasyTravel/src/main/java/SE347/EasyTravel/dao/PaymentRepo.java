package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "payment")
public interface PaymentRepo extends JpaRepository<Payment, Integer> {
}
