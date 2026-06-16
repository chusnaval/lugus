package lugus.dto.films;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lugus.dto.core.FormatDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {

	private int id;

	private String title;

	private String titleMgmt;

	private FormatDTO format;

	private int year;

	private String genreCode;

	private String genreDesc;

	private String mgmtCode;

	private String notes;

	private boolean steelbook;

	private boolean slipcover;

	private boolean owned;

	private boolean watched;

	private String imdbId;

	private Double rating;

	private Integer votes;

	private String situation;

	private ConditionDto condition;
	
	private PackDto pack;

	private List<DirectorDTO> director = new ArrayList<>();

	private List<CastDto> casting = new ArrayList<>();

	private String coverSrc;

	private String synopsis;

	private String imdbUrl;

	private String lastSeen;

	private String location;

	private GroupDto group;

	private String country;

	private String trailerUrl;
	
	private Instant tsCompra;
	
	private boolean mine;
	
	private boolean favorite;
	
	private int duration;
	
	public void addDirector(DirectorDTO directorDTO) {
		if (director == null) {
			director = new ArrayList<>();
		}
		director.add(directorDTO);
	}

	public void addCast(CastDto castDto) {
		if (casting == null) {
			casting = new ArrayList<>();
		}
		casting.add(castDto);
	}
}
