package lugus.api.films;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.FilmDto;
import lugus.dto.films.FilmStatsDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.exception.LugusNotFoundException;
import lugus.export.FilmExportService;
import lugus.mapper.films.FilmMapper;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.model.values.Formato;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.films.DwFotoService;
import lugus.service.films.DwFotoServiceI;
import lugus.service.films.PeliculaService;
import lugus.service.groups.GroupsService;
import lugus.service.people.InsertPersonalDataService;

@RestController
@ResponseBody
@RequestMapping("/v1/api/films")
@RequiredArgsConstructor
public class FilmApiController {

	private final PeliculaService service;

	private final FilmMapper mapper;

	private final GroupsService groupsService;
	private final LocationService locService;
	private final SourceService sourceService;
	private final InsertPersonalDataService insertImdbService;
	private final FilmExportService filmExportService;

	@GetMapping("/{id}")
	FilmDto one(@PathVariable Integer id) throws LugusNotFoundException {
		Pelicula film = service.findById(id).orElse(null);
		if (film != null)
			return mapper.mapToFilmDTO(film);
		return null;
	}

	@PatchMapping("/{id}/trailer")
	public FilmDto updateTrailer(@PathVariable Integer id, @RequestBody TrailerUpdateRequest req) {
		return mapper.mapToFilmDTO(service.updateTrailer(id, req.trailerUrl()));
	}

	@PutMapping("/{id}")
	public FilmDto update(@PathVariable Integer id, @RequestBody FilmDto dto) throws IOException, URISyntaxException {
		return mapper.mapToFilmDTO(service.update(id, dto));
	}

	@GetMapping("/page")
	public Page<FilmDto> getAllFilms(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) Boolean owned, @RequestParam(required = false) String title,
			@RequestParam(required = false) String casting, @RequestParam(required = false) Integer fromYear,
			@RequestParam(required = false) Integer toYear, @RequestParam(required = false) String format,
			@RequestParam(required = false) String genre, @RequestParam(required = false) Boolean pack,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String sortDirection) {
		FiltrosDto filtro = crearFiltro(page, size, owned, title, casting, fromYear, toYear, format, genre, pack, sort,
				sortDirection);
		return service.findAllBy(filtro).map(mapper::mapToFilmDTO);
	}

	@PostMapping("new")
	ResponseEntity<Object> save(@RequestBody FilmDto dto, Authentication auth)
			throws LugusNotFoundException, IOException, URISyntaxException {
		Pelicula film = mapper.mapToFilm(dto);
		Location loc = findLocation(dto);
		film.setLocation(loc);
		film.calcularCodigo();
		film.setTsAlta(Instant.now());
		film.setUsrAlta(auth.getName());
		Pelicula saved = service.save(film);
		if (dto.getCoverSrc() != null && !dto.getCoverSrc().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			final int sourceId = service.calcularIdSource(dto.getCoverSrc());
			Optional<Source> sourceObj = sourceService.findById(sourceId);
			PeliculaFoto pf = new PeliculaFoto();
			pf.setUrl(dto.getCoverSrc());
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setFoto(dwFotoService.descargar(sourceId, dto.getCoverSrc()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			saved = service.save(saved);
		}

		if (dto.getImdbId() != null && !dto.getImdbId().isBlank()) {
			insertImdbService.insert(saved.getId(), dto.getImdbId());
		}

		return ResponseEntity.ok().build();
	}

	private Location findLocation(FilmDto dto) {
		Location loc = null;
		if (dto.getLocation() != null && !dto.getLocation().isBlank()) {
			loc = locService.findById(dto.getLocation())
					.orElseThrow(() -> new LugusNotFoundException(dto.getLocation()));
		}
		return loc;
	}

	@GetMapping("/wanted")
	List<PeliculaCreateDto> wanted() throws LugusNotFoundException {
		Page<Pelicula> page = service.wanted();
		List<PeliculaCreateDto> result = new ArrayList<PeliculaCreateDto>();
		for (Pelicula p : page.getContent()) {
			result.add(mapper.mapToDTO(p));
		}
		return result;
	}

	@GetMapping("/random/wanted")
	PeliculaCreateDto randomw() throws LugusNotFoundException {
		Page<Pelicula> page = service.wanted();
		int number = (int) (Math.random() * page.getNumberOfElements()) + 1;
		return mapper.mapToDTO(page.getContent().get(number));
	}

	@GetMapping(value = "/ultimas", produces = "application/json;charset=UTF-8")
	public List<FilmDto> ultimas() throws LugusNotFoundException {

		return service.findForHome().getContent().stream().map(mapper::mapToFilmDTO).toList();
	}

	@GetMapping(value = "/stats", produces = "application/json;charset=UTF-8")
	public FilmStatsDto getStats() {
		FilmStatsDto stats = new FilmStatsDto();
		stats.setTotalFilms(service.contarTodasCompradas());
		stats.setRecentFilms(service.addedInLastDays(30));
		stats.setIncompleteGroups(groupsService.incompletedGroups());
		stats.setCompleteGroups((int) (groupsService.count() - groupsService.incompletedGroups()));
		return stats;
	}

	@GetMapping("/export/ods")
	public ResponseEntity<byte[]> exportOds(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) Boolean owned, @RequestParam(required = false) String title,
			@RequestParam(required = false) String casting, @RequestParam(required = false) Integer fromYear,
			@RequestParam(required = false) Integer toYear, @RequestParam(required = false) String format,
			@RequestParam(required = false) String genre, @RequestParam(required = false) Boolean pack,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String sortDirection)
			throws IOException {
		FiltrosDto filtro = crearFiltro(page, size, owned, title, casting, fromYear, toYear, format, genre, pack, sort,
				sortDirection);
		byte[] file = filmExportService.toOds(service.findAllBy(filtro).getContent());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.ods")
				.contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet")).body(file);
	}

	private FiltrosDto crearFiltro(Integer page, Integer size, Boolean owned, String title, String casting,
			Integer fromYear, Integer toYear, String format, String genre, Boolean pack, String sort,
			String sortDirection) {
		FiltrosDto filtro = new FiltrosDto();
		if (page != null) {
			filtro.setPagina(Optional.of(page));
		}
		if (title != null) {
			filtro.setTitulo(title);
		}
		if (casting != null) {
			filtro.setActor(casting);
			filtro.setDirector(casting);
		}
		if (fromYear != null) {
			filtro.setFromAnyo(fromYear);
		}
		if (toYear != null) {
			filtro.setToAnyo(toYear);
		}
		if (format != null) {
			filtro.setFormato((int) Formato.getByName(format).getId());
		}
		if (genre != null) {
			filtro.setGenero(genre);
		}
		if (pack != null) {
			filtro.setPack(pack);
		}

		if (sort != null) {
			filtro.setOrden(Optional.of(sort));
		}
		if (sortDirection != null) {
			filtro.setDireccion(Optional.of(sortDirection));
		}
		if (owned != null) {
			filtro.setComprado(owned);
		}
		int sizeAux = size;
		if (size == null || size <= 0) {
			sizeAux = Integer.MAX_VALUE;
		}
		filtro.setPageSize(sizeAux);
		return filtro;
	}

	@SuppressWarnings("null")
	@GetMapping("/export/md")
	public ResponseEntity<String> exportMarkdown(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) Boolean owned, @RequestParam(required = false) String title,
			@RequestParam(required = false) String casting, @RequestParam(required = false) Integer fromYear,
			@RequestParam(required = false) Integer toYear, @RequestParam(required = false) String format,
			@RequestParam(required = false) String genre, @RequestParam(required = false) Boolean pack,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String sortDirection) {
		FiltrosDto filtro = crearFiltro(page, size, owned, title, casting, fromYear, toYear, format, genre, pack, sort,
				sortDirection);
		String file = filmExportService.toMarkdown(service.findAllBy(filtro).getContent());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.md")
				.contentType(MediaType.TEXT_MARKDOWN).body(file);
	}

	@SuppressWarnings("null")
	@GetMapping("/export/pdf")
	public ResponseEntity<byte[]> exportPdf(@RequestParam Integer page, @RequestParam Integer size,
			@RequestParam(required = false) Boolean owned, @RequestParam(required = false) String title,
			@RequestParam(required = false) String casting, @RequestParam(required = false) Integer fromYear,
			@RequestParam(required = false) Integer toYear, @RequestParam(required = false) String format,
			@RequestParam(required = false) String genre, @RequestParam(required = false) Boolean pack,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String sortDirection)
			throws IOException {
		FiltrosDto filtro = crearFiltro(page, size, owned, title, casting, fromYear, toYear, format, genre, pack, sort,
				sortDirection);
		byte[] file = filmExportService.toPdf(service.findAllBy(filtro).getContent());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.pdf")
				.contentType(MediaType.APPLICATION_PDF).body(file);
	}

}
