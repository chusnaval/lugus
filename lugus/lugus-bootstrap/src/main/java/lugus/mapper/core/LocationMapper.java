package lugus.mapper.core;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lugus.dto.core.LocationDTO;
import lugus.dto.core.LocationTypeDTO;
import lugus.model.core.Location;
import lugus.model.core.LocationType;


@Component
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LocationMapper {
	
	public LocationDTO mapToDTO(Location loc) {
		LocationDTO dto = new LocationDTO();
		dto.setCodigo(loc.getCodigo());
		dto.setDescripcion(loc.getDescripcion());
		dto.setCount(loc.getPeliculas().size());
		dto.setLocationType(new LocationTypeDTO(loc.getLocationType().getId(), loc.getLocationType().getDescription(), loc.getLocationType().getLocations().size()));
		return dto;
		
	}

	public Location mapToEntity(LocationDTO dto) {
		Location local = new Location();
		local.setCodigo(dto.getCodigo());
		local.setDescripcion(dto.getDescripcion());
		if(dto.getLocationType()!=null) {
			local.setLocationType(new LocationType(dto.getLocationType().getId()));
		}
		
		return local;
	}
}
