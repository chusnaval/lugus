package lugus.model.imdb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imdbDirectorFilm")
@Data
@EqualsAndHashCode(of = "nconst")
@NoArgsConstructor
@AllArgsConstructor
public class ImdbDirectorFilm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String nconst;

	@Column(name="primaryname")
	private String primaryName;

	@Column
	private Integer birthyear;

	@Column
	private Integer deathyear;
	
	@Column
	private String tconst;
	
	@Column
	private String title;
}
