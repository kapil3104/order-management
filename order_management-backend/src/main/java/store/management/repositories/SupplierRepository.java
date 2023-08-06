package store.management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.management.entities.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
