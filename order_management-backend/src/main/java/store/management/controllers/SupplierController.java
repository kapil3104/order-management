package store.management.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.management.annotation.RequireAllPermissions;
import store.management.entities.Supplier;
import store.management.enums.Permission;
import store.management.services.SupplierService;
import store.management.util.ResponseEntityBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplier")
public class SupplierController {
    private final SupplierService supplierService;

    @RequireAllPermissions({ Permission.MANAGE_SUPPLIER})
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> addSupplier(@RequestBody Supplier supplier){
        return ResponseEntityBuilder.okResponseEntity(supplierService.addSupplier(supplier));
    }

    @RequireAllPermissions({ Permission.MANAGE_SUPPLIER})
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> fetchAllSuppliers(){
        return ResponseEntityBuilder.okResponseEntity(supplierService.fetchAllSuppliers());
    }

    @RequireAllPermissions({ Permission.MANAGE_SUPPLIER})
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> fetchById(@PathVariable Long id){
        return ResponseEntityBuilder.okResponseEntity(supplierService.fetchById(id));
    }

    @RequireAllPermissions({ Permission.MANAGE_SUPPLIER})
    @GetMapping("/update-quantity")
    public ResponseEntity<Map<String, Object>> updateSupplierQuantity(@RequestParam Long supplierId,
                                                                      @RequestParam Long productId,
                                                                      @RequestParam int quantity){
        return ResponseEntityBuilder.okResponseEntity(supplierService.updateSupplierQuantity(supplierId, productId, quantity));
    }

    @RequireAllPermissions({ Permission.MANAGE_SUPPLIER})
    @GetMapping("/quantity/{supplierId}")
    public ResponseEntity<Map<String, Object>> fetchQuantityForSupplier(@PathVariable Long supplierId){
        return ResponseEntityBuilder.okResponseEntity(supplierService.fetchSupplierQuantity(supplierId));
    }
}
