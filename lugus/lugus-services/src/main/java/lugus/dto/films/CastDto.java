package lugus.dto.films;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CastDto {
	
	private int id;
	
	private int order;

	private String name;

	private String character;
}
