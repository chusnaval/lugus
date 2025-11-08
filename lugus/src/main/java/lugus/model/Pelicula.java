package lugus.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.converter.FormatoConverter;
import lugus.converter.GeneroConverter;

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
	
	@Column(nullable = false, name="titulo_gest")
	private String tituloGest;

	@Column(nullable = false)
	@Convert(converter = FormatoConverter.class)
	private Formato formato;

	/* ---------- 3. Many‑to‑One (Localizacion) ---------- */
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

	@JsonManagedReference
	@OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Pelicula> peliculasPack = new HashSet<>();

	@Column
	private boolean pack;
	
	@Column
	private boolean steelbook;

	@Column
	private boolean funda;

	@Column
	private boolean comprado;

	@ManyToOne
	@JoinColumn(name = "padre_id")
	@ToString.Exclude
	private Pelicula padre;
	
	@OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
	@ToString.Exclude
	private Set<PeliculaFoto> peliculaFotos = new HashSet<>();
	
	public String getDescLocalizacion() {
		if(localizacion==null) {
			return "";
		}
		return localizacion.getDescripcion();
	}

	public boolean tieneCaratula() {
		return this.peliculaFotos!=null && !peliculaFotos.isEmpty();
	}
	
	public void calcularCodigo() {
		// Eliminar artículos del título
		String procesado = titulo.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las)\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefijo = procesado.length() >= 3 ? procesado.substring(0, 3).toUpperCase() : procesado.toUpperCase();

		// Obtener la etiqueta del género
		String parteCodigo = genero.getCodigo();

		codigo = prefijo + "-" + parteCodigo + "-" + anyo;

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
