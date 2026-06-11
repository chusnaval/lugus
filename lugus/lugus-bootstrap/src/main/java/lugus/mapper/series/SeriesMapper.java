package lugus.mapper.series;

import org.springframework.stereotype.Component;

import lugus.dto.films.CastDto;
import lugus.dto.films.SerieDto;
import lugus.dto.series.SeasonDto;
import lugus.model.series.Season;
import lugus.model.series.Serie;
import lugus.model.values.Formato;
import lugus.model.values.Genero;

@Component
public class SeriesMapper {

	public SerieDto mapToSerieDTO(Serie serie) {

		SerieDto dto = new SerieDto();
		dto.setId(serie.getId());
		dto.setTitle(serie.getTitulo());
		dto.setTitleMgmt(serie.getTituloGest());
		dto.setStartYear(serie.getAnyoInicio());
		dto.setFinishYear(serie.getAnyoFin());
		dto.setMgmtCode(serie.getCodigo());

		if (serie.getFormato() != null) {
			dto.setFormat(serie.getFormato().name());
		}

		if (serie.getGenero() != null) {
			dto.setGenreCode(serie.getGenero().getCodigo());
			dto.setGenreDesc(serie.getGenero().getDisplayName());
		}

		if (serie.getLocation() != null) {
			dto.setLocation(serie.getLocation().getDescripcion());
		}

		createCasting(dto, serie);
		createSeasons(dto, serie);

		dto.setOwned(serie.isComprado());
		dto.setCompleted(serie.isCompleta());
		dto.setNotes(serie.getNotas());
		dto.setCoverSrc(serie.getCoverUrl());
		return dto;
	}

	private void createSeasons(SerieDto dto, Serie serie) {
		for(Season season  : serie.getSeasons()) {
			dto.addSeason(new SeasonDto(season.getDesc(), season.getOrder(), season.isPurchased(), season.isWanted()));
		}
		
	}

	public Serie mapToSerie(SerieDto dto) {
		Serie serie = new Serie();
		serie.setTitulo(dto.getTitle());
		serie.setTituloGest(dto.getTitleMgmt());
		serie.setAnyoInicio(dto.getStartYear());
		serie.setAnyoFin(dto.getFinishYear());
		serie.setCodigo(dto.getMgmtCode());

		serie.setFormato(Formato.getById(Short.valueOf(dto.getFormat())));
		serie.setGenero(Genero.getById(dto.getGenreCode()));

		serie.setCompleta(dto.isCompleted());
		serie.setComprado(dto.isOwned());
		serie.setNotas(dto.getNotes());

		return serie;
	}

	private void createCasting(SerieDto dto, Serie film) {
		film.getActores().stream()
				.forEach(act -> dto.addCast(new CastDto(act.getPersona(), act.getNombre(), act.getPersonaje())));

	}
}
