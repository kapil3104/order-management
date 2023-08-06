package store.management.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DistributorProduct")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributorProduct {
    @Id
    @GeneratedValue
    private long distributorProductId;
    private long distributorId;
    private long productId;
    private int quantity;
}
