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
import lugus.dto.films.FilmCategoryDto;
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
import lugus.model.imdb.OmdbConstants;
import lugus.model.values.Categoria;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
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
			String url = OmdbConstants.OMDB_API_URL + dto.getImdbId() + OmdbConstants.API_KEY_PARAM + apiKey
					+ OmdbConstants.PLOT_PARAM;
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

			if (edto.getLocation() != null && edto.getLocation().getCodigo() != null
					&& !edto.getLocation().getCodigo().isEmpty()) {
				Location loc = locService.findById(edto.getLocation().getCodigo())
						.orElseThrow(() -> new LugusNotFoundException(edto.getLocation().getCodigo()));
				edto.setLocation(loc);
			} else {
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

			edto.setCodigo(edicionService.calculateCompleteCode(edto.getId(), film));

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
				String url = OmdbConstants.OMDB_API_URL + dto.getImdbId() + OmdbConstants.API_KEY_PARAM + apiKey
						+ OmdbConstants.PLOT_PARAM;
				final RestTemplate rest = new RestTemplate();
				Map<String, Object> json = rest.getForObject(url, Map.class);
				String posterUrl = (String) json.get(OmdbConstants.POSTER);
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
			saved.setSynopsis((String) node.get(OmdbConstants.FIELD_PLOT).asText());

			service.save(saved);

			String posterUrl = node.get(OmdbConstants.POSTER).asText();
			if (dto.getCoverSrc().isEmpty() || dto.getCoverSrc() == null) {
				dto.setCoverSrc(posterUrl);
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
		}

		return ResponseEntity.ok().build();
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

		getBasicStats(stats);
		getCategoryNumbers(stats);
		getGenreNumbers(stats);

		return stats;
	}

	private void getBasicStats(FilmStatsDto stats) {
		stats.setTotalFilms(edicionService.contarTodasCompradas());
		stats.setNotOwned(edicionService.contarNoCompradas());
		stats.setRecentFilms(edicionService.addedInLastDays(30));
		stats.setIncompleteGroups(groupsService.incompletedGroups());
		stats.setCompleteGroups((int) (groupsService.count() - groupsService.incompletedGroups()));
		stats.setVhs(edicionService.contarPorFormato(Formato.VHS));
		stats.setDvd(edicionService.contarPorFormato(Formato.DVD));
		stats.setBluray(edicionService.contarPorFormato(Formato.BLURAY));
		stats.setUhd(edicionService.contarPorFormato(Formato.ULTRAHD));
		stats.setDigital(edicionService.contarPorFormato(Formato.DIGITAL));
	}

	private void getGenreNumbers(FilmStatsDto stats) {
		Map<Object, Integer> generos = service.contarPorGenero();
		stats.setNumerosPorGenero(new FilmGenreDto());
		stats.setAnimacion(getNumberFromMap(generos, Genero.ANIMACION));
		stats.setAnime(getNumberFromMap(generos, Genero.ANIME));
		stats.setInfantil(getNumberFromMap(generos, Genero.INFANTIL));
		stats.setMusical(getNumberFromMap(generos, Genero.MUSICAL));
		stats.setNavidena(getNumberFromMap(generos, Genero.NAVIDENA));
		stats.setDrama(getNumberFromMap(generos, Genero.DRAMA));
		stats.setRomantica(getNumberFromMap(generos, Genero.ROMANTICA));
		stats.setComedia(getNumberFromMap(generos, Genero.COMEDIA));
		stats.setCienciaFiccion(getNumberFromMap(generos, Genero.CIENCIA_FICCION));
		stats.setAccion(getNumberFromMap(generos, Genero.ACCION));
		stats.setAventura(getNumberFromMap(generos, Genero.AVENTURA));
		stats.setFantasia(getNumberFromMap(generos, Genero.FANTASIA));
		stats.setThriller(getNumberFromMap(generos, Genero.THRILLER));
		stats.setMisterio(getNumberFromMap(generos, Genero.MISTERIO));
		stats.setCrimen(getNumberFromMap(generos, Genero.CRIMEN));
		stats.setTerror(getNumberFromMap(generos, Genero.TERROR));
		stats.setBelico(getNumberFromMap(generos, Genero.BELICO));
		stats.setWestern(getNumberFromMap(generos, Genero.WESTERN));
		stats.setDocumental(getNumberFromMap(generos, Genero.DOCUMENTAL));
	}

	private int getNumberFromMap(Map<Object, Integer> generos, Genero genero) {
		return generos.get(genero.getCodigo()) != null ? generos.get(genero.getCodigo()) : 0;
	}

	private int getNumberFromMap(Map<Object, Integer> categorias, Categoria categoria) {
		return categorias.get(categoria) != null ? categorias.get(categoria) : 0;
	}

	private void getCategoryNumbers(FilmStatsDto stats) {
		Map<Object, Integer> categorias = service.contarPorCategoria();
		stats.setNumerosPorCategoria(new FilmCategoryDto());
		stats.setArteEntretenimiento(getNumberFromMap(categorias, Categoria.ARTE_ENTRETENIMIENTO));
		stats.setLiteraturaNarrativa(getNumberFromMap(categorias, Categoria.LITERATURA_NARRATIVA));
		stats.setCienciaFiccionCategory(getNumberFromMap(categorias, Categoria.CIENCIA_FICCION));
		stats.setAccionCategory(getNumberFromMap(categorias, Categoria.ACCION));
		stats.setMisterioCategory(getNumberFromMap(categorias, Categoria.MISTERIO));
		stats.setTerrorCategory(getNumberFromMap(categorias, Categoria.TERROR));
		stats.setConflicto(getNumberFromMap(categorias, Categoria.CONFLICTO));
		stats.setDocumentalCategory(getNumberFromMap(categorias, Categoria.DOCUMENTAL));
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
			filtro.setFormato((int) Formato.getById(Short.valueOf(format)).getId());
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
