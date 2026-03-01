package lugus.model.films;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmWanted {

    private String titulo;
    private int anyo;
    private String formato;
}
