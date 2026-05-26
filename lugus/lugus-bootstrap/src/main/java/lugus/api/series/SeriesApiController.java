package lugus.api.series;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.series.Serie;
import lugus.model.series.SerieFoto;
import lugus.model.values.Formato;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.films.DwFotoService;
import lugus.service.films.DwFotoServiceI;
import lugus.service.series.SeriesService;

@RestController
@RequestMapping("/v1/api/series")
@RequiredArgsConstructor
public class SeriesApiController {

	private final SeriesMapper mapper;
	
	private final SeriesService service;
	
	private final SourceService sourceService;
	
	private final LocationService locService;
	
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
	
	@PutMapping("/{id}")
	public SerieDto update(
	        @PathVariable Integer id,
	        @RequestBody SerieDto dto
	) throws IOException, URISyntaxException {
	    return mapper.mapToSerieDTO(service.update(id, dto));
	}
	
	@PostMapping("new")
	ResponseEntity<Object> save(@RequestBody SerieDto dto, Authentication auth) throws LugusNotFoundException, IOException, URISyntaxException {
		Serie serie = mapper.mapToSerie(dto);
		Location loc = findLocation(dto);
		serie.setLocation(loc);
		serie.calcularCodigo();
		serie.setTsAlta(Instant.now());
		serie.setUsrAlta(auth.getName());
		Serie saved = service.save(serie);
		if (dto.getCoverSrc() != null && !dto.getCoverSrc().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			final int sourceId = service.calcularIdSource(dto.getCoverSrc());
			Optional<Source> sourceObj = sourceService.findById(sourceId);
			SerieFoto pf = new SerieFoto();
			pf.setUrl(dto.getCoverSrc());
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setFoto(dwFotoService.descargar(sourceId, dto.getCoverSrc()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			saved = service.save(saved);
		}

		
		
		return ResponseEntity.ok().build();
	}
	
	
	private Location findLocation(SerieDto dto) {
		Location loc = null;
		if (dto.getLocation() != null && !dto.getLocation().isBlank()) {
			loc = locService.findById(dto.getLocation())
					.orElseThrow(() -> new LugusNotFoundException(dto.getLocation()));
		}
		return loc;
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
