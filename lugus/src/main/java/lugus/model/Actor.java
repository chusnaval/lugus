package lugus.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "actores")
@Data 
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

	@Id
	private int id;
	
	@Column(name="pelicula_id")
	private int peliculaId;
	
	@Column(name="persona_id")
	private int persona;
	
	@Column(name="nombre")
	private String nombre;
	
	@Column(name="orden")
	private int orden;
	
	@Column(name="personaje")
	private String personaje;
	
	
}
