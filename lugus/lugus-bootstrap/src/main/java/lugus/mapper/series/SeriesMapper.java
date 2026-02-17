package lugus.mapper.series;

import org.springframework.stereotype.Component;

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

		if(serie.getFormato()!=null) {
			dto.setFormatoCodigo(serie.getFormato().getId());
		}
		
		if(serie.getGenero()!=null) {
			dto.setGeneroCodigo(serie.getGenero().getCodigo());
		}
		
		if(serie.getLocation()!=null) {
			dto.setLocationCode(serie.getLocation().getCodigo());
		}

		dto.setComprado(serie.isComprado());
		dto.setCompleta(serie.isCompleta());
		dto.setNotas(serie.getNotas());
		
		return dto;
	}

}
