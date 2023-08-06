package store.management.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import store.management.dto.ProductDto;

import java.util.List;

@Entity
@Table(name = "Product")
@Data
public class Product {
	@Id
    @GeneratedValue
	private long productId;
	private String productName;
	private String productCode;
	private double price;
	@Lob
	private String description;
	@Lob
	private String pictureName;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name = "categoryId")
	private Category category;
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name = "subCategoryId")
	private SubCategory subCategory;

	@JsonIgnore
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CustomerOrderedProduct> customerOrderedProductList;

	public ProductDto toProductDto() {
		ProductDto productDto = new ProductDto();
		productDto.setProductId(this.productId);
		productDto.setProductName(this.productName);
		productDto.setProductCode(this.productCode);
		productDto.setPrice(this.price);
		productDto.setDescription(this.description);
		productDto.setPictureName(this.pictureName);
		productDto.setCategoryId(this.category.getCategoryId());
		productDto.setSubCategoryId(this.subCategory.getSubCategoryId());
		return productDto;
	}

}
