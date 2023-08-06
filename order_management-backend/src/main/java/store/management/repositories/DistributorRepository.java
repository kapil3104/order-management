package store.management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.management.entities.Distributor;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Long> {

}
