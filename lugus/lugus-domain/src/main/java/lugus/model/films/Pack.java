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
import lugus.converter.FormatConverter;
import lugus.model.core.Status;
import lugus.model.core.Location;
import lugus.model.values.Format;

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

	@Column(name="titulo", nullable = false)
	private String title;

	@Column(name = "titulo_gest", nullable = false)
	private String mngmtTitle;

	@Column(name="formato", nullable = false)
	@Convert(converter = FormatConverter.class)
	private Format format;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "localizacion_codigo", nullable = true) // FK → location.code
	private Location location;

	@Column(name="codigo", nullable = false)
	private String code;

	@Column(name="notas", nullable = true)
	private String notes;

	@JsonIgnore
	@OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("anyo ASC")
	private final Set<Edition> editionsPack = new HashSet<>();

	@Column(name="steelbook")
	private boolean steelbook;

	@Column(name="funda")
	private boolean slipCover;

	@Column(name="comprado")
	private boolean purchased;

	@Column(name = "usr_alta")
	private String usrCreation;

	@Column(name = "ts_alta", nullable = false, columnDefinition = "TIMESTAMP")
	private Instant tsCreation;

	@Column(name = "ts_compra", nullable = true, columnDefinition = "TIMESTAMP")
	private Instant tsPurchase;

	@Column(name = "usr_modif")
	private String usrModification;

	@Column(name = "ts_modif", columnDefinition = "TIMESTAMP")
	private Instant tsModification;

	@Column(name = "ts_baja", columnDefinition = "TIMESTAMP")
	private Instant tsDeletion;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "estado_id", nullable = true) // FK → estado.id
	private Status status;



}
