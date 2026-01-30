package lugus.mapper.core;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.core.LocationDTO;
import lugus.model.core.Location;


@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LocationMapper {
	
	public LocationDTO mapToDTO(Location loc) {
		LocationDTO dto = new LocationDTO();
		dto.setCodigo(loc.getCodigo());
		dto.setDescripcion(loc.getDescripcion());
		return dto;
		
	}

	public Location mapToEntity(LocationDTO dto) {
		Location local = new Location();
		local.setCodigo(local.getCodigo());
		local.setDescripcion(local.getDescripcion());
		return local;
	}
}
