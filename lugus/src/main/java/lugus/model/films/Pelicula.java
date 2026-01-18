package lugus.model.films;

import java.time.Instant;
import java.util.HashSet;
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
import jakarta.persistence.OneToOne;
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
import lugus.model.core.Localizacion;
import lugus.model.groups.GroupFilms;
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
	@JoinColumn(name = "localizacion_codigo", nullable = true) // FK → localizaciones.codigo
	private Localizacion localizacion;

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
	private final Set<Director> directores = new HashSet<Director>();

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Actor> actores = new HashSet<Actor>();
	
	@JsonIgnore
	@OneToOne(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private PeliculasOtros otros;

	@JsonIgnore
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<GroupFilms> groups = new HashSet<GroupFilms>();

	public String getDescLocalizacion() {
		if (localizacion == null) {
			return "";
		}
		return localizacion.getDescripcion();
	}

	public boolean tieneCaratula() {
		return this.peliculaFotos != null && !peliculaFotos.isEmpty();
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
}
