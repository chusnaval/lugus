package lugus.dto.films;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {

	private int id;

	private String title;

	private String titleMgmt;

	private String format;

	private int year;

	private String genreCode;

	private String genreDesc;

	private String mgmtCode;

	private String notes;

	private boolean pack;

	private boolean steelbook;

	private boolean slipcover;

	private boolean owned;

	private boolean watched;

	private String imdbId;

	private Double rating;

	private Integer votes;

	private String situation;

	// Relaciones que NO están @JsonIgnore
	private ConditionDto condition;
	private FilmDto father;

	private DirectorDTO[] director;

	private CastDto[] casting;

	private String coverSrc;

	private String synopsis;

	private String imdbUrl;

	private String faUrl;

	private String lastSeen;

	private String location;

	private GroupDto group;

	private String country;
}
