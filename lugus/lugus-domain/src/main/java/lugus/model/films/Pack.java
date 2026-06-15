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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lugus.converter.FormatoConverter;
import lugus.model.core.Estado;
import lugus.model.core.Location;
import lugus.model.values.Formato;

@Entity
@Table(name = "packs")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Pack {

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
	private String codigo;

	@Column(nullable = true)
	private String notas;

	@JsonIgnore
	@OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("anyo ASC")
	private final Set<Pelicula> peliculasPack = new HashSet<>();

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



}
