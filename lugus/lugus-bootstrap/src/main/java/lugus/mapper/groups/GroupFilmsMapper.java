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
		dto.setStatus(gp.getPelicula()!=null? (gp.getPelicula().isComprado()?"OWNED":"WISHLIST"):"NONE");
		dto.setFilm(gp.getPelicula()!=null?gp.getPelicula().getId():-1);
		dto.setYear(gp.getPelicula()!=null?gp.getPelicula().getAnyo():Integer.parseInt(gp.getItb().getStartyear()));
		dto.setCover(gp.getPelicula()!=null?gp.getPelicula().getCoverUrl():"./covers/placeholder.png");
		dto.setImdbId(gp.getPelicula()!=null?gp.getPelicula().getImdbId():gp.getItb().getTconst());
		return dto;
		
	}

}
