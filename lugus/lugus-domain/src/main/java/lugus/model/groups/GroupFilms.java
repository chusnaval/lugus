package lugus.model.groups;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lugus.model.films.Film;
import lugus.model.imdb.ImdbTitleBasics;

@Entity
@Table(name = "grupos_peliculas")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class GroupFilms {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "grupo_id", nullable = true)
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "peliculas_id", nullable = true)
	private Film film;

	@Column(name="orden", nullable = false)
	private int order;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(nullable = false, name = "imdb_id")
	private ImdbTitleBasics itb;

	/**
	 * We prefer the usual stored title film
	 * but  in another case we choose original title in IMDB
	 * 
	 * @return
	 */
	public String getTitle() {
		String titulo = "";
		if (film != null) {
			titulo = film.getTitle();
		}else {
			titulo = itb.getPrimarytitle();
		}

		return titulo;
	}
	
	public String getYear() {
		String result = "";
		if (film != null) {
			result += film.getYear();
		}else {
			result += itb.getStartyear();
		}

		return result;
	}


}
