package lugus.mapper.films;

import org.springframework.stereotype.Component;

import lugus.dto.films.PeliculaCreateDto;
import lugus.model.films.Pelicula;

@Component
public class FilmMapper {

	public PeliculaCreateDto mapToDTO(Pelicula film) {
		PeliculaCreateDto dto = new PeliculaCreateDto();
		dto.setId(film.getId());
		dto.setTitulo(film.getTitulo());
		dto.setTituloGest(film.getTituloGest());
		dto.setAnyo(film.getAnyo());

		if(film.getFormato()!=null) {
			dto.setFormatoCodigo(film.getFormato().getId());
		}
		
		if(film.getGenero()!=null) {
			dto.setGeneroCodigo(film.getGenero().getCodigo());
		}
		
		if(film.getLocation()!=null) {
			dto.setLocationCode(film.getLocation().getCodigo());
		}

		dto.setPack(film.isPack());
		dto.setSteelbook(film.isSteelbook());
		dto.setFunda(film.isFunda());
		dto.setComprado(film.isComprado());
		dto.setNotas(film.getNotas());
		
		return dto;
	}

}
