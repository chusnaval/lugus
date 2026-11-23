package lugus.mapper.titles;

import lugus.dto.groups.TitleDto;
import lugus.model.titles.Title;

public class TitleMapper {

	private TitleMapper() {// util class
	}

	public static TitleDto toDto(Title t) {
		TitleDto dto = new TitleDto();
		dto.setId(t.getId());
		dto.setTitle(t.getTitle());
		dto.setYear(t.getYear());
		dto.setType(t.getType().name());
		dto.setPosterUrl(t.getUrlCover());
		dto.setPeliculaId(t.getFilm() != null ? t.getFilm().getId() : null);
		dto.setSerieId(t.getSeries() != null ? t.getSeries().getId() : null);
		dto.setImdbId(t.getImdb() != null ? t.getImdb().getTconst() : null);
		dto.setOwned((t.getFilm() != null && t.getFilm().isPurchased())
				|| (t.getSeries() != null && t.getSeries().isComplete()));

		return dto;
	}
}
