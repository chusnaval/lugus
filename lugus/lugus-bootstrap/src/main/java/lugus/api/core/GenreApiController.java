package lugus.api.core;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.GenreDTO;
import lugus.model.values.Genere;

@RestController
@ResponseBody
@RequestMapping("/v1/api/genres")
@RequiredArgsConstructor
public class GenreApiController {

	@GetMapping
	List<GenreDTO> all() {
		List<Genere> genres = Genere.valoresOrdenados();
		return genres.stream().map(d -> convert(d)).collect(Collectors.toList());
	}

	private GenreDTO convert(Genere d) {
		GenreDTO dto = new GenreDTO();
		dto.setCodigo(String.valueOf(d.getCodigo()));
		dto.setDescripcion(d.name());
		return dto;
	}
}
