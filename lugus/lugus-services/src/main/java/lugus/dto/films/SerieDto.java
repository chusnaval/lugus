package lugus.dto.films;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerieDto {

	private int id;
	
	private String title;

	private String titleMgmt;

	private String format;

	private String location;

	private int startYear;
	
	private Integer finishYear;

	private String genreCode;

	private String genreDesc;

	private String mgmtCode;

	private String notes;
	
	private boolean owned;

	private boolean completed;
	
	private String coverSrc;
	
}
