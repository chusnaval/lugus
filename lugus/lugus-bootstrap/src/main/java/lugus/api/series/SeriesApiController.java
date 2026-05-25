package lugus.api.series;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.FilmDto;
import lugus.dto.films.SerieDto;
import lugus.dto.films.SeriesStatsDto;
import lugus.dto.series.SerieCreateDto;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.series.SeriesMapper;
import lugus.model.series.Serie;
import lugus.model.values.Formato;
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
	
	@GetMapping("/page")
	public Page<SerieDto> getAllFilms(
	        @RequestParam Integer page,
	        @RequestParam Integer size,
	        @RequestParam(required = false) Boolean owned,
	        @RequestParam(required = false) String title,
	        @RequestParam(required = false) String casting,
	        @RequestParam(required = false) Integer fromYear,
	        @RequestParam(required = false) Integer toYear,
	        @RequestParam(required = false) String format,
	        @RequestParam(required = false) String genre,
	        @RequestParam(required = false) Boolean complete,
	        @RequestParam(required = false) String sort,
	        @RequestParam(required = false) String sortDirection
	) {
		FiltrosDto filtro = new FiltrosDto();
		if(page!=null) {
			filtro.setPagina(Optional.of(page));
		}
		if(title!=null) {
			filtro.setTitulo(title);
		}
		if(casting!=null) {
			filtro.setActor(casting);
			filtro.setDirector(casting);
		}
		if(fromYear!=null) {
			filtro.setFromAnyo(fromYear);
		}
		if(toYear!=null) {
			filtro.setToAnyo(toYear);
		}
		if(format!=null) {
			filtro.setFormato((int) Formato.getByName(format).getId());
		}
		if(genre!=null) {
			filtro.setGenero(genre);
		}
		if(complete!=null) {
			filtro.setCompleta(complete);
		}

		if(sort!=null) {
			filtro.setOrden(Optional.of(sort));
		}
		if(sortDirection!=null) {
			filtro.setDireccion(Optional.of(sortDirection));
		}
		if(owned!=null) {
			filtro.setComprado(owned);
		}
		filtro.setPageSize(size);
	    return service.findAllBy(filtro).map(mapper::mapToSerieDTO);
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
		stats.setIncompleteGroups(service.countByComprado(false));
		stats.setCompleteGroups(service.countByComprado(true));
		return stats;
	}
}
