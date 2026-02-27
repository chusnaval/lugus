package lugus.dto.groups;

import lombok.Data;

@Data
public class GroupFilmsDTO {

	private int id;
	
	private String title;
	
	private int orden;
	
	private boolean inCollection;
	
	private int film;
}
