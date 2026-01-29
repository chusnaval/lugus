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
import lugus.dto.core.LocationDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.LocationMapper;
import lugus.model.core.Location;
import lugus.service.core.LocationService;

@RestController
@RequestMapping("/v1/api/locations")
@RequiredArgsConstructor
public class LocationApiController {

	private final LocationService service;

	private final LocationMapper mapper;

	@GetMapping
	List<LocationDTO> all() {
		List<Location> sources = service.findAll();
		return sources.stream().map(mapper::mapToDTO).collect(Collectors.toList());
	}

	@PostMapping
	LocationDTO newLocation(@RequestBody LocationDTO dto) {
		Location location = service.save(mapper.mapToEntity(dto));
		return mapper.mapToDTO(location);
	}

	@GetMapping("/{id}")
	LocationDTO one(@PathVariable String id) throws LugusNotFoundException {
		Location loc = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		return mapper.mapToDTO(loc);
	}

	@PutMapping("/{id}")
	LocationDTO replaceEmployee(@RequestBody LocationDTO newSource, @PathVariable String id) {
		Optional<Location> location = service.findById(id);
		if (location.isPresent()) {
			Location obj = location.get();
			obj.setDescripcion(newSource.getDescripcion());
			obj = service.save(obj);

			return mapper.mapToDTO(obj);
		} else {
			throw new LugusNotFoundException(id);
		}
	}

	@DeleteMapping("/{id}")
	void deleteEmployee(@PathVariable String id) {
		service.deleteById(id);
	}

}
