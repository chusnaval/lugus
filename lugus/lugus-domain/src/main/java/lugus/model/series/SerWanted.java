package lugus.model.series;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SerWanted {

    private String title;

    private int startYear;
    
    private String seasonDesc;
}
