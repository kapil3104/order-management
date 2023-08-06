package store.management.dto;

import lombok.Data;

import java.util.List;

@Data
public class CustomerOrderDto {
    private long customerOrderId;
    private String status;
    private String date;
    private String customerName;
    private String customerPhone;
    private long salesPersonId;
    private UserDto user;
    private List<CustomerOrderedProductDto> customerOrderedProductList;
}
