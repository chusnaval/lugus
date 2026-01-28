package lugus.api.core;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.SourceDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.SourceMapper;
import lugus.model.core.Source;
import lugus.service.core.SourceService;

@RestController
@RequestMapping("/v1/api/sources")
@RequiredArgsConstructor
public class SourceApiController {

	private final SourceService service;

	private final SourceMapper mapper;

	@GetMapping
	List<SourceDTO> all() {
		List<Source> sources = service.findAll();
		return sources.stream().map(mapper::mapToDTO).collect(Collectors.toList());
	}

	@PostMapping
	SourceDTO newSource(@RequestBody SourceDTO dto) {
		Source source = service.save(mapper.mapToEntity(dto));
		return mapper.mapToDTO(source);
	}

	@GetMapping("/{id}")
	SourceDTO one(@PathVariable Integer id) throws LugusNotFoundException {
		Source source = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		return mapper.mapToDTO(source);
	}

	@PutMapping("/{id}")
	SourceDTO replaceEmployee(@RequestBody SourceDTO newSource, @PathVariable Integer id) {
		Optional<Source> source = service.findById(id);
		if (source.isPresent()) {
			Source obj = source.get();
			obj.setDescripcion(newSource.getDescription());
			obj.setSuggest(newSource.getSuggest());
			obj = service.save(obj);

			return mapper.mapToDTO(obj);
		} else {
			throw new LugusNotFoundException(id);
		}
	}

	@DeleteMapping("/{id}")
	void deleteEmployee(@PathVariable Integer id) {
		service.deleteById(id);
	}

	@GetMapping("/suggested")
	public ResponseEntity<List<SourceDTO>> findAllWhenSuggestNotNull() {
		List<Source> sources = service.findBySuggestIsNotNull();
		return ResponseEntity.ok(sources.stream().map(mapper::mapToDTO).collect(Collectors.toList()));
	}
}
