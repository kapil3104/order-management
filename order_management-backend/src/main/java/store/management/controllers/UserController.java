package store.management.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.management.annotation.RequireAllPermissions;
import store.management.dto.UserDto;
import store.management.enums.Permission;
import store.management.services.JwtUserDetailsService;
import store.management.util.ResponseEntityBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtUserDetailsService userDetailsService;

    @RequireAllPermissions({ Permission.MANAGE_USERS })
    @PostMapping("/add-user")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserDto userDto) {
        try {
            return ResponseEntityBuilder.okResponseEntity(userDetailsService.addUser(userDto));
        } catch (Exception ex) {
            return ResponseEntityBuilder.errorResponseEntity(ex, HttpStatus.PRECONDITION_FAILED);
        }
    }

    @RequireAllPermissions({ Permission.MANAGE_USERS })
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> fetchAllUsers() {
        return ResponseEntityBuilder.okResponseEntity(userDetailsService.fetchAllUsers());
    }

    @RequireAllPermissions({ Permission.MANAGE_USERS })
    @GetMapping("/users/{roleName}")
    public ResponseEntity<Map<String, Object>> fetchAllUsersByRoleName(@PathVariable String roleName) {
        return ResponseEntityBuilder.okResponseEntity(userDetailsService.fetchAllUsersByRoleName(roleName));
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> fetchUserById(@RequestParam("email") String email) {
        return ResponseEntityBuilder.okResponseEntity(userDetailsService.fetchUserById(email));
    }
}
