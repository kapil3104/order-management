package store.management.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.management.annotation.RequireAllPermissions;
import store.management.entities.Category;
import store.management.enums.Permission;
import store.management.exception.ValidationException;
import store.management.services.CategoryService;
import store.management.util.ResponseEntityBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
	private final CategoryService categoryService;

	@RequireAllPermissions({ Permission.MANAGE_CATEGORY })
	@PostMapping("/")
	public ResponseEntity<Map<String, Object>> addCategory(@RequestBody @Validated Category category) {
		try {
			return ResponseEntityBuilder.okResponseEntity(categoryService.addCategory(category));
		} catch (ValidationException ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex, HttpStatus.PRECONDITION_FAILED);
		}
	}
	
	@GetMapping("/all")
	public ResponseEntity<Map<String, Object>> getAllCategories(){
		return ResponseEntityBuilder.okResponseEntity(categoryService.getAllCategories());
	}
}
