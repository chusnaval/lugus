package lugus.dto.series;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeasonDto {

	private String desc;
	
	private int ordinal;
	
	private boolean purchased;
	
	private boolean wanted;
}
