package store.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.management.entities.CustomerOrder;
import store.management.entities.CustomerOrderedProduct;
import store.management.entities.Product;
@Repository
public interface CustomerOrderedProductsRepository extends JpaRepository<CustomerOrderedProduct, Long> {

	CustomerOrderedProduct findByCustomerOrderAndProduct(CustomerOrder customerOrder,
														 Product product);

	List<CustomerOrderedProduct> findByCustomerOrder(CustomerOrder customerOrder);

}
