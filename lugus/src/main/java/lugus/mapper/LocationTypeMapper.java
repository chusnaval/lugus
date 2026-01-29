package lugus.mapper;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.core.LocationTypeDTO;
import lugus.model.core.LocationType;

@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LocationTypeMapper {

	public LocationTypeDTO mapToDTO(LocationType loc) {
		LocationTypeDTO dto = new LocationTypeDTO();
		dto.setId(loc.getId());
		dto.setDescripcion(loc.getDescription());
		return dto;
		
	}

	public LocationType mapToEntity(LocationTypeDTO dto) {
		LocationType local = new LocationType();
		local.setId(local.getId());
		local.setDescription(local.getDescription());
		return local;
	}
}
