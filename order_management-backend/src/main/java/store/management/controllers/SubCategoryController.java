package store.management.controllers;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import store.management.annotation.RequireAllPermissions;
import store.management.entities.SubCategory;
import store.management.enums.Permission;
import store.management.services.SubCategoryService;
import store.management.util.ResponseEntityBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sub-category")
public class SubCategoryController {
	private final SubCategoryService subCategoryService;

	@RequireAllPermissions({ Permission.MANAGE_SUB_CATEGORY })
	@PostMapping("/")
	public ResponseEntity<Map<String, Object>> addSubCategory(@RequestBody @Validated SubCategory subCategory) {
		try {
			return ResponseEntityBuilder.okResponseEntity(subCategoryService.addSubCategory(subCategory));
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex);
		}
	}

	@GetMapping("/")
	public ResponseEntity<Map<String, Object>> getSubCategoryByCategoryId(@RequestParam String categoryId){
		return ResponseEntityBuilder.okResponseEntity(subCategoryService.getSubCategoryByCategoryId(categoryId));
	}
	
	@GetMapping("/all")
	public ResponseEntity<Map<String, Object>> getAllSubCategories(){
		return ResponseEntityBuilder.okResponseEntity(subCategoryService.getAllSubCategories());
	}
}
