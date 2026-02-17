package lugus.mapper.groups;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.groups.GroupFilmsDTO;
import lugus.model.groups.GroupFilms;

@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GroupFilmsMapper {


	public GroupFilmsDTO mapToDTO(GroupFilms gp) {
		GroupFilmsDTO dto = new GroupFilmsDTO();
		dto.setId(gp.getId());
		dto.setOrden(gp.getOrden());
		dto.setTitle(gp.getTitle());
		dto.setInCollection(gp.getPelicula()!=null);
		dto.setFilm(gp.getPelicula()!=null?gp.getPelicula().getId():-1);
		return dto;
		
	}

}
