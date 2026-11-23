package lugus.model.people;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
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
import lugus.model.series.Series;

@Entity
@Table(name = "series_personal")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SeriesStaff {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "serie_id", nullable = true) // FK → movie.id
	private Series series;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "persona_id", nullable = true) // FK → people.id
	private People people;
	
	@Column(name="notas")
	private String notes;

	@Column(name="personaje")
	private String character;

	@Column(name="orden")
	private Integer order;

	@Column(name="trabajo")
	private String job;
}
