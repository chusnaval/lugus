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
		dto.setPosterUrl(title.getPosterUrl());
		dto.setYear(title.getYear());
		
		
		dto.setPeliculaId(title.getPelicula() != null ? title.getPelicula().getId() : null);
		dto.setSerieId(title.getSerie() != null ? title.getSerie().getId() : null);
		dto.setImdbId(title.getImdb() != null ? title.getImdb().getTconst() : null);
			
		// calcular type
		if (title.getPelicula() != null) {
			dto.setOwned(title.getPelicula().isComprado());
			dto.setType("MOVIES");
		} else if (title.getSerie() != null) {
			dto.setOwned(title.getSerie().isCompleta());
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
