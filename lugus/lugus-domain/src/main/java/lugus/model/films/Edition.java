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
import lugus.converter.FormatConverter;
import lugus.model.core.Status;
import lugus.model.core.Location;
import lugus.model.values.Format;

@Entity
@Table(name = "peliculas_edicion")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Edition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "pelicula_id", nullable = true) // FK → estado.id
	private Film film;

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

	@ManyToOne
	@JoinColumn(name = "padre_id")
	@ToString.Exclude
	private Pack pack;

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
	private String usrModif;

	@Column(name = "ts_modif", columnDefinition = "TIMESTAMP")
	private Instant tsModif;

	@Column(name = "ts_baja", columnDefinition = "TIMESTAMP")
	private Instant tsDeletion;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "estado_id", nullable = true) // FK → estado.id
	private Status status;

	/**
	 * Obtain initial code from the film
	 * @return
	 */
	public String obtainInitialCode() {
		if(film == null){
			return "";
		}
		return film.obtainCode();
	}
}
