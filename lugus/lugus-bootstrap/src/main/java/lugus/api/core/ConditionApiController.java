package lugus.api.core;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.films.ConditionDto;
import lugus.model.core.Estado;
import lugus.service.core.EstadoService;

@RestController
@ResponseBody
@RequestMapping("/v1/api/conditions")
@RequiredArgsConstructor
public class ConditionApiController {
	
	private final EstadoService service;
	@GetMapping
	List<ConditionDto> all() {
		List<Estado> formats = service.findAll();
		return formats.stream().map(d -> convert(d)).collect(Collectors.toList());
	}

	private ConditionDto convert(Estado d) {
		ConditionDto dto = new ConditionDto();
		dto.setId(d.getId());
		dto.setDesc(d.getName());
		return dto;
	}
}
