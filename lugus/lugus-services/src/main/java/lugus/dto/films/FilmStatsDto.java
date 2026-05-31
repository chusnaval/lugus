package lugus.dto.films;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmStatsDto {
	private long totalFilms;
	private int recentFilms;
	private int completeGroups;
	private int incompleteGroups;
	private int vhs;
	private int dvd;
	private int bluray;
	private int uhd;
	private int digital;
	private int notOwned;
	
    private FilmGenreDto generosPorCategoria;
}
