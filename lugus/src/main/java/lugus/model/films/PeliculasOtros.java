package lugus.model.films;

import java.text.NumberFormat;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "peliculas_otros")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PeliculasOtros {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "pelicula_id", nullable = true) // FK → locations.code
	private Pelicula pelicula;

	@Column(name = "idmb_id")
	private String imdbId;

	@Column
	private Double rating;

	@Column
	private Integer votes;
	
	@Column(name="lbrating")
	private Float lbRating;
	
	@Column(name="vista")
	private Boolean vista;

	public String getRatingFormatted() {
		if (rating == null) {
			return "";
		} else {
			NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
			nf.setMinimumFractionDigits(1);
			return nf.format(rating);
		}
	}
}
