package store.management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.management.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
