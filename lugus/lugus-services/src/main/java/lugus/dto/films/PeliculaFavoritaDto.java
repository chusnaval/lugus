package lugus.dto.films;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PeliculaFavoritaDto {
    private int id;
    private String titulo;
    private Integer anyo;
    private String formatoCodigo;
    private String formato;
    private String generoCodigo;
    private boolean favorita;
    private boolean tieneCaratula;
    private String notas;
    private String ratingFormatted;
    private String location;
    private boolean comprado;
    private boolean funda;

}
