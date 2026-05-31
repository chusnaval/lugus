package lugus.dto.groups;

import lombok.Data;

@Data
public class GroupFilmsDTO {

	private int id;
	
	private String title;
	
	private int orden;
	
	private String status;
	
	private int film;
	
	private int year;
	
	private String cover;
	
	private String imdbId;
}
