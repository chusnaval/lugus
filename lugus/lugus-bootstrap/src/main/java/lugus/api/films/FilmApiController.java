package lugus.api.films;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.dto.core.CountryCode;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.EditionDto;
import lugus.dto.films.FilmDto;
import lugus.dto.films.FilmGenreDto;
import lugus.dto.films.FilmStatsDto;
import lugus.exception.LugusNotFoundException;
import lugus.export.FilmExportService;
import lugus.mapper.films.FilmMapper;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.films.Edicion;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.model.values.Categoria;
import lugus.model.values.Formato;
import lugus.model.values.TitleType;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.films.DwFotoService;
import lugus.service.films.DwFotoServiceI;
import lugus.service.films.EdicionService;
import lugus.service.films.PeliculaService;
import lugus.service.films.PeliculasUsuarioService;
import lugus.service.groups.GroupsService;
import lugus.service.imdb.OmdbCacheService;
import lugus.service.people.InsertPersonalDataService;
import lugus.service.titles.TitlesService;

@RestController
@ResponseBody
@RequestMapping("/v1/api/films")
public class FilmApiController {

	private final PeliculaService service;
	private final EdicionService edicionService;
	private final FilmMapper mapper;
	private final GroupsService groupsService;
	private final LocationService locService;
	private final SourceService sourceService;
	private final InsertPersonalDataService insertImdbService;
	private final FilmExportService filmExportService;
	private final TitlesService titlesService;
	private final PeliculasUsuarioService usuarioPeliculaService;
	private final String apiKey;
	private final OmdbCacheService cacheService;
	private final ObjectMapper jsonMapper;

	@Autowired
	public FilmApiController(@Value("${omdb.api.key}") String apiKey, PeliculaService service,
			EdicionService edicionService, FilmMapper mapper, GroupsService groupsService, LocationService locService,
			SourceService sourceService, InsertPersonalDataService insertImdbService,
			FilmExportService filmExportService, TitlesService titlesService,
			PeliculasUsuarioService usuarioPeliculaService, OmdbCacheService cacheService, ObjectMapper jsonMapper) {
		super();
		this.service = service;
		this.edicionService = edicionService;
		this.mapper = mapper;
		this.groupsService = groupsService;
		this.locService = locService;
		this.sourceService = sourceService;
		this.insertImdbService = insertImdbService;
		this.filmExportService = filmExportService;
		this.titlesService = titlesService;
		this.usuarioPeliculaService = usuarioPeliculaService;
		this.apiKey = apiKey;
		this.cacheService = cacheService;
		this.jsonMapper = jsonMapper;
	}

	@GetMapping("/{id}")
	FilmDto one(@PathVariable Integer id, Authentication auth) throws LugusNotFoundException {
		Pelicula film = service.findById(id).orElse(null);
		if (film != null)
			return mapper.mapToFilmDTO(film, auth.getName());
		return null;
	}

	@PatchMapping("/{id}/trailer")
	public FilmDto updateTrailer(@PathVariable Integer id, @RequestBody TrailerUpdateRequest req) {
		return mapper.mapToFilmDTO(service.updateTrailer(id, req.trailerUrl()), null);
	}

	@PutMapping("/{id}")
	public FilmDto update(@PathVariable Integer id, @RequestBody FilmDto dto) throws IOException, URISyntaxException {
		var cached = cacheService.getFromCache(dto.getImdbId());

		if (cached == null) {
			String url = "https://www.omdbapi.com/?i=" + dto.getImdbId() + "&apikey=" + apiKey + "&plot=full";
			final RestTemplate rest = new RestTemplate();
			Map<String, Object> json = rest.getForObject(url, Map.class);
			cacheService.saveToCache(dto.getImdbId(), json);
		}
		return mapper.mapToFilmDTO(service.update(id, dto, apiKey), null);
	}

	@PostMapping("/{id}/toggle")
	public ResponseEntity<?> toggleOwned(@PathVariable Integer id, Authentication auth) {
		usuarioPeliculaService.toggleOwned(auth.getName(), id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/fav")
	public ResponseEntity<?> toggleFav(@PathVariable Integer id, Authentication auth) {
		usuarioPeliculaService.toggleFav(auth.getName(), id);
		return ResponseEntity.ok().build();
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

		for (Edicion edto : film.getEditions()) {

			if(edto.getLocation()!=null && edto.getLocation().getCodigo() != null
					&& !edto.getLocation().getCodigo().isEmpty()) {
				Location loc = locService.findById(edto.getLocation().getCodigo())
						.orElseThrow(() -> new LugusNotFoundException(edto.getLocation().getCodigo()));
				edto.setLocation(loc);
			}else {
				edto.setLocation(null);
			}

			// TIMESTAMPS
			edto.setTsAlta(Instant.now());
			edto.setTsModif(Instant.now());
			edto.setUsrAlta(auth.getName());
			edto.setUsrModif(auth.getName());

			if (edto.isComprado()) {
				edto.setTsCompra(Instant.now());
			}

			// CÓDIGO
			edto.calcularCodigoInicial(film.getTituloGest(), film.getGenero().getCodigo(), film.getAnyo());
			edicionService.calculateCodeSuffix(edto);

		}

		Pelicula saved = service.save(film);
		if (dto.getCoverSrc() != null && !dto.getCoverSrc().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			final int sourceId = sourceService.calcularIdSource(dto.getCoverSrc());
			Optional<Source> sourceObj = sourceService.findById(sourceId);
			PeliculaFoto pf = new PeliculaFoto();
			pf.setUrl(dto.getCoverSrc());
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setFoto(dwFotoService.descargar(sourceId, dto.getCoverSrc()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			service.save(saved);
		}

		if (dto.getImdbId() != null && !dto.getImdbId().isBlank()) {
			insertImdbService.insert(saved.getId(), dto.getImdbId());

			titlesService.findByImdb_Id(dto.getImdbId()).ifPresent(title -> {
				title.setTitle(saved.getTitulo());
				title.setYear(saved.getAnyo());
				title.setType(TitleType.MOVIE);
				title.setPosterUrl(dto.getCoverSrc());
				title.setPelicula(saved);
				titlesService.save(title);
			});
			var cached = cacheService.getFromCache(dto.getImdbId());
			
			if (cached == null) {
				String url = "https://www.omdbapi.com/?i=" + dto.getImdbId() + "&apikey=" + apiKey + "&plot=full";
				final RestTemplate rest = new RestTemplate();
				Map<String, Object> json = rest.getForObject(url, Map.class);
				cacheService.saveToCache(dto.getImdbId(), json);
			}
			cached = cacheService.getFromCache(dto.getImdbId());
			JsonNode node = jsonMapper.valueToTree(cached.getJson());
			String country = node.get("Country").asText();
			// puede tener valor o no
			// o tener uno o varios países separados por coma
			List<String> values = new ArrayList<>();
			if (country != null) {
				String[] countries = country.split(",");
				for (int i = 0; i < countries.length; i++) {
					values.add(CountryCode.fromString(countries[i]).getCode());
				}
			}
			
			// los guardamosen un campo separados por coma
			saved.setCountry(String.join(",", values));
			saved.setSynopsis((String) node.get("Plot").asText());
			service.save(saved);
		}



		return ResponseEntity.ok().build();
	}

	@GetMapping("/wanted")
	List<FilmDto> wanted() throws LugusNotFoundException {
		Page<Pelicula> page = service.wanted();
		List<FilmDto> result = new ArrayList<FilmDto>();
		for (Pelicula p : page.getContent()) {
			result.add(mapper.mapToFilmDTO(p));
		}
		return result;
	}

	@GetMapping("/random/wanted")
	FilmDto randomw() throws LugusNotFoundException {
		Page<Pelicula> page = service.wanted();
		int number = (int) (Math.random() * page.getNumberOfElements()) + 1;
		return mapper.mapToFilmDTO(page.getContent().get(number));
	}

	@GetMapping(value = "/ultimas", produces = "application/json;charset=UTF-8")
	public List<FilmDto> ultimas() throws LugusNotFoundException {

		return service.findForHome().getContent().stream().map(mapper::mapToFilmDTO).toList();
	}

	@GetMapping(value = "/ultimas/{genero}", produces = "application/json;charset=UTF-8")
	public List<FilmDto> ultimas(@PathVariable String genero) throws LugusNotFoundException {

		return service.lastForGenre(genero).getContent().stream().map(mapper::mapToFilmDTO).toList();
	}

	@GetMapping(value = "/stats", produces = "application/json;charset=UTF-8")
	public FilmStatsDto getStats() {
		FilmStatsDto stats = new FilmStatsDto();
		stats.setTotalFilms(edicionService.contarTodasCompradas());
		stats.setRecentFilms(edicionService.addedInLastDays(30));
		stats.setIncompleteGroups(groupsService.incompletedGroups());
		stats.setCompleteGroups((int) (groupsService.count() - groupsService.incompletedGroups()));
		stats.setVhs(edicionService.contarPorFormato(Formato.VHS));
		stats.setDvd(edicionService.contarPorFormato(Formato.DVD));
		stats.setBluray(edicionService.contarPorFormato(Formato.BLURAY));
		stats.setUhd(edicionService.contarPorFormato(Formato.ULTRAHD));
		stats.setDigital(edicionService.contarPorFormato(Formato.DIGITAL));
		stats.setNotOwned(edicionService.contarNoCompradas());

		Map<Object, Integer> categorias = service.contarPorCategoria();
		stats.setGenerosPorCategoria(new FilmGenreDto());
		stats.getGenerosPorCategoria().setArteEntretenimiento(categorias.get(Categoria.ARTE_ENTRETENIMIENTO));
		stats.getGenerosPorCategoria().setLiteraturaNarrativa(categorias.get(Categoria.LITERATURA_NARRATIVA));
		stats.getGenerosPorCategoria().setCienciaFiccion(categorias.get(Categoria.CIENCIA_FICCION));
		stats.getGenerosPorCategoria().setAccion(categorias.get(Categoria.ACCION));
		stats.getGenerosPorCategoria().setMisterio(categorias.get(Categoria.MISTERIO));
		stats.getGenerosPorCategoria().setTerror(categorias.get(Categoria.TERROR));
		stats.getGenerosPorCategoria().setConflicto(categorias.get(Categoria.CONFLICTO));
		stats.getGenerosPorCategoria().setDocumental(categorias.get(Categoria.DOCUMENTAL));

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
