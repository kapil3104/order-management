package store.management.dto;

import lombok.Data;
import store.management.entities.Product;

@Data
public class ProductDto {
    private Long productId;
    private String productName;
    private String productCode;
    private double price;
    private int availableQuantity;
    private String description;
    private String pictureName;
    private long subCategoryId;
    private long categoryId;
    private String categoryName;
    private String subCategoryName;

    public Product toProduct() {
        Product product = new Product();
        product.setProductName(this.productName);
        product.setProductCode(this.productCode);
        product.setPrice(this.price);
        product.setDescription(this.description);
        return product;
    }
}
