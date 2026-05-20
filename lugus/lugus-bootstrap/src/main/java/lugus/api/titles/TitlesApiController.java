package lugus.api.titles;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.TitleDto;
import lugus.mapper.titles.TitleMapper;
import lugus.service.titles.TitlesService;

@RestController
@RequestMapping("/v1/api/titles")
@RequiredArgsConstructor
public class TitlesApiController {
	
	private final TitlesService service;
	
	// BUSCAR TITULOS
	@GetMapping("/search")
	public List<TitleDto> searchTitles(@RequestParam String query) {
		return service.searchTitles(query).stream().map(TitleMapper::toDto).toList();
	}
}
