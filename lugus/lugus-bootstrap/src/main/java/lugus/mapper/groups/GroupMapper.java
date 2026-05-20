package lugus.mapper.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.groups.GroupDTO;
import lugus.dto.groups.GroupTitleDto;
import lugus.dto.groups.TitleDto;
import lugus.model.groups.Group;
import lugus.model.groups.GroupTitle;
import lugus.model.titles.Title;


@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class GroupMapper {

	public GroupDTO mapToDTO(Group group) {
		GroupDTO dto = new GroupDTO();
		dto.setId(group.getId());
		dto.setName(group.getName());
		dto.setMovieCount(group.getFilms().size());
		String cover = "./covers/placeholder.png";
		Optional<GroupTitle> gf = group.getFilms().stream().findFirst();
		if(gf.isPresent() && gf.get().getTitle().getPelicula()!=null) {
			cover = gf.get().getTitle().getPelicula().getCoverUrl();
		}
		dto.setCover(cover);
		dto.setTitles(mapChilds(group));
		return dto;
		
	}

	private List<GroupTitleDto> mapChilds(Group group) {
		List<GroupTitleDto> resultado = new ArrayList<>();
		for(GroupTitle gt: group.getFilms()) {
			GroupTitleDto dto = new GroupTitleDto();
			dto.setId(gt.getId());
			dto.setOrden(gt.getOrden());
			dto.setTitle(mapTitle(gt.getTitle()));
			resultado.add(dto);
		}
		return resultado;
	}

	private TitleDto mapTitle(Title title) {
		TitleDto dto = new TitleDto();
		dto.setId(title.getId());
		dto.setTitle(title.getTitle());
		dto.setYear(title.getYear());
		dto.setType(title.getType().name());
		dto.setPosterUrl(title.getPosterUrl());
		if(title.getPelicula()!=null) {
			dto.setPeliculaId(title.getPelicula().getId());
		}
		if(title.getSerie()!=null) {
			dto.setSerieId(title.getSerie().getId());
		}
		if(title.getImdb()!=null) {
			dto.setImdbId(title.getImdb().getTconst());
		}
		return dto;
	}

	public Group mapToEntity(GroupDTO dto) {
		Group group = new Group();
		group.setId(group.getId());
		group.setName(group.getName());
		return group;
	}
}
