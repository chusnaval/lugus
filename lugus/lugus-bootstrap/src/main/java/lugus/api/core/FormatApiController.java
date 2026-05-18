package lugus.api.core;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FormatDTO;
import lugus.model.values.Formato;

@RestController
@ResponseBody
@RequestMapping("/v1/api/formats")
@RequiredArgsConstructor
public class FormatApiController {

	@GetMapping
	List<FormatDTO> all() {
		List<Formato> formats = Formato.valoresOrdenados();
		return formats.stream().map(d -> convert(d)).collect(Collectors.toList());
	}

	private FormatDTO convert(Formato d) {
		FormatDTO dto = new FormatDTO();
		dto.setCodigo(String.valueOf(d.getId()));
		dto.setDescripcion(d.name());
		return dto;
	}

}
