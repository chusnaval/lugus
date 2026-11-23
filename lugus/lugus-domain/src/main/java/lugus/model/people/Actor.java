package lugus.model.people;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lugus.model.series.Series;
import lugus.model.values.TitleType;

@Entity
@Table(name = "actores")
@Data 
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

	@Id
	private int id;
	
    // Tipo de título: MOVIE, SERIES
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TitleType type;
    
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "pelicula_id", nullable = true) // FK → movie.id
	private Film film;
	
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "serie_id", nullable = true)
    private Series series;
    
	@Column(name="persona_id")
	private int people;
	
	@Column(name="nombre")
	private String name;
	
	@Column(name="orden")
	private int order;
	
	@Column(name="personaje")
	private String character;
	
	
}
