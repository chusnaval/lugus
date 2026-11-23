package lugus.model.people;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lugus.model.films.Film;

@Entity
@Table(name = "directores")
@Data 
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Director {

	@Id
	private int id;
	
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "pelicula_id", nullable = true) // FK → movie.id
	private Film film;
	
	@Column(name="persona_id")
	private int people;
	
	@Column(name="nombre")
	private String name;
	
	@Column(name="orden")
	private int order;
	
	
}
