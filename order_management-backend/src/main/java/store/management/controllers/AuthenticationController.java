package store.management.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import store.management.dto.JwtRequest;
import store.management.dto.JwtResponse;
import store.management.entities.User;
import store.management.security.JwtTokenUtil;
import store.management.services.JwtUserDetailsService;
import store.management.util.ResponseEntityBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenUtil jwtTokenUtil;
	private final JwtUserDetailsService userDetailsService;

	@PostMapping("/register-admin")
	public ResponseEntity<Map<String, Object>> registerAdminUser() {
		try {
			return ResponseEntityBuilder.okResponseEntity(userDetailsService.registerAdminUser());
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex, HttpStatus.PRECONDITION_FAILED);
		}
	}

	@PostMapping("/authenticate")
	public ResponseEntity<Map<String, Object>> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
		try {
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
			final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
			final String token = jwtTokenUtil.generateToken(userDetails);
			final String roleName = userDetailsService.fetchRole(authenticationRequest.getUsername());
			return ResponseEntityBuilder.okResponseEntity(new JwtResponse(token, roleName));
		} catch (Exception ex) {
			return ResponseEntityBuilder.errorResponseEntity(ex, HttpStatus.UNAUTHORIZED);
		}
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}