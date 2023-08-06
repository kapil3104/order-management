package store.management.repositories;

import store.management.dto.SearchProductDto;
import store.management.entities.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> findProduct(SearchProductDto searchProductDto);
}
