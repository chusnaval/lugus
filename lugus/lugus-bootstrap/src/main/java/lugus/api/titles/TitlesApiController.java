package lugus.api.titles;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.SearchTitleResultDto;
import lugus.dto.groups.TitleDto;
import lugus.mapper.core.TitleMapper;
import lugus.model.titles.Title;
import lugus.model.values.TitleType;
import lugus.service.titles.TitleSearchService;
import lugus.service.titles.TitlesService;

@RestController
@RequestMapping("/v1/api/titles")
@RequiredArgsConstructor
public class TitlesApiController {

	private final TitleSearchService searchService;
	
	private final TitlesService service;
	
	private final TitleMapper mapper;

	@GetMapping("/search")
	public List<SearchTitleResultDto> search(@RequestParam String query) {
		return searchService.search(query);
	}
	
	@GetMapping
	List<TitleDto> all() {
		List<Title> sources = service.findByType(TitleType.EXTERNAL);
		return sources.stream().map(mapper::mapToDTO).collect(Collectors.toList());
	}

	
}
