package lugus.dto.films;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PeliculaFavoritaDto {
    private int id;
    private String titulo;
    private Integer anyo;
    private String formatoCodigo;
    private String generoCodigo;
    private boolean favorita;
    // Puedes añadir más campos según lo que muestres en la vista
}
