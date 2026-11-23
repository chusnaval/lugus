package lugus.model.films;

import java.io.Serializable;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.converter.GenreConverter;
import lugus.model.groups.GroupFilms;
import lugus.model.orders.Order;
import lugus.model.people.Actor;
import lugus.model.people.Director;
import lugus.model.people.FilmStaff;
import lugus.model.values.Genre;

@Entity
@Table(name = "peliculas")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Film implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "titulo", nullable = false)
	private String title;

	@Column(name = "titulo_gest", nullable = false)
	private String mgmtTitle;

	@Column(name = "anyo", nullable = false)
	private int year;

	@Column(name = "genero", nullable = false)
	@Convert(converter = GenreConverter.class)
	private Genre genre;

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<FilmStaff> staff = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<FilmImage> images = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<FilmLink> links = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Director> directors = new HashSet<>();

	@JsonIgnore
	@OrderBy("orden ASC")
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Actor> actors = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<FilmUser> userFilms = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<GroupFilms> groups = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Order> orders = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final List<Edition> editions = new ArrayList<>();

	@Column(name = "imdb_id")
	private String imdbId;

	@Column
	private Double rating;

	@Column
	private Integer votes;

	@Column(name = "trailer_url")
	private String trailerUrl;

	@Column(name = "country")
	private String country;

	@Column(name = "synopsis")
	private String synopsis;

	@Column(name = "duration")
	private int duration;

	@Column(name = "synopsis_translated")
	private boolean synopsisTranslated;

	@Formula("(select max(e.ts_compra) from peliculas_edicion e where e.pelicula_id = id)")
	private Instant lastPurchased;

	// exports
	private transient String situation;

	/**
	 * Constructor para facilitar la creación de instancias con solo el ID, útil
	 * para asociaciones sin necesidad de cargar toda la entidad.
	 * 
	 * @param filmId
	 */
	public Film(Integer filmId) {
		this.id = filmId;
	}

	public boolean isFavorite(String login) {
		return userFilms.stream().filter(up -> up.getUser().getLogin().equals(login))
				.map(FilmUser::isFavorite).findFirst().orElse(false);
	}

	public boolean isMine(String login) {
		return userFilms.stream().anyMatch(up -> up.getUser().getLogin().equals(login));
	}

	public LocalDateTime getLastSeenDate(String login) {
		Optional<FilmUser> aux = userFilms.stream()
				.filter(up -> up.getUser().getLogin().equals(login)).findFirst();
		if (aux.isPresent()) {
			return aux.get().getSeenDate();
		}
		return null;
	}

	public boolean hasImages() {
		return !images.isEmpty();
	}

	public String getRatingFormatted() {
		if (rating == null) {
			return "";
		} else {
			NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
			nf.setMinimumFractionDigits(1);
			return nf.format(rating);
		}
	}

	public void addImage(FilmImage pf) {
		this.images.add(pf);
		pf.setFilm(this);

	}

	public void addEdition(Edition ed) {
		this.editions.add(ed);
		ed.setFilm(this);
	}

	public void addDirector(Director d) {
		this.directors.add(d);
		d.setFilm(this);

	}

	public void addActor(Actor a) {
		this.actors.add(a);
		a.setFilm(this);

	}

	public void addUserFilm(FilmUser pu) {
		this.userFilms.add(pu);
		pu.setFilm(this);

	}

	public void addGroup(GroupFilms gf) {
		this.groups.add(gf);
		gf.setFilm(this);

	}

	public void removeGroup(GroupFilms gf) {
		this.groups.remove(gf);
		gf.setFilm(null);

	}

	public void removeUserFilm(FilmUser pu) {
		this.userFilms.remove(pu);
		pu.setFilm(null);

	}

	public void removeActor(Actor a) {
		this.actors.remove(a);
		a.setFilm(null);

	}

	public void removeDirector(Director d) {
		this.directors.remove(d);
		d.setFilm(null);

	}

	public void removeImage(FilmImage pf) {
		this.images.remove(pf);
		pf.setFilm(null);
	}

	public boolean isSeenByUser(String usuarioLogin) {
		return userFilms.stream().filter(pu -> pu.getUser().getLogin().equals(usuarioLogin))
				.map(FilmUser::isSeen).findFirst().orElse(false);
	}

	public String getUserRating(String usuarioLogin) {
		return userFilms.stream().filter(pu -> pu.getUser().getLogin().equals(usuarioLogin))
				.map(pu -> pu.getLbRating() != null ? pu.getLbRating().toString() : "").findFirst().orElse("");
	}

	public void obtainSituation() {

		boolean purchased = this.editions.stream().anyMatch(Edition::isPurchased);

		if (purchased) {
			this.situation = "Comprado";
		} else if (hasAnIncompleteOrder()) {
			this.situation = "En pedido";
		} else {
			this.situation = "No comprado";
		}
	}

	private boolean hasAnIncompleteOrder() {
		return orders.stream().anyMatch(order -> !order.isReceived());
	}

	public boolean isPurchased() {
		return this.editions.stream().anyMatch(Edition::isPurchased);
	}

	public String getCoverUrl() {
		String coverUrl = null;
		if (this.images != null && !this.images.isEmpty()) {
			Optional<FilmImage> aux = this.images.stream().findFirst();
			if (aux.isPresent()) {
				FilmImage pf = aux.get();
				if (pf.isCover()) {
					coverUrl = pf.getUrl();
				}
			}
		}
		return coverUrl;
	}

	public String getFormatosConjunto() {
		StringJoiner joiner = new StringJoiner("|");
		for (Edition edition : this.editions) {
			joiner.add(edition.getFormat().name());
		}
		return joiner.toString();
	}

	public String getNotasConjuntas() {
		StringJoiner joiner = new StringJoiner("|");
		for (Edition edition : this.editions) {
			joiner.add(edition.getNotes());
		}
		return joiner.toString();
	}

	public String obtainCode() {

		// Eliminar artículos del título
		String processed = mgmtTitle.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las| )\\b\\s*", "");

		// reemplazar caracteres acentuados por sus equivalentes sin acento
		processed = processed.replaceAll("[áàäâ]", "a");
		processed = processed.replaceAll("[éèëê]", "e");
		processed = processed.replaceAll("[íìïî]", "i");
		processed = processed.replaceAll("[óòöô]", "o");
		processed = processed.replaceAll("[úùüû]", "u");
		// y en mayúsculas
		processed = processed.replaceAll("[ÁÀÄÂ]", "A");
		processed = processed.replaceAll("[ÉÈËÊ]", "E");
		processed = processed.replaceAll("[ÍÌÏÎ]", "I");
		processed = processed.replaceAll("[ÓÒÖÔ]", "O");
		processed = processed.replaceAll("[ÚÙÜÛ]", "U");

		// reemplazar la ñ por n
		processed = processed.replace("ñ", "n");
		processed = processed.replace("Ñ", "N");

		// eliminar caracteres especiales y espacios
		processed = processed.replaceAll("[^a-zA-Z0-9]", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefix = processed.length() >= 3 ? processed.substring(0, 3).toUpperCase() : processed.toUpperCase();

		String genreCode = genre.getCode();

		return genreCode + "-" + prefix + "-" + year;

	}

	public boolean isDiffInCode(String mgmtTitle, int year, String genreCode) {
		return (!this.getMgmtTitle().equals(mgmtTitle) || this.getYear() != year
				|| !this.getGenre().getCode().equals(genreCode));
	}
}
