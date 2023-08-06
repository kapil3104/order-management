package store.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.management.entities.Category;
import store.management.entities.Product;
import store.management.entities.SubCategory;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

	Product findByProductCode(String productCode);

}
