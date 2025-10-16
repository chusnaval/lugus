package lugus;

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
@Table(name = "generos")
@Data 
@EqualsAndHashCode(of = "codigo")
@NoArgsConstructor
@AllArgsConstructor
public class Genero {

	@Id
	@NotBlank  
	private String codigo;
	
	@NotBlank  
	private String descripcion;
	
	@OneToMany(mappedBy = "genero")
	@ToString.Exclude
	private Set<Pelicula> peliculas = new HashSet<>();
}
