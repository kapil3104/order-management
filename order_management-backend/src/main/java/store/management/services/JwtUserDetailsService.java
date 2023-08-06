package store.management.services;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import store.management.dto.RoleDto;
import store.management.entities.Distributor;
import store.management.entities.Role;
import store.management.enums.Permission;
import store.management.exception.ValidationException;
import store.management.entities.User;
import store.management.dto.UserDto;
import store.management.repositories.DistributorRepository;
import store.management.repositories.RoleRepository;
import store.management.repositories.UserRepository;
import store.management.security.CustomUserDetails;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder bcryptEncoder;
	@Autowired
	private DistributorRepository distributorRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Value("${admin.username}")
	private String adminUsername;
	@Value("${admin.password}")
	private String adminPassword;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (isNull(user)) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
		return new CustomUserDetails(user);
	}

	public String fetchRole(String username) {
		User user = userRepository.findByUsername(username);
		if(nonNull(user)) {
			for(Role role : user.getRoles()) {
				return role.getRoleName();
			}
		}
		return null;
	}

	public User registerAdminUser() throws Exception {
		User adminUser = userRepository.findByUsername(adminUsername);
		if (isNull(adminUser)) {
			adminUser = new User();
			adminUser.setUsername(adminUsername);
			adminUser.setPassword(bcryptEncoder.encode(adminPassword));
			adminUser.setName("Admin");
			adminUser.setPhone("9876543210");
			adminUser.setAddress("Oklahoma");
			Role role = createRoleWithAllPermissions();
			adminUser.addRole(role);
			roleRepository.saveAndFlush(role);
			return userRepository.saveAndFlush(adminUser);
		} else {
			throw new ValidationException("Admin user already exists");
		}
	}

	public User addUser(UserDto userDto) throws Exception {
		User user = userRepository.findByUsername(userDto.getEmail());
		if(isNull(user)) {
			user = new User();
			BeanUtils.copyProperties(userDto, user);
			user.setUsername(userDto.getEmail());
			user.setPassword(bcryptEncoder.encode(userDto.getPassword()));
			if(userDto.getDistributorId() != 0) {
				Distributor distributor = distributorRepository.findById(userDto.getDistributorId()).get();
				user.setDistributor(distributor);
			}
			if(userDto.getRoleId() != 0) {
				Role role = roleRepository.findById(userDto.getRoleId()).get();
				user.addRole(role);
				roleRepository.save(role);
			}
			return userRepository.save(user);
		} else {
			throw new ValidationException( "Duplicate Email Address");
		}
	}

	public List<UserDto> fetchAllUsers() {
		List<User> allusers = userRepository.findAll();
		return getUserDtoList(allusers);
	}

	public List<UserDto> fetchAllUsersByRoleName(String roleName) {
		List<User> usersByRoleName = userRepository.findByRolesRoleName(roleName);
		return getUserDtoList(usersByRoleName);
	}

	public UserDto fetchUserById(String email) {
		User user = userRepository.findByUsername(email);
		UserDto userDto = new UserDto();
		userDto.setName(user.getName());
		userDto.setPhone(user.getPhone());
		userDto.setEmail(user.getUsername());
		userDto.setAddress(user.getAddress());
		RoleDto roleDto = new RoleDto();
		for(Role role : user.getRoles()) {
			roleDto.setRoleName(role.getRoleName());
			roleDto.setRoleId(role.getRoleId());
		}
		userDto.setRole(roleDto);
		return userDto;
	}

	private Role createRoleWithAllPermissions() {
		Role role = new Role();
		role.setRoleName("Admin_Role");
		for(Permission permission : Permission.values()) {
			role.addPermission(permission);
		}
		roleRepository.save(role);
		return role;
	}

	private static List<UserDto> getUserDtoList(List<User> allusers) {
		List<UserDto> userDtoList = Lists.newArrayList();
		for(User user : allusers) {
			UserDto userDto = new UserDto();
			userDto.setName(user.getName());
			userDto.setPhone(user.getPhone());
			userDto.setEmail(user.getUsername());
			userDto.setAddress(user.getAddress());
			RoleDto roleDto = new RoleDto();
			for(Role role : user.getRoles()) {
				roleDto.setRoleName(role.getRoleName());
				roleDto.setRoleId(role.getRoleId());
			}
			userDto.setRole(roleDto);
			userDtoList.add(userDto);
		}
		return userDtoList;
	}
}