package lugus.model.films;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lugus.converter.FormatoConverter;
import lugus.model.core.Estado;
import lugus.model.core.Location;
import lugus.model.values.Formato;

@Entity
@Table(name = "peliculas_edicion")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Edicion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "pelicula_id", nullable = true) // FK → estado.id
	private Pelicula pelicula;

	@Column(nullable = false)
	@Convert(converter = FormatoConverter.class)
	private Formato formato;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "localizacion_codigo", nullable = true) // FK → location.code
	private Location location;

	@Column(nullable = false)
	private String codigo;

	@Column(nullable = true)
	private String notas;

	@ManyToOne
	@JoinColumn(name = "padre_id")
	@ToString.Exclude
	private Pack pack;

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

	@Column(name = "ts_compra", nullable = true, columnDefinition = "TIMESTAMP")
	private Instant tsCompra;

	@Column(name = "usr_modif")
	private String usrModif;

	@Column(name = "ts_modif", columnDefinition = "TIMESTAMP")
	private Instant tsModif;

	@Column(name = "ts_baja", columnDefinition = "TIMESTAMP")
	private Instant tsBaja;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "estado_id", nullable = true) // FK → estado.id
	private Estado estado;

	
	public void calcularCodigoInicial(String titloGest, String codGenero, int anyo) {
		// Eliminar artículos del título
		String procesado = titloGest.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las| )\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefijo = procesado.length() >= 3 ? procesado.substring(0, 3).toUpperCase() : procesado.toUpperCase();

		codigo = codGenero + "-" + prefijo + "-" + anyo;

	}
}
