package store.management.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import java.util.Set;

import static java.util.Objects.isNull;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
    @GeneratedValue
    private Long userId;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE}, optional = true)
    @JoinColumn(name = "distributor_id")
    private Distributor distributor;

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return userId != null && userId.equals(other.userId);
    }

    public void addRole(Role role) {
        if(isNull(this.roles)) {
            this.roles = Sets.newHashSet();
        }
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        if(CollectionUtils.isNotEmpty(this.roles) && this.roles.contains(role)) {
            this.roles.remove(role);
            role.getUsers().remove(this);
        }
    }
}