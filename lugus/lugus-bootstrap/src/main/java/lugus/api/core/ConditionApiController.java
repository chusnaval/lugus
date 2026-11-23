package lugus.api.core;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.films.ConditionDto;
import lugus.model.core.Status;
import lugus.service.core.EstadoService;

@RestController
@RequestMapping("/v1/api/conditions")
@RequiredArgsConstructor
public class ConditionApiController {
	
	private final EstadoService service;
	@GetMapping
	List<ConditionDto> all() {
		List<Status> formats = service.findAll();
		return formats.stream().map(d -> convert(d)).toList();
	}

	private ConditionDto convert(Status d) {
		ConditionDto dto = new ConditionDto();
		dto.setId(d.getId());
		dto.setDesc(d.getName());
		return dto;
	}
}
