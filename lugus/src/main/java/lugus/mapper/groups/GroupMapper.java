package lugus.mapper.groups;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.groups.GroupDTO;
import lugus.model.groups.Group;


@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GroupMapper {

	public GroupDTO mapToDTO(Group group) {
		GroupDTO dto = new GroupDTO();
		dto.setId(group.getId());
		dto.setName(group.getName());
		return dto;
		
	}

	public Group mapToEntity(GroupDTO dto) {
		Group group = new Group();
		group.setId(group.getId());
		group.setName(group.getName());
		return group;
	}
}
