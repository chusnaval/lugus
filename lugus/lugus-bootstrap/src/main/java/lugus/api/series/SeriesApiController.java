package lugus.api.series;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.films.SerieDto;
import lugus.dto.films.SeriesStatsDto;
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
	SerieDto one(@PathVariable int id) {
		Serie serie = service.findById(id).orElse(null);
		if(serie!=null) {
			return mapper.mapToSerieDTO(serie);
		}
		return null;
	}
	
	@GetMapping("/wanted")
	List<SerieCreateDto> wanted() throws LugusNotFoundException {
		Page<Serie> page = service.wanted();
		List<SerieCreateDto> result = new ArrayList<>();
		for (Serie p : page.getContent()) {
			result.add(mapper.mapToDTO(p));
		}
		return result;
	}
	
	@GetMapping(value="/ultimas", produces = "application/json;charset=UTF-8")
	public List<SerieDto> ultimas() throws LugusNotFoundException {

		return service.findForHome().getContent().stream()
                .map(mapper::mapToSerieDTO)
                .toList();
	}
	
	@GetMapping(value="/stats", produces = "application/json;charset=UTF-8")
	public SeriesStatsDto getStats() {
		SeriesStatsDto stats = new SeriesStatsDto();
		stats.setTotal(service.contarTodas());
		stats.setRecent(service.addedInLastDays(30));
		stats.setIncompleteGroups(0);
		stats.setCompleteGroups(0);
		return stats;
	}
}
