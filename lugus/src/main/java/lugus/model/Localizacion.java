package lugus.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "localizaciones")
@Data 
@EqualsAndHashCode(of = "codigo")
@NoArgsConstructor
@AllArgsConstructor
public class Localizacion {

	@Id
	@NotBlank  
	private String codigo;
	
	@NotBlank  
	private String descripcion;

	
	@OneToMany(mappedBy = "localizacion")
	@ToString.Exclude
	private Set<Pelicula> peliculas = new HashSet<>();
}
