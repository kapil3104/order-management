package store.management.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.management.annotation.RequireAllPermissions;
import store.management.dto.DistributorDto;
import store.management.enums.Permission;
import store.management.services.DistributorService;
import store.management.util.ResponseEntityBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/distributor")
public class DistributorController {
    private final DistributorService distributorService;

    @RequireAllPermissions({ Permission.MANAGE_DISTRIBUTOR})
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> addDistributor(@RequestBody DistributorDto distributorDto){
        return ResponseEntityBuilder.okResponseEntity(distributorService.addDistributor(distributorDto));
    }

    @RequireAllPermissions({ Permission.MANAGE_DISTRIBUTOR})
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> fetchAllDistributors(){
        return ResponseEntityBuilder.okResponseEntity(distributorService.fetchAllDistributors());
    }

    @RequireAllPermissions({ Permission.MANAGE_DISTRIBUTOR})
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> fetchById(@PathVariable Long id){
        return ResponseEntityBuilder.okResponseEntity(distributorService.fetchById(id));
    }
}
