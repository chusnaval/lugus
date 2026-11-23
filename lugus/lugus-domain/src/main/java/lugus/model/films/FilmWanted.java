package lugus.model.films;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmWanted {

    private String title;
    
    private int year;

    private String format;
}
