package lugus.dto.groups;

import java.util.List;

import lombok.Data;

@Data
public class GroupDTO {

	private int id;
	
	private String name;
	
	private int movieCount;
	
	private String cover;
	
	private Integer filmaffinityId;
	
	 private List<GroupTitleDto> titles;
}
