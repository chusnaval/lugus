package lugus.api.series;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import lugus.dto.core.FilterDto;
import lugus.dto.films.SerieDto;
import lugus.dto.films.SeriesStatsDto;
import lugus.exception.LugusNotFoundException;
import lugus.export.SeriesExportService;
import lugus.mapper.series.SeriesMapper;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.series.Series;
import lugus.model.series.SeriesImage;
import lugus.model.values.Format;
import lugus.model.values.TitleType;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.films.DwImageService;
import lugus.service.films.DwImageServiceI;
import lugus.service.people.InsertPersonalSeriesDataService;
import lugus.service.series.SeriesService;
import lugus.service.titles.TitlesService;

@RestController
@RequestMapping("/v1/api/series")
@RequiredArgsConstructor
public class SeriesApiController {

	private final SeriesMapper mapper;
	
	private final SeriesService service;
	
	private final SourceService sourceService;
	
	private final LocationService locService;
	
	private final SeriesExportService seriesExportService;
	
	private final InsertPersonalSeriesDataService insertImdbService;
	
	private final TitlesService titlesService;
	
	@GetMapping("/{id}")
	SerieDto one(@PathVariable int id) {
		Series serie = service.findById(id).orElse(null);
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
		FilterDto filtro = new FilterDto();
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
			filtro.setFormato((int) Format.getByName(format).getId());
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
		Series serie = mapper.mapToSerie(dto);
		Location loc = findLocation(dto);
		serie.setLocation(loc);
		serie.obtainCode();
		serie.setTsCreation(Instant.now());
		serie.setUsrCreation(auth.getName());
		Series saved = service.save(serie);
		
		if (dto.getCoverSrc() != null && !dto.getCoverSrc().isEmpty()) {
			final DwImageServiceI dwFotoService = new DwImageService();
			final int sourceId = sourceService.calcularIdSource(dto.getCoverSrc());
			Optional<Source> sourceObj = sourceService.findById(sourceId);
			SeriesImage pf = new SeriesImage();
			pf.setUrl(dto.getCoverSrc());
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setContent(dwFotoService.download(sourceId, dto.getCoverSrc()));
			pf.setCover(true);

			saved.addCover(pf);
			service.save(saved);
		}

		if (dto.getImdbId() != null && !dto.getImdbId().isBlank()) {
			insertImdbService.insert(saved.getId(), dto.getImdbId());

			titlesService.findByImdb_Id(dto.getImdbId()).ifPresent(title -> {
				title.setTitle(saved.getTitle());
				title.setYear(saved.getStartYear());
				title.setType(TitleType.SERIES);
				title.setUrlCover(dto.getCoverSrc());
				title.setSeries(saved);
				titlesService.save(title);
			});
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
		stats.setIncompleteGroups(service.countByPurchased(false));
		stats.setCompleteGroups(service.countByPurchased(true));
		return stats;
	}
	
	@GetMapping("/export/ods")
	public ResponseEntity<byte[]> exportOds(  @RequestParam Integer page,
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
	        @RequestParam(required = false) String sortDirection) throws IOException {
		FilterDto filtro = createFilter(page, size, owned, title, casting, fromYear, toYear, format, genre, false, sort,
				sortDirection);
	    byte[] file = seriesExportService.toOds(service.findAllBy(filtro).getContent());

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.ods")
	            .contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet"))
	            .body(file);
	}

	private FilterDto createFilter(Integer page, Integer size, Boolean owned, String title, String casting,
			Integer fromYear, Integer toYear, String format, String genre, Boolean pack, String sort,
			String sortDirection) {
		FilterDto filtro = new FilterDto();
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
			filtro.setFormato((int) Format.getByName(format).getId());
		}
		if(genre!=null) {
			filtro.setGenero(genre);
		}
		if(pack!=null) {
			filtro.setPack(pack);
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
		int sizeAux = size;
		if(size==null || size <=0) {
			sizeAux = Integer.MAX_VALUE;
		}
		filtro.setPageSize(sizeAux);
		return filtro;
	}

	
	@GetMapping("/export/md")
	public ResponseEntity<String> exportMarkdown(	  @RequestParam Integer page,
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
	        @RequestParam(required = false) String sortDirection) {
		FilterDto filtro = createFilter(page, size, owned, title, casting, fromYear, toYear, format, genre, false, sort,
				sortDirection);
	    String file = seriesExportService.toMarkdown(service.findAllBy(filtro).getContent());
	    

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.md")
	            .contentType(MediaType.TEXT_MARKDOWN)
	            .body(file);
	}
	
	@GetMapping("/export/pdf")
	public ResponseEntity<byte[]> exportPdf(  @RequestParam Integer page,
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
	        @RequestParam(required = false) String sortDirection) throws IOException {
		FilterDto filtro = createFilter(page, size, owned, title, casting, fromYear, toYear, format, genre, false, sort,
				sortDirection);
		byte[] file = seriesExportService.toPdf(service.findAllBy(filtro).getContent());

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.pdf")
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(file);
	}
}
