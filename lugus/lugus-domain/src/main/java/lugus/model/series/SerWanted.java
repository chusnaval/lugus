package lugus.model.series;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SerWanted {
    private String titulo;
    private int anyoInicio;
    private String seasonDesc;
}
