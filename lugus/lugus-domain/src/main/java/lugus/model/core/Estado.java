package lugus.model.core;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.model.films.Pelicula;


@Entity
@Table(name = "estados")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
	
	@Id
	@NotBlank
	private int id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@OrderBy("id ASC")
	@OneToMany(mappedBy = "estado")
	@ToString.Exclude
	private Set<Pelicula> peliculas = new HashSet<>();
}
