package lugus.dto.groups;

import java.util.List;

import lombok.Data;

@Data
public class GroupDetailedDTO {

	private int id;
	
	private String name;
	
	private String description;
	
	private List<GroupFilmsDTO> movies;
}
