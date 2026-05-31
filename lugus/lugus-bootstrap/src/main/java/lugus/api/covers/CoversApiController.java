package lugus.api.covers;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.films.FilmDto;
import lugus.dto.filters.CoversFilter;
import lugus.mapper.films.FilmMapper;
import lugus.service.films.PeliculaService;

@RestController
@ResponseBody
@RequestMapping("/v1/api/covers")
@RequiredArgsConstructor
public class CoversApiController {

	private final PeliculaService service;

	private final FilmMapper mapper;

	@GetMapping("/page")
	public Page<FilmDto> getCoversPage(@RequestParam int page, @RequestParam int size,
			@RequestParam(required = false) String missing, @RequestParam(required = false) String source,
			@RequestParam(required = false) String title) {
		CoversFilter filter = new CoversFilter(missing, source, title);
		return service.getCoversPage(page, size, filter).map(mapper::mapToFilmDTO);
	}

}
