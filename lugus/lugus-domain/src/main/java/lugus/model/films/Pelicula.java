package lugus.model.films;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.converter.FormatoConverter;
import lugus.converter.GeneroConverter;
import lugus.model.core.Estado;
import lugus.model.core.Location;
import lugus.model.groups.GroupFilms;
import lugus.model.orders.Order;
import lugus.model.people.Actor;
import lugus.model.people.Director;
import lugus.model.people.PeliculasPersonal;
import lugus.model.values.Formato;
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
	@Convert(converter = FormatoConverter.class)
	private Formato formato;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "localizacion_codigo", nullable = true) // FK → location.code
	private Location location;

	@Column(nullable = false)
	private int anyo;

	@Column(nullable = false)
	@Convert(converter = GeneroConverter.class)
	private Genero genero;

	@Column(nullable = false)
	private String codigo;

	@Column(nullable = true)
	private String notas;

	@JsonIgnore
	@OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("anyo ASC")
	private final Set<Pelicula> peliculasPack = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<PeliculasPersonal> peliculasPersonal = new HashSet<>();

	@Column
	private boolean pack;

	@Column
	private boolean steelbook;

	@Column
	private boolean funda;

	@Column
	private boolean comprado;

	@Column(name = "usr_alta")
	private String usrAlta;

	@Column(name = "ts_alta", nullable = false, columnDefinition = "TIMESTAMP")
	private Instant tsAlta;

	@Column(name = "usr_modif")
	private String usrModif;

	@Column(name = "ts_modif", columnDefinition = "TIMESTAMP")
	private Instant tsModif;

	@Column(name = "ts_baja", columnDefinition = "TIMESTAMP")
	private Instant tsBaja;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "estado_id", nullable = true) // FK → estado.id
	private Estado estado;

	@ManyToOne
	@JoinColumn(name = "padre_id")
	@ToString.Exclude
	private Pelicula padre;

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<PeliculaFoto> peliculaFotos = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Director> directores = new HashSet<>();

	@JsonIgnore
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

	@Column(name = "imdb_id")
	private String imdbId;

	@Column
	private Double rating;

	@Column
	private Integer votes;

	private transient String situacion;

	public String getDescLocation() {
		if (location == null) {
			return "";
		}
		return location.getDescripcion();
	}

	public boolean tieneCaratula() {
		return !peliculaFotos.isEmpty();
	}

	public void calcularCodigo() {
		// Eliminar artículos del título
		String procesado = tituloGest.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las| )\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefijo = procesado.length() >= 3 ? procesado.substring(0, 3).toUpperCase() : procesado.toUpperCase();

		// Obtener la etiqueta del género
		String parteCodigo = genero.getCodigo();

		codigo = parteCodigo + "-" + prefijo + "-" + anyo;

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

	/**
	 * Vincula las peliculas
	 * 
	 * @param hijo
	 */
	public void addHijo(Pelicula hijo) {
		this.peliculasPack.add(hijo);
		hijo.setPadre(this);

	}

	public void addCaratula(PeliculaFoto pf) {
		this.peliculaFotos.add(pf);
		pf.setPelicula(this);

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

	public void removeHijo(Pelicula hijo) {
		this.peliculasPack.remove(hijo);
		hijo.setPadre(null);
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
}
