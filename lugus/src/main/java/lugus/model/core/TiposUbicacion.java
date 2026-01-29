package lugus.model.core;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
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
@Table(name = "tipos_ubicacion")
@Data 
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class TiposUbicacion {
	
	@Id
	@NotBlank  
	private int id;
	
	@Column
	private String description;
	
	@OneToMany(mappedBy = "tiposUbicacion")
	@ToString.Exclude
	private final Set<Location> locations = new HashSet<>();

	/**
	 * 
	 * @param id
	 */
	public TiposUbicacion(@NotBlank int id) {
		super();
		this.id = id;
	}
	
	

}
