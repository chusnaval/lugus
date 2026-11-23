package lugus.api.films;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.dto.core.CountryCode;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.FilmCategoryDto;
import lugus.dto.films.FilmDto;
import lugus.dto.films.FilmGenreDto;
import lugus.dto.films.FilmStatsDto;
import lugus.exception.LugusNotFoundException;
import lugus.export.FilmExportService;
import lugus.mapper.films.FilmMapper;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.model.films.FilmImage;
import lugus.model.imdb.OmdbConstants;
import lugus.model.values.Category;
import lugus.model.values.Format;
import lugus.model.values.Genre;
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
@RequestMapping("/v1/api/films")
public class FilmApiController {

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";
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
	private static final String FILE_NAME = "peliculas";

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
		Film film = service.findById(id).orElse(null);
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
			String url = OmdbConstants.OMDB_API_URL + dto.getImdbId() + OmdbConstants.API_KEY_PARAM + apiKey
					+ OmdbConstants.PLOT_PARAM;
			final RestTemplate rest = new RestTemplate();
			Map<String, Object> json = rest.getForObject(url, Map.class);
			cacheService.saveToCache(dto.getImdbId(), json);
		}
		return mapper.mapToFilmDTO(service.update(id, dto, apiKey), null);
	}

	@PostMapping("/{id}/toggle")
	public ResponseEntity<Object> toggleOwned(@PathVariable Integer id, Authentication auth) {
		usuarioPeliculaService.toggleOwned(auth.getName(), id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/fav")
	public ResponseEntity<Object> toggleFav(@PathVariable Integer id, Authentication auth) {
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
		Film film = mapper.mapToFilm(dto);

		for (Edition edto : film.getEditions()) {

			if (edto.getLocation() != null && edto.getLocation().getCode() != null
					&& !edto.getLocation().getCode().isEmpty()) {
				Location loc = locService.findById(edto.getLocation().getCode())
						.orElseThrow(() -> new LugusNotFoundException(edto.getLocation().getCode()));
				edto.setLocation(loc);
			} else {
				edto.setLocation(null);
			}

			// TIMESTAMPS
			edto.setTsCreation(Instant.now());
			edto.setTsModif(Instant.now());
			edto.setUsrCreation(auth.getName());
			edto.setUsrModif(auth.getName());

			if (edto.isPurchased()) {
				edto.setTsPurchase(Instant.now());
			}

			edto.setCode(edicionService.calculateCompleteCode(edto.getId(), film));

		}

		Film saved = service.save(film);
		if (dto.getCoverSrc() != null && !dto.getCoverSrc().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			final int sourceId = sourceService.calcularIdSource(dto.getCoverSrc());
			Optional<Source> sourceObj = sourceService.findById(sourceId);
			FilmImage pf = new FilmImage();
			pf.setUrl(dto.getCoverSrc());
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setContent(dwFotoService.descargar(sourceId, dto.getCoverSrc()));
			pf.setCover(true);

			saved.addImage(pf);
			service.save(saved);
		}

		if (dto.getImdbId() != null && !dto.getImdbId().isBlank()) {
			insertImdbService.insert(saved.getId(), dto.getImdbId());

			titlesService.findByImdb_Id(dto.getImdbId()).ifPresent(title -> {
				title.setTitle(saved.getTitle());
				title.setYear(saved.getYear());
				title.setType(TitleType.MOVIE);
				title.setPosterUrl(dto.getCoverSrc());
				title.setFilm(saved);
				titlesService.save(title);
			});
			var cached = cacheService.getFromCache(dto.getImdbId());

			if (cached == null) {
				String url = OmdbConstants.OMDB_API_URL + dto.getImdbId() + OmdbConstants.API_KEY_PARAM + apiKey
						+ OmdbConstants.PLOT_PARAM;
				final RestTemplate rest = new RestTemplate();
				Map<String, Object> json = rest.getForObject(url, Map.class);

				cacheService.saveToCache(dto.getImdbId(), json);
			}
			cached = cacheService.getFromCache(dto.getImdbId());
			JsonNode node = jsonMapper.valueToTree(cached.getJson());
			String country = node.get(OmdbConstants.COUNTRY_PLOT).asText();

			// puede tener valor o no
			// o tener uno o varios países separados por coma
			List<String> values = getCountries(country);
			// los guardamosen un campo separados por coma
			saved.setCountry(String.join(",", values));
			saved.setSynopsis(node.get(OmdbConstants.FIELD_PLOT).asText());

			service.save(saved);

			String posterUrl = node.get(OmdbConstants.POSTER).asText();
			if (dto.getCoverSrc().isEmpty() || dto.getCoverSrc() == null) {
				dto.setCoverSrc(posterUrl);
				final DwFotoServiceI dwFotoService = new DwFotoService();
				final int sourceId = sourceService.calcularIdSource(dto.getCoverSrc());
				Optional<Source> sourceObj = sourceService.findById(sourceId);
				FilmImage pf = new FilmImage();
				pf.setUrl(dto.getCoverSrc());
				if (sourceObj.isPresent()) {
					pf.setSource(sourceObj.get());
				}
				pf.setContent(dwFotoService.descargar(sourceId, dto.getCoverSrc()));
				pf.setCover(true);

				saved.addImage(pf);
				service.save(saved);
			}
			
			getDurationAndUpdate(saved, node);
			
		}

		return ResponseEntity.ok().build();
	}

	private void getDurationAndUpdate(Film saved, JsonNode node) {
		try {
			Integer duration = node.get(OmdbConstants.DURATION).asInt();
			saved.setDuration(duration);
			service.save(saved);
		}catch(Exception e) {
			// no pasa nada, no tiene duración
		}
	}

	private List<String> getCountries(String country) {
		List<String> values = new ArrayList<>();
		if (country != null) {
			String[] countries = country.split(",");
			for (int i = 0; i < countries.length; i++) {
				values.add(CountryCode.fromString(countries[i]).getCode());
			}
		}
		return values;
	}

	@GetMapping("/wanted")
	List<FilmDto> wanted() throws LugusNotFoundException {
		Page<Film> page = service.wanted();
		List<FilmDto> result = new ArrayList<FilmDto>();
		for (Film p : page.getContent()) {
			result.add(mapper.mapToFilmDTO(p));
		}
		return result;
	}

	@GetMapping("/random/wanted")
	FilmDto randomw() throws LugusNotFoundException {
		Page<Film> page = service.wanted();
		Random random = new Random(Instant.now().toEpochMilli());
		int number = (random.nextInt() * page.getNumberOfElements()) + 1;
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

		getBasicStats(stats);
		getCategoryNumbers(stats);
		getGenreNumbers(stats);

		return stats;
	}

	private void getBasicStats(FilmStatsDto stats) {
		stats.setTotalFilms(edicionService.countAllPurchased());
		stats.setNotOwned(edicionService.countNotPurchased());
		stats.setRecentFilms(edicionService.addedInLastDays(30));
		stats.setIncompleteGroups(groupsService.incompletedGroups());
		stats.setCompleteGroups((int) (groupsService.count() - groupsService.incompletedGroups()));
		stats.setVhs(edicionService.countByFormat(Format.VHS));
		stats.setDvd(edicionService.countByFormat(Format.DVD));
		stats.setBluray(edicionService.countByFormat(Format.BLURAY));
		stats.setUhd(edicionService.countByFormat(Format.ULTRAHD));
		stats.setDigital(edicionService.countByFormat(Format.DIGITAL));
	}

	private void getGenreNumbers(FilmStatsDto stats) {
		Map<Object, Integer> generos = service.countByGenre();
		stats.setNumerosPorGenero(new FilmGenreDto());
		stats.setAnimacion(getNumberFromMap(generos, Genre.ANIMACION));
		stats.setAnime(getNumberFromMap(generos, Genre.ANIME));
		stats.setInfantil(getNumberFromMap(generos, Genre.INFANTIL));
		stats.setMusical(getNumberFromMap(generos, Genre.MUSICAL));
		stats.setNavidena(getNumberFromMap(generos, Genre.NAVIDENA));
		stats.setDrama(getNumberFromMap(generos, Genre.DRAMA));
		stats.setRomantica(getNumberFromMap(generos, Genre.ROMANTICA));
		stats.setComedia(getNumberFromMap(generos, Genre.COMEDIA));
		stats.setCienciaFiccion(getNumberFromMap(generos, Genre.CIENCIA_FICCION));
		stats.setAccion(getNumberFromMap(generos, Genre.ACCION));
		stats.setAventura(getNumberFromMap(generos, Genre.AVENTURA));
		stats.setFantasia(getNumberFromMap(generos, Genre.FANTASIA));
		stats.setThriller(getNumberFromMap(generos, Genre.THRILLER));
		stats.setMisterio(getNumberFromMap(generos, Genre.MISTERIO));
		stats.setCrimen(getNumberFromMap(generos, Genre.CRIMEN));
		stats.setTerror(getNumberFromMap(generos, Genre.TERROR));
		stats.setBelico(getNumberFromMap(generos, Genre.BELICO));
		stats.setWestern(getNumberFromMap(generos, Genre.WESTERN));
		stats.setDocumental(getNumberFromMap(generos, Genre.DOCUMENTAL));
	}

	private int getNumberFromMap(Map<Object, Integer> generos, Genre genero) {
		return generos.get(genero.getCodigo()) != null ? generos.get(genero.getCodigo()) : 0;
	}

	private int getNumberFromMap(Map<Object, Integer> categorias, Category categoria) {
		return categorias.get(categoria) != null ? categorias.get(categoria) : 0;
	}

	private void getCategoryNumbers(FilmStatsDto stats) {
		Map<Object, Integer> categorias = service.countByCategory();
		stats.setNumerosPorCategoria(new FilmCategoryDto());
		stats.setArteEntretenimiento(getNumberFromMap(categorias, Category.ARTE_ENTRETENIMIENTO));
		stats.setLiteraturaNarrativa(getNumberFromMap(categorias, Category.LITERATURA_NARRATIVA));
		stats.setCienciaFiccionCategory(getNumberFromMap(categorias, Category.CIENCIA_FICCION));
		stats.setAccionCategory(getNumberFromMap(categorias, Category.ACCION));
		stats.setMisterioCategory(getNumberFromMap(categorias, Category.MISTERIO));
		stats.setTerrorCategory(getNumberFromMap(categorias, Category.TERROR));
		stats.setConflicto(getNumberFromMap(categorias, Category.CONFLICTO));
		stats.setDocumentalCategory(getNumberFromMap(categorias, Category.DOCUMENTAL));
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
			if(Format.getById(Short.valueOf(format)) == null) {
				throw new IllegalArgumentException("Formato no válido: " + format);
			}
			filtro.setFormato((int) Format.getById(Short.valueOf(format)).getId());
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

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FILE_NAME + ".md")
				.contentType(MediaType.TEXT_MARKDOWN).body(file);
	}

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

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FILE_NAME + ".pdf")
				.contentType(MediaType.APPLICATION_PDF).body(file);
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

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FILE_NAME + ".ods")
				.contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet")).body(file);
	}
}
