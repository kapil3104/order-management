package store.management.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Entity
@Table(name = "CustomerOrderedProducts")
@Data
public class CustomerOrderedProduct {
	@Id
    @GeneratedValue
	private long customerOrderedProductId;
	@Transient
	private long customerOrderId;
	@Transient
	private long productId;
	private int quantity;
	private double price;
	@NotBlank
	private String orderDate;
	@NotBlank
	private String status;
	private String email;
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH,
			CascadeType.MERGE})
	@JoinColumn(name = "customer_order_id")
	private CustomerOrder customerOrder;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE})
	@JoinColumn(name = "productId")
	private Product product;
	
}
