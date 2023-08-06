package store.management.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import store.management.dto.ProductDto;
import store.management.dto.SearchProductDto;
import store.management.entities.*;
import store.management.exception.ValidationException;
import store.management.repositories.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	private final EmailNotificationService emailNotificationService;
	private final ProductRepository productRepository;
	private final SubCategoryRepository subCategoryRepository;
	private final CategoryRepository categoryRepository;
	private final DistributorProductRepository distributorProductRepository;
	private final UserRepository userRepository;
	@Value("${productsImagePath}")
	private String productsImagePath ;
	public Product addProduct(ProductDto productDto, MultipartFile multipartFile, String email) throws Exception {
		File uploadedFile = new File(productsImagePath, multipartFile.getOriginalFilename());
		uploadedFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(uploadedFile);
		fos.write(multipartFile.getBytes());
		fos.close();
		Product existingProduct = productRepository.findByProductCode(productDto.getProductCode());
		if(isNull(existingProduct)) {
			SubCategory subCategory = subCategoryRepository.findById(productDto.getSubCategoryId()).get();
			Category category = categoryRepository.findById(productDto.getCategoryId()).get();
			Product product = productDto.toProduct();
			product.setPictureName(multipartFile.getOriginalFilename());
			product.setCategory(category);
			product.setSubCategory(subCategory);
			productRepository.save(product);
			User user = userRepository.findByUsername(email);
			Distributor distributor = user.getDistributor();
			DistributorProduct distributorProduct =
					distributorProductRepository.findByDistributorIdAndProductId(distributor.getDistributorId()
							, product.getProductId());
			if(isNull(distributorProduct)) {
				distributorProduct =
						DistributorProduct.builder().distributorId(distributor.getDistributorId())
										  .productId(product.getProductId()).build();
			}
			distributorProduct.setQuantity(distributorProduct.getQuantity() + productDto.getAvailableQuantity());
			distributorProductRepository.saveAndFlush(distributorProduct);
			emailNotificationService.sendEmail(distributor.getEmail(), "New Product", "A new product has been added to" +
																					 " the inventory");
			return product;
		} else {
			throw new ValidationException("Product code already exists");
		}
	}
	
	public List<ProductDto> searchProducts(SearchProductDto searchProductDto, String email) {
		List<Product> productList = productRepository.findProduct(searchProductDto);
		List<ProductDto> productDtos = new ArrayList<>();
		User user = userRepository.findByUsername(email);
		Distributor distributor = user.getDistributor();
		for(Product product : productList) {
			try {
				File file=new File(productsImagePath + "/" + product.getPictureName());
			    InputStream in = new FileInputStream(file);
			    product.setPictureName(Base64.getEncoder().encodeToString(IOUtils.toByteArray(in)));
				DistributorProduct distributorProduct =
						distributorProductRepository.findByDistributorIdAndProductId(distributor.getDistributorId()
								, product.getProductId());
				ProductDto productDto = product.toProductDto();
				productDto.setCategoryName(product.getCategory().getCategoryName());
				productDto.setSubCategoryName(product.getSubCategory().getSubCategoryName());
				if(nonNull(distributorProduct)) {
					productDto.setAvailableQuantity(distributorProduct.getQuantity());
				}
				productDtos.add(productDto);
			} catch (Exception e) {
				log.error("Exception Occurred while setting picture name : ", e);
			}
		}
		return productDtos;
	}
	public DistributorProduct updateQuantity(long productId, int quantity, String email) {
		User user = userRepository.findByUsername(email);
		Distributor distributor = user.getDistributor();
		DistributorProduct distributorProduct =
				distributorProductRepository.findByDistributorIdAndProductId(distributor.getDistributorId()
						, productId);
		if(isNull(distributorProduct)) {
			distributorProduct =
					DistributorProduct.builder().distributorId(distributor.getDistributorId())
									  .productId(productId).build();
		}
		distributorProduct.setQuantity(distributorProduct.getQuantity() + quantity);
		distributorProductRepository.saveAndFlush(distributorProduct);
		return distributorProduct;
	}

	public Product findProductById(Long id) {
		return productRepository.findById(id).get();
	}

	public List<Product> fetchAllProducts() {
		return productRepository.findAll();
	}
}
