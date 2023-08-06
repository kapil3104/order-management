package store.management.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Distributor")
@Data
public class Distributor {
    @Id
    @GeneratedValue
    private Long distributorId;
    private String distributorName;
    private String distributorAddress;
    private String email;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "distributor_supplier",
            joinColumns = @JoinColumn(name = "distributor_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    @JsonIgnore
    private Set<Supplier> suppliers;

    @Override
    public int hashCode() {
        return distributorId != null ? distributorId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Distributor)) return false;
        Distributor other = (Distributor) o;
        return distributorId != null && distributorId.equals(other.distributorId);
    }
}
