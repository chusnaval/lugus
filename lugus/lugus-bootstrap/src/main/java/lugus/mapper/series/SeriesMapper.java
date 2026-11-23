package lugus.mapper.series;

import org.springframework.stereotype.Component;

import lugus.dto.films.CastDto;
import lugus.dto.films.SerieDto;
import lugus.dto.series.SeasonDto;
import lugus.model.series.Season;
import lugus.model.series.Series;
import lugus.model.values.Format;
import lugus.model.values.Genre;

@Component
public class SeriesMapper {

	public SerieDto mapToSerieDTO(Series serie) {

		SerieDto dto = new SerieDto();
		dto.setId(serie.getId());
		dto.setTitle(serie.getTitle());
		dto.setTitleMgmt(serie.getMgmtTitle());
		dto.setStartYear(serie.getStartYear());
		dto.setFinishYear(serie.getEndYear());
		dto.setMgmtCode(serie.getCode());

		if (serie.getFormat() != null) {
			dto.setFormat(serie.getFormat().name());
		}

		if (serie.getGenre() != null) {
			dto.setGenreCode(serie.getGenre().getCode());
			dto.setGenreDesc(serie.getGenre().getDisplayName());
		}

		if (serie.getLocation() != null) {
			dto.setLocation(serie.getLocation().getDescription());
		}

		createCasting(dto, serie);
		createSeasons(dto, serie);

		dto.setOwned(serie.isPurchased());
		dto.setCompleted(serie.isComplete());
		dto.setNotes(serie.getNotes());
		dto.setCoverSrc(serie.getCoverUrl());
		return dto;
	}

	private void createSeasons(SerieDto dto, Series serie) {
		for (Season season : serie.getSeasons()) {
			dto.addSeason(new SeasonDto(season.getDesc(), season.getOrder(), season.isPurchased(), season.isWanted()));
		}

	}

	public Series mapToSerie(SerieDto dto) {
		Series serie = new Series();
		serie.setTitle(dto.getTitle());
		serie.setMgmtTitle(dto.getTitleMgmt());
		serie.setStartYear(dto.getStartYear());
		serie.setEndYear(dto.getFinishYear());
		serie.setCode(dto.getMgmtCode());

		serie.setFormat(Format.getById(Short.valueOf(dto.getFormat())));
		serie.setGenre(Genre.getById(dto.getGenreCode()));

		serie.setComplete(dto.isCompleted());
		serie.setPurchased(dto.isOwned());
		serie.setNotes(dto.getNotes());

		return serie;
	}

	private void createCasting(SerieDto dto, Series film) {
		film.getActores()
				.forEach(act -> dto
						.addCast(new CastDto(act.getPeople(), act.getOrder(), act.getName(), act.getCharacter())));

	}
}
