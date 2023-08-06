package store.management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.management.entities.DistributorProduct;

@Repository
public interface DistributorProductRepository extends JpaRepository<DistributorProduct, Long> {
    DistributorProduct findByDistributorIdAndProductId(long distributorId, long productId);
}
