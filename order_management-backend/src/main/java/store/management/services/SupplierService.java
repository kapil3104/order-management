package store.management.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import store.management.entities.Supplier;
import store.management.entities.SupplierProduct;
import store.management.repositories.SupplierProductRepository;
import store.management.repositories.SupplierRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierProductRepository supplierProductRepository;

    public Supplier addSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public List<Supplier> fetchAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier fetchById(Long id) {
        return supplierRepository.findById(id).get();
    }

    public List<Supplier> fetchAllByIds(List<Long> ids) {
        return supplierRepository.findAllById(ids);
    }

    public SupplierProduct updateSupplierQuantity(long supplierId, long productId, int quantity) {
        List<SupplierProduct> supplierProducts = supplierProductRepository.findBySupplierIdAndProductId(supplierId,
                productId);
        SupplierProduct supplierProduct;
        if(CollectionUtils.isEmpty(supplierProducts)) {
            supplierProduct = new SupplierProduct();
            supplierProduct.setSupplierId(supplierId);
            supplierProduct.setProductId(productId);
        } else {
            supplierProduct = supplierProducts.get(0);
        }
        supplierProduct.setQuantity(supplierProduct.getQuantity() + quantity);
        return supplierProductRepository.save(supplierProduct);
    }

    public List<SupplierProduct> fetchSupplierQuantity(long supplierId) {
        return supplierProductRepository.findBySupplierId(supplierId);
    }
}
