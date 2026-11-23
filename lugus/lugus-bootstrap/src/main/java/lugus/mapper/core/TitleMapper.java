package lugus.mapper.core;

import org.springframework.stereotype.Component;

import lugus.dto.groups.TitleDto;
import lugus.model.titles.Title;
@Component
public class TitleMapper {

	public TitleDto mapToDTO(Title title) {
		TitleDto dto = new TitleDto();
		dto.setId(title.getId());
		dto.setTitle(title.getTitle());
		dto.setPosterUrl(title.getUrlCover());
		dto.setYear(title.getYear());
		
		
		dto.setPeliculaId(title.getFilm() != null ? title.getFilm().getId() : null);
		dto.setSerieId(title.getSeries() != null ? title.getSeries().getId() : null);
		dto.setImdbId(title.getImdb() != null ? title.getImdb().getTconst() : null);
			
		// calcular type
		if (title.getFilm() != null) {
			dto.setOwned(title.getFilm().isPurchased());
			dto.setType("MOVIES");
		} else if (title.getSeries() != null) {
			dto.setOwned(title.getSeries().isComplete());
			dto.setType("SERIES");
		} else if (title.getImdb() != null) {
			dto.setOwned(false);
			dto.setType("EXTERNAL");
		} else {
			dto.setOwned(false);
			dto.setType("EXTERNAL");
		}
		
		return dto;
	}
}
