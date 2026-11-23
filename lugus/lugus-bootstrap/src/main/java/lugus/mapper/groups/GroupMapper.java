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

	public GroupDTO mapToDTO(final Group group) {
		GroupDTO dto = new GroupDTO();
		dto.setId(group.getId());
		dto.setName(group.getName());
		dto.setMovieCount(group.getFilms().size());
		String cover = "/covers/placeholder.png";
		Optional<GroupTitle> gf = group.getFilms().stream().findFirst();
		if (gf.isPresent() && gf.get().getTitle().getFilm() != null) {
			cover = gf.get().getTitle().getFilm().getCoverUrl();
		}else if (gf.isPresent() && gf.get().getTitle().getSeries() != null) {
			cover = gf.get().getTitle().getSeries().getCoverUrl();
		}else if (gf.isPresent() && gf.get().getTitle().getUrlCover() != null) {
			cover = gf.get().getTitle().getUrlCover();
		}
		dto.setPercentageOwned(group.getFilms().stream()
				.filter(gt -> (gt.getTitle().getFilm() != null && gt.getTitle().getFilm().isPurchased())
						|| (gt.getTitle().getSeries() != null && gt.getTitle().getSeries().isComplete()))
				.count() * 100.0 / group.getFilms().size());
		dto.setCover(cover);
		dto.setFilmaffinityId(group.getFilmaffinityId());
		dto.setTitles(mapChilds(group));
		return dto;

	}

	private List<GroupTitleDto> mapChilds(final Group group) {
		List<GroupTitleDto> resultado = new ArrayList<>();
		for (GroupTitle gt : group.getFilms()) {
			GroupTitleDto dto = new GroupTitleDto();
			dto.setId(gt.getId());
			dto.setOrden(gt.getOrden());
			dto.setTitle(mapTitle(gt.getTitle()));
			resultado.add(dto);
		}
		return resultado;
	}

	private TitleDto mapTitle(final Title title) {
		TitleDto dto = new TitleDto();
		dto.setId(title.getId());
		dto.setTitle(title.getTitle());
		dto.setYear(title.getYear());
		dto.setType(title.getType().name());
		dto.setPosterUrl(title.getUrlCover());
		if (title.getFilm() != null) {
			dto.setPeliculaId(title.getFilm().getId());
		}
		if (title.getSeries() != null) {
			dto.setSerieId(title.getSeries().getId());
		}
		if (title.getImdb() != null) {
			dto.setImdbId(title.getImdb().getTconst());
		}
		dto.setOwned((title.getFilm() != null && title.getFilm().isPurchased())
				|| (title.getSeries() != null && title.getSeries().isComplete()));
		
		return dto;
	}

	public Group mapToEntity(final GroupDTO dto) {
		Group group = new Group();
		group.setId(dto.getId());
		group.setName(dto.getName());
		return group;
	}
}
