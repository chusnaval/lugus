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
		dto.setOrden(gp.getOrder());
		dto.setTitle(gp.getTitle());
		dto.setStatus(gp.getFilm()!=null? (gp.getFilm().isComprado()?"OWNED":"WISHLIST"):"NONE");
		dto.setFilm(gp.getFilm()!=null?gp.getFilm().getId():-1);
		dto.setYear(gp.getFilm()!=null?gp.getFilm().getYear():Integer.parseInt(gp.getItb().getStartyear()));
		dto.setCover(gp.getFilm()!=null?gp.getFilm().getCoverUrl():"/covers/placeholder.png");
		dto.setImdbId(gp.getFilm()!=null?gp.getFilm().getImdbId():gp.getItb().getTconst());
		return dto;
		
	}

}
