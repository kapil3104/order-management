package store.management.services;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import store.management.entities.Category;
import store.management.entities.SubCategory;
import store.management.exception.ValidationException;
import store.management.repositories.CategoryRepository;
import store.management.repositories.SubCategoryRepository;
@Service
@RequiredArgsConstructor
@Slf4j
public class SubCategoryService {
	private final SubCategoryRepository subCategoryRepository;
	private final CategoryRepository categoryRepository;
	
	public SubCategory addSubCategory(SubCategory subCategory) throws Exception {
		Category category = categoryRepository.findById(subCategory.getCategoryId()).get();
		List<SubCategory> subCategoryList = subCategoryRepository.findByCategoryAndSubCategoryName(
				category, subCategory.getSubCategoryName());
		if(subCategoryList.size() == 0) {
			subCategory.setCategory(category);
			return subCategoryRepository.save(subCategory);
		}else {
			throw new ValidationException("Sub Category name already exists");
		}
	}

	public List<SubCategory> getSubCategoryByCategoryId(String categoryId) {
		if(StringUtils.isEmpty(categoryId) || categoryId.equalsIgnoreCase("All")) {
			return subCategoryRepository.findAll();
		} else {
			Category category = categoryRepository.findById(Long.parseLong(categoryId)).get();
			return subCategoryRepository.findByCategory(category);
		}
	}

	public List<SubCategory> getAllSubCategories() {
		return subCategoryRepository.findAll();
	}
}
