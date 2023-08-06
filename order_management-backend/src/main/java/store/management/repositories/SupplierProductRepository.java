package store.management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.management.entities.SupplierProduct;

import java.util.List;

@Repository
public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {
    List<SupplierProduct> findByProductId(long productId);
    List<SupplierProduct> findBySupplierIdAndProductId(long supplierId, long productId);
    List<SupplierProduct> findBySupplierId(long supplierId);
}
