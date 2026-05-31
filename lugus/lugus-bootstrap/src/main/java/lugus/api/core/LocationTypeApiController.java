package lugus.api.core;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.LocationTypeDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.core.LocationTypeMapper;
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
	
	@PostMapping
	LocationTypeDTO newLocation(@RequestBody LocationTypeDTO dto) {
		LocationType location = service.save(mapper.mapToEntity(dto));
		return mapper.mapToDTO(location);
	}
	
	@PutMapping("/{id}")
	LocationTypeDTO replace(@RequestBody LocationTypeDTO newSource, @PathVariable Integer id) {
		Optional<LocationType> lt = service.findById(id);
		if (lt.isPresent()) {
			LocationType obj = lt.get();
			obj.setDescription(newSource.getDescription());
			obj = service.save(obj);

			return mapper.mapToDTO(obj);
		} else {
			throw new LugusNotFoundException(id);
		}
	}
	
	@DeleteMapping("/{id}")
	void delete(@PathVariable Integer id) {
		service.deleteById(id);
	}

}
