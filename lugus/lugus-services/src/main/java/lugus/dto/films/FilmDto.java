package lugus.dto.films;

import java.util.ArrayList;
import java.util.List;

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

	private int year;

	private String genreCode;

	private String genreDesc;

	private String imdbId;

	private Double rating;

	private Integer votes;

	private List<DirectorDTO> director = new ArrayList<>();

	private List<CastDto> casting = new ArrayList<>();
	
	private List<EditionDto> editions  = new ArrayList<>();

	private String coverSrc;

	private String synopsis;

	private String imdbUrl;

	private String lastSeen;
	
	private Double lbRating;
	
	private GroupDto group;

	private String country;

	private String trailerUrl;
	
	private boolean mine;
	
	private boolean favorite;
	
	private int duration;
	
	private boolean watched;
	
	public void addDirector(DirectorDTO directorDTO) {
		if (director == null) {
			director = new ArrayList<>();
		}
		director.add(directorDTO);
	}
	
	public void addEdition(EditionDto editionDTO) {
		if(editions == null) {
			editions = new ArrayList<>();
		}
		this.editions.add(editionDTO);
	}

	public void addCast(CastDto castDto) {
		if (casting == null) {
			casting = new ArrayList<>();
		}
		casting.add(castDto);
	}
}
