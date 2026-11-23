package lugus.service.user;

import org.springframework.stereotype.Component;

import lugus.dto.core.GenreDTO;
import lugus.model.values.Genre;

@Component
public class GenreMapper {

	public GenreDTO toDTO(Genre genre) {
		GenreDTO dto = new GenreDTO();
		dto.setCodigo(genre.getCode());
		dto.setDescripcion(genre.name());
		return dto;
	}
	
	public Genre toEntity(GenreDTO dto) {
		return Genre.getById(dto.getCodigo());
	}
}
