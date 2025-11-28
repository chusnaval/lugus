package lugus.model;

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
@Table(name = "series")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Serie {

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

	@Column(nullable = false, name="anyo_inicio")
	private int anyoInicio;
	
	@Column(nullable = false, name="anyo_fin")
	private Integer anyoFin;

	@Column(nullable = false)
	@Convert(converter = GeneroConverter.class)
	private Genero genero;

	@Column(nullable = false)
	private String codigo;

	@Column(nullable = true)
	private String notas;
	
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
	
	@Column
	private boolean completa;
	
	@JsonIgnore
	@OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<SerieFoto> serieFotos = new HashSet<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<Season> seasons = new HashSet<>();
	
	public String getDescLocalizacion() {
		if (localizacion == null) {
			return "";
		}
		return localizacion.getDescripcion();
	}

	public boolean tieneCaratula() {
		return this.serieFotos != null && !serieFotos.isEmpty();
	}

	
	public void calcularCodigo() {
		// Eliminar artículos del título
		String procesado = tituloGest.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las| )\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefijo = procesado.length() >= 3 ? procesado.substring(0, 3).toUpperCase() : procesado.toUpperCase();

		// Obtener la etiqueta del género
		String parteCodigo = genero.getCodigo();

		codigo = parteCodigo + "-" + prefijo + "-" + anyoInicio;

	}
	
	public void addCaratula(SerieFoto pf) {
		this.serieFotos.add(pf);
		pf.setSerie(this);

	}
}
