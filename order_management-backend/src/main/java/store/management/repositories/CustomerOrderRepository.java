package store.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.management.entities.CustomerOrder;
import store.management.entities.User;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

	CustomerOrder findByUserAndStatus(User user, String status);

	List<CustomerOrder> findByUserAndStatusNotIn(User user,
												 List<String> statuses);

	CustomerOrder findByCustomerOrderId(long customerOrderId);

	List<CustomerOrder> findByStatusNotIn(List<String> statuses);

}
