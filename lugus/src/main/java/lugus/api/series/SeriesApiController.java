package lugus.api.series;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.series.SerieCreateDto;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.series.SeriesMapper;
import lugus.model.series.Serie;
import lugus.service.series.SeriesService;

@RestController
@RequestMapping("/v1/api/series")
@RequiredArgsConstructor
public class SeriesApiController {

	private final SeriesMapper mapper;
	
	private final SeriesService service;
	
	@GetMapping("/{id}")
	SerieCreateDto one(@PathVariable int id) {
		Serie serie = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		
		return mapper.mapToDTO(serie);
	}
	
	@GetMapping("/wanted")
	List<SerieCreateDto> wanted() throws LugusNotFoundException {
		Page<Serie> page = service.wanted();
		List<SerieCreateDto> result = new ArrayList<SerieCreateDto>();
		for (Serie p : page.getContent()) {
			result.add(mapper.mapToDTO(p));
		}
		return result;
	}
}
