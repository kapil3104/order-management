package store.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.management.entities.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByCategoryName(String categoryName);
}
