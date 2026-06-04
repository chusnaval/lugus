package lugus.dto.groups;

import lombok.Data;

@Data
public class TitleDto {
	private long id;
	private String title;
	private Integer year;
	private String type; // MOVIE | SERIES | EXTERNAL
	private String posterUrl;
	private Integer peliculaId;
	private Integer serieId;
	private String imdbId;
	private boolean owned;
}
