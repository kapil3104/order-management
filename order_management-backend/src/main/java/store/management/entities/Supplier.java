package store.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Supplier")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue
    private Long supplierId;
    @NotBlank
    private String supplierName;
    @NotBlank
    private String supplierAddress;
    private String email;
    @ManyToMany(mappedBy = "suppliers", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Distributor> distributors = new HashSet<>();

    @Override
    public int hashCode() {
        return supplierId != null ? supplierId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Supplier)) return false;
        Supplier other = (Supplier) o;
        return supplierId != null && supplierId.equals(other.supplierId);
    }
}
