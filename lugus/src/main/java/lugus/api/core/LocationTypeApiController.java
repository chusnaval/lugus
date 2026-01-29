package lugus.api.core;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.LocationTypeDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.LocationTypeMapper;
import lugus.model.core.LocationType;
import lugus.service.core.LocationTypeService;

@RestController
@RequestMapping("/v1/api/locationTypes")
@RequiredArgsConstructor
public class LocationTypeApiController {

	private final LocationTypeService service;

	private final LocationTypeMapper mapper;

	@GetMapping
	List<LocationTypeDTO> all() {
		List<LocationType> locations = service.findAll();
		return locations.stream().map(mapper::mapToDTO).collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	LocationTypeDTO one(@PathVariable int id) throws LugusNotFoundException {
		LocationType loc = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		return mapper.mapToDTO(loc);
	}

}
