package lugus.mapper.groups;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.groups.GroupDTO;
import lugus.model.groups.Group;
import lugus.model.groups.GroupFilms;


@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GroupMapper {

	public GroupDTO mapToDTO(Group group) {
		GroupDTO dto = new GroupDTO();
		dto.setId(group.getId());
		dto.setName(group.getName());
		dto.setMovieCount(group.getFilms().size());
		String cover = "./covers/placeholder.png";
		Optional<GroupFilms> gf = group.getFilms().stream().findFirst();
		if(gf.isPresent() && gf.get().getPelicula()!=null) {
			cover = gf.get().getPelicula().getCoverUrl();
		}
		dto.setCover(cover);
		return dto;
		
	}

	public Group mapToEntity(GroupDTO dto) {
		Group group = new Group();
		group.setId(group.getId());
		group.setName(group.getName());
		return group;
	}
}
