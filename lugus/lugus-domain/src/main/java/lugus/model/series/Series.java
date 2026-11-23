package lugus.model.series;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
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
import lombok.ToString;
import lugus.converter.FormatConverter;
import lugus.converter.GenreConverter;
import lugus.model.core.Location;
import lugus.model.people.Actor;
import lugus.model.people.SeriesStaff;
import lugus.model.values.Format;
import lugus.model.values.Genre;
import lugus.model.values.LangVersion;

@Entity
@Table(name = "series")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Series {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name="titulo", nullable = false)
	private String title;

	@Column(name = "titulo_gest", nullable = false)
	private String mgmtTitle;

	@Column(name="formato", nullable = false)
	@Convert(converter = FormatConverter.class)
	private Format format;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "localizacion_codigo", nullable = true) // FK → location.code
	private Location location;

	@Column(name="anyo_inicio", nullable = false)
	private int startYear;
	
	@Column(name="anyo_fin", nullable = false)
	private Integer endYear;

	@Column(name="genero", nullable = false)
	@Convert(converter = GenreConverter.class)
	private Genre genre;

	@Column(name="codigo", nullable = false)
	private String code;

	@Column(name="notas", nullable = true)
	private String notes;
	
	@Column(name="comprado")
	private boolean purchased;
	
	@Column(name = "usr_alta")
	private String usrCreation;

	@Column(name = "ts_alta", nullable = false, columnDefinition = "TIMESTAMP")
	private Instant tsCreation;

	@Column(name = "usr_modif")
	private String usrModif;

	@Column(name = "ts_modif", columnDefinition = "TIMESTAMP")
	private Instant tsModif;

	@Column(name = "ts_baja", columnDefinition = "TIMESTAMP")
	private Instant tsDeletion;
	
	@Column(name = "imdb_id")
	private String imdbId;
	
	@Column(name="completa")
	private boolean complete;
	
	@JsonIgnore
	@OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<SeriesImage> images = new HashSet<>();
	
	@JsonIgnore
	@OrderBy("order ASC")
	@OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<Season> seasons = new HashSet<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final Set<SeriesStaff> staff = new HashSet<>();

	@JsonIgnore
	@OneToMany(mappedBy = "series", cascade = CascadeType.ALL)
	@ToString.Exclude
	private final Set<Actor> actores = new HashSet<>();

	
	public String getDescLocation() {
		if (location == null) {
			return "";
		}
		return location.getDescription();
	}

	public boolean hasCover() {
		return this.images != null && !images.isEmpty();
	}

	
	public void obtainCode() {
		// Eliminar artículos del título
		String processed = mgmtTitle.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las| )\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefix = processed.length() >= 3 ? processed.substring(0, 3).toUpperCase() : processed.toUpperCase();

		// Obtener la etiqueta del género
		String genreCode = genre.getCode();

		code = genreCode + "-" + prefix + "-" + startYear;

	}
	
	public void addCover(SeriesImage pf) {
		this.images.add(pf);
		pf.setSeries(this);

	}

	public void createSeasons() {
		if(this.seasons == null || this.seasons.isEmpty()) {
			int index = 1;
			for(int from = startYear; from <= (endYear != null ? endYear-1 : startYear); from++) {
				Season season = new Season();
				season.setOrder(index++);
				season.setYear(from);
				season.setPurchased(false);
				season.setWanted(true);
				season.setPublishedVersion(LangVersion.VE);
				season.setPurchasedVersion(LangVersion.VE);
				season.setSeries(this);
				this.seasons.add(season);
			}
		}
		
	}

	public String getCoverUrl() {
		String coverUrl = null;
		if (this.images != null && !this.images.isEmpty()) {
			Optional<SeriesImage> aux = this.images.stream().findFirst();
			if (aux.isPresent()) {
				SeriesImage pf = aux.get();
				if (pf.isCover()) {
					coverUrl = pf.getUrl();
				}
			}
		}
		return coverUrl;
	}
}
