package lugus.service.user;

import org.springframework.stereotype.Component;

import lugus.dto.core.GenreDTO;
import lugus.model.values.Genere;

@Component
public class GenreMapper {

	public GenreDTO toDTO(Genere genre) {
		GenreDTO dto = new GenreDTO();
		dto.setCodigo(genre.getCodigo());
		dto.setDescripcion(genre.name());
		return dto;
	}
	
	public Genere toEntity(GenreDTO dto) {
		return Genere.getById(dto.getCodigo());
	}
}
