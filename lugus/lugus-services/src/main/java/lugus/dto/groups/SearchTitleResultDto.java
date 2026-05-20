package lugus.dto.groups;

import lombok.Data;

@Data
public class SearchTitleResultDto {
    private Long titleId;      // si existe en Titles
    private String source;     // INTERNAL | MOVIE | SERIES | IMDB
    private String title;
    private Integer year;
    private String type;       // MOVIE | SERIES | EXTERNAL
    private String posterUrl;
    private String imdbId;     // tconst
}
