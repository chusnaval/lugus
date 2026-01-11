package lugus.model.series;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
import lugus.model.converter.LangVersionConverter;
import lugus.model.values.LangVersion;

@Entity
@Table(name = "seasons")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Season {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "serie_id", nullable = true) // FK → localizaciones.codigo
	private Serie serie;
	
	@Column
	private String desc;
	
	@Column
	private int year;
	
	@Column
	private int order;
	
	@Column
	private boolean purchased;
	
	@Column
	private boolean wanted;

	@Column
	@Convert(converter = LangVersionConverter.class)
	private LangVersion publishedVersion;
	
	@Column
	@Convert(converter = LangVersionConverter.class)
	private LangVersion purchasedVersion;
}
