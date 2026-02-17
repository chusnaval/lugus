package lugus.dto.groups;

import java.util.List;

import lombok.Data;

@Data
public class GroupDetailedDTO {

	private int id;
	
	private String name;
	
	private List<GroupFilmsDTO> films;
}
