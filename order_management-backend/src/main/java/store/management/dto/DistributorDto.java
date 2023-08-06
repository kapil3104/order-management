package store.management.dto;

import lombok.Data;
import store.management.entities.Distributor;

import java.util.List;

@Data
public class DistributorDto {
    private String distributorName;
    private String distributorAddress;
    private List<Long> supplierIds;

    public Distributor toDistributor() {
        Distributor distributor = new Distributor();
        distributor.setDistributorName(this.distributorName);
        distributor.setDistributorAddress(this.distributorAddress);
        return distributor;
    }
}
