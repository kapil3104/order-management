package store.management.repositories.impl;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import store.management.dto.SearchProductDto;
import store.management.entities.Category;
import store.management.entities.Product;
import store.management.entities.SubCategory;
import store.management.repositories.ProductRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Product> findProduct(SearchProductDto searchProductDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> productRoot = criteriaQuery.from(Product.class);

        List<Predicate> predicates = Lists.newArrayList();
        if(StringUtils.isNotBlank(searchProductDto.getProductCode())) {
            predicates.add(criteriaBuilder.equal(productRoot.get("productCode"), searchProductDto.getProductCode()));
        }
        if(StringUtils.isNotBlank(searchProductDto.getProductName())) {
            predicates.add(criteriaBuilder.like(productRoot.get("productName"),
                    "%" + searchProductDto.getProductName() + "%"));
        }
        if(searchProductDto.getCategoryId() != 0) {
            Join<Product, Category> categoryJoin = productRoot.join("category");
            predicates.add(criteriaBuilder.equal(categoryJoin.get("categoryId"), searchProductDto.getCategoryId()));
        }
        if(searchProductDto.getSubCategoryId() != 0) {
            Join<Product, SubCategory> subCategoryJoin = productRoot.join("subCategory");
            predicates.add(criteriaBuilder.equal(subCategoryJoin.get("subCategoryId"),
                    searchProductDto.getSubCategoryId()));
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
