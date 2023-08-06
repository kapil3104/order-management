package store.management.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "Category")
@Data
public class Category {
	@Id
    @GeneratedValue
	private long categoryId;
	@NotBlank
	private String categoryName;
}
