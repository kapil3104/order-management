package store.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import store.management.entities.Category;
import store.management.entities.SubCategory;
@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

	List<SubCategory> findByCategoryAndSubCategoryName(Category category, String subCategoryName);

	List<SubCategory> findByCategory(Category category);

}
