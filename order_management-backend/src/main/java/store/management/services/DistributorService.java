package store.management.services;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import store.management.dto.DistributorDto;
import store.management.entities.Distributor;
import store.management.entities.Supplier;
import store.management.repositories.DistributorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistributorService {
    private final DistributorRepository distributorRepository;
    private final SupplierService supplierService;

    public Distributor addDistributor(DistributorDto distributorDto) {
        Distributor distributor = distributorDto.toDistributor();
        if(CollectionUtils.isNotEmpty(distributorDto.getSupplierIds())) {
            List<Supplier> suppliers = supplierService.fetchAllByIds(distributorDto.getSupplierIds());
            distributor.setSuppliers(Sets.newHashSet(suppliers));
        }
        return distributorRepository.save(distributor);
    }

    public List<Distributor> fetchAllDistributors() {
        return distributorRepository.findAll();
    }

    public Distributor fetchById(Long id) {
        return distributorRepository.findById(id).get();
    }
}
