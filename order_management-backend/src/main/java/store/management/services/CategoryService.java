package store.management.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.management.exception.ValidationException;
import store.management.entities.Category;
import store.management.repositories.CategoryRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
	private final CategoryRepository categoryRepository;
	
	public Category addCategory(Category category) throws ValidationException {
		boolean exists = categoryRepository.existsByCategoryName(category.getCategoryName());
		if(!exists) {
			return categoryRepository.save(category);
		} else {
			throw new ValidationException("Category name already exists");
		}
	}

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
}	
