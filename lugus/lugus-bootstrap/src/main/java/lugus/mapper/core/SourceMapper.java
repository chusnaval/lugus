package lugus.mapper.core;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.core.SourceDTO;
import lugus.model.core.Source;

@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class SourceMapper {

	public SourceDTO mapToDTO(Source source) {
		SourceDTO dto = new SourceDTO();
		dto.setId(source.getId());
		dto.setDescription(source.getDescripcion());
		dto.setSuggest(source.getSuggest());
		return dto;
		
	}

	public Source mapToEntity(SourceDTO dto) {
		Source source = new Source();
		source.setId(dto.getId());
		source.setDescripcion(dto.getDescription());
		source.setSuggest(dto.getSuggest());
		return source;
	}
}
