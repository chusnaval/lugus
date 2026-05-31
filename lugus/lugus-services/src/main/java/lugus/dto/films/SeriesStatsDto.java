package lugus.dto.films;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeriesStatsDto {
	private long total;
	private int recent;
	private int completeGroups;
	private int incompleteGroups;
}
