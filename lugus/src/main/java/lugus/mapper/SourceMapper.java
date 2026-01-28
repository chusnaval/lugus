package lugus.mapper;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.core.SourceDTO;
import lugus.model.core.Fuente;

@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SourceMapper {

	public SourceDTO mapToDTO(Fuente source) {
		SourceDTO dto = new SourceDTO();
		dto.setId(source.getId());
		dto.setDescription(source.getDescripcion());
		dto.setSuggest(source.getSuggest());
		return dto;
		
	}

	public Fuente mapToEntity(SourceDTO dto) {
		Fuente source = new Fuente();
		source.setId(dto.getId());
		source.setDescripcion(dto.getDescription());
		source.setSuggest(dto.getSuggest());
		return null;
	}
}
