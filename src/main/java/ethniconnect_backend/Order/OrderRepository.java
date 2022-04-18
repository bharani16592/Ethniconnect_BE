package ethniconnect_backend.Order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Orders, Integer> {

    Optional<Orders> findByOrderid(int id);
    List<Orders> findAllByCustLoginid(long loginid);
}
