package lugus.model.core;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.model.films.Pelicula;

@Entity
@Table(name = "localizaciones")
@Data
@EqualsAndHashCode(of = "codigo")
@NoArgsConstructor
@AllArgsConstructor
public class Location {

	@Id
	@NotBlank
	private String codigo;

	@NotBlank
	private String descripcion;

	@OneToMany(mappedBy = "location")
	@ToString.Exclude
	private Set<Pelicula> peliculas = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "ubicacion_tipo_cod")
	private LocationType locationType;

	public String countFilms() {
		return "(" + peliculas.size() + ")";
	}
}
