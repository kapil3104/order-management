package store.management.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SupplierProduct")
@Data
public class SupplierProduct {
    @Id
    @GeneratedValue
    private long supplierProductId;
    private long supplierId;
    private long productId;
    private int quantity;
}
