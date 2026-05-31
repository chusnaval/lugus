package lugus.mapper.titles;

import lugus.dto.groups.TitleDto;
import lugus.model.titles.Title;

public class TitleMapper {

    public static TitleDto toDto(Title t) {
        TitleDto dto = new TitleDto();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setYear(t.getYear());
        dto.setType(t.getType().name());
        dto.setPosterUrl(t.getPosterUrl());
        dto.setPeliculaId(t.getPelicula() != null ? t.getPelicula().getId() : null);
        dto.setSerieId(t.getSerie() != null ? t.getSerie().getId() : null);
        dto.setImdbId(t.getImdb() != null ? t.getImdb().getTconst() : null);
        return dto;
    }
}
