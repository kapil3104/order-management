package store.management.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Entity
@Table(name = "CustomerOrder")
@Data
public class CustomerOrder {
	@Id
    @GeneratedValue
	private long customerOrderId;
	@NotBlank
	private String status;
	@NotBlank
	private String date;
	private String customerName;
	private String customerPhone;
	@Transient
	private long salesPersonId;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE})
	@JoinColumn(name = "user_id")
	private User user;
	@JsonIgnore
	@OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CustomerOrderedProduct> customerOrderedProductList;
}
