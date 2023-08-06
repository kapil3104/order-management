package store.management.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import store.management.entities.Role;

@Data
@JsonIgnoreProperties
public class UserDto {
	private String name;
	private String email;
	private String phone;
	private String password;
	private String address;
	private long roleId;
	private long distributorId;
	private RoleDto role;
}
