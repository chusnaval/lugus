package lugus.model.films;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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
import lugus.converter.GeneroConverter;
import lugus.model.groups.GroupFilms;
import lugus.model.orders.Order;
import lugus.model.people.Actor;
import lugus.model.people.Director;
import lugus.model.people.PeliculasPersonal;
import lugus.model.values.Genero;

@Entity
@Table(name = "peliculas")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Pelicula {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String titulo;

	@Column(nullable = false, name = "titulo_gest")
	private String tituloGest;

	@Column(nullable = false)
	private int anyo;

	@Column(nullable = false)
	@Convert(converter = GeneroConverter.class)
	private Genero genero;

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<PeliculasPersonal> peliculasPersonal = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<PeliculaFoto> peliculaFotos = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<PeliculasEnlaces> peliculasEnlaces = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Director> directores = new HashSet<>();

	@JsonIgnore
	@OrderBy("orden ASC")
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Actor> actores = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<PeliculasUsuario> peliculasUsuario = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<GroupFilms> groups = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Order> orders = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Edicion> editions = new ArrayList<>();

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
	private Instant ultimaCompra;

	
	// exports
	private transient String situacion;

	/**
	 * Constructor para facilitar la creación de instancias con solo el ID, útil
	 * para asociaciones sin necesidad de cargar toda la entidad.
	 * 
	 * @param peliculaId
	 */
	public Pelicula(Integer peliculaId) {
		this.id = peliculaId;
	}

	public boolean isFavorite(String login) {
		return peliculasUsuario.stream().filter(up -> up.getUsuario().getLogin().equals(login))
				.map(PeliculasUsuario::isFavorita).findFirst().orElse(false);
	}

	public boolean isMine(String login) {
		return peliculasUsuario.stream().filter(up -> up.getUsuario().getLogin().equals(login)).findFirst().isPresent();
	}

	public LocalDateTime getFechaVista(String login) {
		Optional<PeliculasUsuario> aux = peliculasUsuario.stream()
				.filter(up -> up.getUsuario().getLogin().equals(login)).findFirst();
		if (aux.isPresent()) {
			return aux.get().getFechaVista();
		}
		return null;
	}

	public boolean tieneCaratula() {
		return !peliculaFotos.isEmpty();
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

	public void addCaratula(PeliculaFoto pf) {
		this.peliculaFotos.add(pf);
		pf.setPelicula(this);

	}
	
	public void addEdicion(Edicion ed) {
		this.editions.add(ed);
		ed.setPelicula(this);
	}

	public void addDirector(Director d) {
		this.directores.add(d);
		d.setPelicula(this);

	}

	public void addActor(Actor a) {
		this.actores.add(a);
		a.setPelicula(this);

	}

	public void addPeliculasUsuario(PeliculasUsuario pu) {
		this.peliculasUsuario.add(pu);
		pu.setPelicula(this);

	}

	public void addGroup(GroupFilms gf) {
		this.groups.add(gf);
		gf.setPelicula(this);

	}

	public void removeGroup(GroupFilms gf) {
		this.groups.remove(gf);
		gf.setPelicula(null);

	}

	public void removePeliculasUsuario(PeliculasUsuario pu) {
		this.peliculasUsuario.remove(pu);
		pu.setPelicula(null);

	}

	public void removeActor(Actor a) {
		this.actores.remove(a);
		a.setPelicula(null);

	}

	public void removeDirector(Director d) {
		this.directores.remove(d);
		d.setPelicula(null);

	}

	public void removeCaratula(PeliculaFoto pf) {
		this.peliculaFotos.remove(pf);
		pf.setPelicula(null);
	}

	public boolean getUsuarioVista(String usuarioLogin) {
		return peliculasUsuario.stream().filter(pu -> pu.getUsuario().getLogin().equals(usuarioLogin))
				.map(PeliculasUsuario::isVista).findFirst().orElse(false);
	}

	public String getUsuarioRating(String usuarioLogin) {
		return peliculasUsuario.stream().filter(pu -> pu.getUsuario().getLogin().equals(usuarioLogin))
				.map(pu -> pu.getLbRating() != null ? pu.getLbRating().toString() : "").findFirst().orElse("");
	}

	public void calcularSituacion() {

		boolean comprado = this.editions.stream().filter(e -> e.isComprado()).findAny().isPresent();

		if (comprado) {
			this.situacion = "Comprado";
		} else if (tieneOrdenPendiente()) {
			this.situacion = "En pedido";
		} else {
			this.situacion = "No comprado";
		}
	}

	private boolean tieneOrdenPendiente() {
		boolean tieneOrdenPendiente = orders.stream().anyMatch(order -> !order.isRecibido());
		if (tieneOrdenPendiente) {
			return true;
		}
		return false;
	}

	public boolean isComprado() {
		 return this.editions.stream().filter(e -> e.isComprado()).findAny().isPresent();
	}
	
	public String getCoverUrl() {
		String coverUrl = null;
		if (this.peliculaFotos != null && !this.peliculaFotos.isEmpty()) {
			Optional<PeliculaFoto> aux = this.peliculaFotos.stream().findFirst();
			if (aux.isPresent()) {
				PeliculaFoto pf = aux.get();
				if (pf.isCaratula()) {
					coverUrl = pf.getUrl();
				}
			}
		}
		return coverUrl;
	}

	public String getFormatosConjunto() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNotasConjuntas() {
		// TODO Auto-generated method stub
		return null;
	}

	public String calcularCodigo() {
		
		// Eliminar artículos del título
		String procesado = tituloGest.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las| )\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefijo = procesado.length() >= 3 ? procesado.substring(0, 3).toUpperCase() : procesado.toUpperCase();
		
		String codGenero = genero.getCodigo();

		return codGenero + "-" + prefijo + "-" + anyo;

	}
}
