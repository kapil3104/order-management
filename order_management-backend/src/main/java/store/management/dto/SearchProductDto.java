package store.management.dto;

import lombok.Data;

@Data
public class SearchProductDto {
    private long categoryId;
    private long subCategoryId;
    private String productCode;
    private String productName;
}
