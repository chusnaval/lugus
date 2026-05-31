package lugus.service.user;

import org.springframework.stereotype.Component;

import lugus.dto.core.GenreDTO;
import lugus.model.values.Genero;

@Component
public class GenreMapper {

	public GenreDTO toDTO(Genero genre) {
		GenreDTO dto = new GenreDTO();
		dto.setCodigo(genre.getCodigo());
		dto.setDescripcion(genre.name());
		return dto;
	}
	
	public Genero toEntity(GenreDTO dto) {
		return Genero.getById(dto.getCodigo());
	}
}
