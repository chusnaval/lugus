package lugus.model.core;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.model.films.Edition;

@Entity
@Table(name = "localizaciones")
@Data
@EqualsAndHashCode(of = "code")
@NoArgsConstructor
@AllArgsConstructor
public class Location {

	@Id
	@NotBlank
	@Column(name = "codigo")
	private String code;

	@NotBlank
	@Column(name = "descripcion")
	private String description;

	@OrderBy("codigo ASC")
	@OneToMany(mappedBy = "location")
	@ToString.Exclude
	private Set<Edition> editions = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "ubicacion_tipo_cod")
	private LocationType locationType;


}
