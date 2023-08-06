package store.management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.management.entities.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	User findByUsername(String username);
	List<User> findByRolesRoleName(String roleName);
}
