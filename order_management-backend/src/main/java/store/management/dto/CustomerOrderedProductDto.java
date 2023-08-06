package store.management.dto;

import lombok.Data;
import store.management.entities.CustomerOrder;
import store.management.entities.Product;

@Data
public class CustomerOrderedProductDto {
    private long customerOrderedProductId;
    private long customerOrderId;
    private long productId;
    private int quantity;
    private double price;
    private String orderDate;
    private String status;
    private String email;
    private CustomerOrderDto customerOrder;
    private ProductDto product;
}
