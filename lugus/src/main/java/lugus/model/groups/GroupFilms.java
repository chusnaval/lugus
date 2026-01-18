package lugus.model.groups;

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
import lugus.model.films.Pelicula;
import lugus.model.imdb.ImdbTitleBasics;

@Entity
@Table(name = "grupos_peliculas")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class GroupFilms {

	@Id
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "grupo_id", nullable = true)
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "peliculas_id", nullable = true)
	private Pelicula pelicula;

	@Column(nullable = false)
	private int orden;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(nullable = false, name = "imdb_id")
	private ImdbTitleBasics itb;

	public String getTitle() {
		String titulo = "";
		if (pelicula != null) {
			titulo = pelicula.getTitulo();
		}else {
			titulo = itb.getPrimarytitle();
		}

		return titulo;
	}

}
