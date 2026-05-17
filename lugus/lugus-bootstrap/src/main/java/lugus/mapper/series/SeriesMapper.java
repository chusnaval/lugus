package lugus.mapper.series;

import org.springframework.stereotype.Component;

import lugus.dto.films.SerieDto;
import lugus.dto.series.SerieCreateDto;
import lugus.model.series.Serie;

@Component
public class SeriesMapper {

	public SerieCreateDto mapToDTO(Serie serie) {

		SerieCreateDto dto = new SerieCreateDto();
		dto.setId(serie.getId());
		dto.setTitulo(serie.getTitulo());
		dto.setTituloGest(serie.getTituloGest());
		dto.setAnyoInicio(serie.getAnyoInicio());
		dto.setAnyoFin(serie.getAnyoFin());

		if (serie.getFormato() != null) {
			dto.setFormatoCodigo(serie.getFormato().getId());
		}

		if (serie.getGenero() != null) {
			dto.setGeneroCodigo(serie.getGenero().getCodigo());
		}

		if (serie.getLocation() != null) {
			dto.setLocationCode(serie.getLocation().getCodigo());
		}

		dto.setComprado(serie.isComprado());
		dto.setCompleta(serie.isCompleta());
		dto.setNotas(serie.getNotas());

		return dto;
	}

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

		dto.setOwned(serie.isComprado());
		dto.setCompleted(serie.isCompleta());
		dto.setNotes(serie.getNotas());
		dto.setCoverSrc(serie.getCoverUrl());
		return dto;
	}
}
