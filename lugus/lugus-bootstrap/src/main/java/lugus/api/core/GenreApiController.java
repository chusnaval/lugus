package lugus.api.core;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.GenreDTO;
import lugus.model.values.Genre;

@RestController

@RequestMapping("/v1/api/genres")
@RequiredArgsConstructor
public class GenreApiController {

	@GetMapping
	List<GenreDTO> all() {
		List<Genre> genres = Genre.orderedValues();
		return genres.stream().map(d -> this.convert(d)).toList();
	}

	private GenreDTO convert(final Genre d) {
		GenreDTO dto = new GenreDTO();
		dto.setCodigo(String.valueOf(d.getCode()));
		dto.setDescripcion(d.name());
		return dto;
	}
}
