package store.management.controllers;

import java.security.Principal;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import store.management.annotation.RequireAllPermissions;
import store.management.dto.ProductDto;
import store.management.dto.SearchProductDto;
import store.management.enums.Permission;
import store.management.services.ProductService;
import store.management.util.ResponseEntityBuilder;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/product")
public class ProductController {
	private final ProductService productService;
	@Value("${productsImagePath}")
	private String productsImagePath;

	@RequireAllPermissions({ Permission.MANAGE_PRODUCT })
	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> addProduct(@RequestPart(name = "file") MultipartFile multipartFile,
														  @RequestPart ProductDto productDto, Principal principal) {
		try {
			return ResponseEntityBuilder.okResponseEntity(productService.addProduct(productDto, multipartFile,
					principal.getName()));
		} catch (Exception ex) {
			log.info("Exception occurred while adding product : ", ex);
			return ResponseEntityBuilder.errorResponseEntity(ex);
		}
	}
	
	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>> searchProducts(@RequestBody SearchProductDto searchProductDto, Principal principal){
		return ResponseEntityBuilder.okResponseEntity(productService.searchProducts(searchProductDto, principal.getName()));
	}

	@RequireAllPermissions({ Permission.UPDATE_QUANTITIES})
	@GetMapping("/update-quantity")
	public ResponseEntity<Map<String, Object>> updateQuantity(@RequestParam long productId,@RequestParam int quantity,
								 Principal principal){
		return ResponseEntityBuilder.okResponseEntity(productService.updateQuantity(productId, quantity, principal.getName()));
	}

	@GetMapping("/")
	public ResponseEntity<Map<String, Object>> fetchAll(){
		return ResponseEntityBuilder.okResponseEntity(productService.fetchAllProducts());
	}
}
