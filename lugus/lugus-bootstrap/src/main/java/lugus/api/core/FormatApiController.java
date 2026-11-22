package lugus.api.core;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FormatDTO;
import lugus.model.values.Format;

@RestController
@RequestMapping("/v1/api/formats")
@RequiredArgsConstructor
public class FormatApiController {

	@GetMapping
	List<FormatDTO> all() {
		List<Format> formats = Format.valoresOrdenados();
		return formats.stream().map(d -> convert(d)).toList();
	}

	private FormatDTO convert(final Format d) {
		FormatDTO dto = new FormatDTO();
		dto.setCodigo(String.valueOf(d.getId()));
		dto.setDescripcion(d.name());
		return dto;
	}

}
