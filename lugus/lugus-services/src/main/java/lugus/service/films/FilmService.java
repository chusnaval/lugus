package lugus.service.films;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.CountryCode;
import lugus.dto.core.FilterDto;
import lugus.dto.films.EditionDto;
import lugus.dto.films.FilmDto;
import lugus.dto.filters.CoversFilter;
import lugus.dto.filters.CoversSpecs;
import lugus.infrastructure.repository.films.FilmSpecification;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.model.films.FilmImage;
import lugus.model.values.Format;
import lugus.model.values.Genre;
import lugus.repository.films.FilmRepository;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.service.imdb.OmdbCacheService;
import lugus.service.titles.TitlesService;

@Service
@RequiredArgsConstructor
public class FilmService {

	private static final String MGMT_TITLE = "mgmtTitle";

	private static final Logger logger = LogManager.getLogger();

	private static final int NUM_ELEMENTS_PER_HOME = 5;
	private final FilmRepository filmRepo;
	private final LocationService locService;
	private final SourceService sourceService;
	private final TitlesService titlesService;
	private final ImdbTitleBasicsService imdbTitleBasicsService;
	private final OmdbCacheService cacheService;
	private final ObjectMapper jsonMapper;
	private final EditionService edicionService;

	public List<Film> findAll() {
		return filmRepo.findAll();
	}

	/**
	 * Return a film by its id
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Film> findById(Integer id) {
		if (id != null) {
			return filmRepo.findById(id);
		}
		return Optional.empty();
	}

	public Film updateTrailer(Integer id, String url) {
		Film p = filmRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));

		p.setTrailerUrl(url);
		return filmRepo.save(p);
	}

	@Transactional(rollbackFor = {java.io.IOException.class, java.net.URISyntaxException.class})
	public Film update(final Integer id, final FilmDto dto, final String apiKey)
			throws IOException, URISyntaxException {

		Film p = filmRepo.findById(id).orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));

		boolean isDiffInCode = p.isDiffInCode(dto.getTitleMgmt(), dto.getYear(), dto.getGenreCode());

		p.setTitle(dto.getTitle());
		p.setMgmtTitle(dto.getTitleMgmt());
		p.setYear(dto.getYear());
		p.setImdbId(dto.getImdbId());
		p.setGenre(Genre.getById(dto.getGenreCode()));
		p.setDuration(dto.getDuration());

		updateEditions(dto, p, isDiffInCode);

		addCover(p, dto.getCoverSrc());
		Film saved = filmRepo.save(p);

		// Sincronizar Title si existe
		titlesService.findByFilmId(id).ifPresent(title -> {
			title.setTitle(p.getTitle());
			title.setYear(p.getYear());
			title.setUrlCover(dto.getCoverSrc());

			// actualizar IMDB si ha cambiado
			if (dto.getImdbId() != null) {
				imdbTitleBasicsService.findByTconst(dto.getImdbId()).ifPresent(title::setImdb);
			}

			titlesService.save(title);
		});

		if (!dto.getImdbId().equals(p.getImdbId())) {

			var cached = cacheService.getFromCache(dto.getImdbId());
			if (cached != null) {

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
				saved.setSynopsis(node.get("Plot").asText());
				save(saved);
			}
		}
		return p;
	}

	private void updateEditions(final FilmDto dto, Film p, boolean updateCode) {
		for (EditionDto edto : dto.getEditions()) {
			Optional<Edition> optEditional = edicionService.findById(edto.getId());
			if (optEditional.isPresent()) {
				Edition edition = optEditional.get();
				Optional<Location> loc = locService.findById(edto.getLocation());
				edition.setTsModif(Instant.now());
				edition.setFormat(Format.getById(Short.valueOf(edto.getFormatCode())));
				if (loc.isPresent()) {
					edition.setLocation(loc.get());
				}
				edition.setCode(edto.getMgmtCode());
				edition.setPurchased(edto.isOwned());
				edition.setSlipCover(edto.isSlipcover());
				edition.setSteelbook(edto.isSteelbook());
				edition.setNotes(edto.getNotes());

				p.addEdition(edition);
			}
		}

		if (updateCode) {
			for (Edition edicion : p.getEditions()) {
				String expected = edicionService.calculateCompleteCode(edicion.getId(), p);
				edicion.setCode(expected);
			}
		}
	}

	public void addCover(Film pelicula, String url) throws IOException, URISyntaxException {

		try {
			final DwImageServiceI dwFotoService = new DwImageService();
			Optional<Source> sourceObj = sourceService.findById(sourceService.calcularIdSource(url));
			FilmImage pf = new FilmImage();
			pf.setUrl(url);
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setContent(dwFotoService.download(sourceObj.get().getId(), url));
			pf.setCover(true);

			pelicula.getImages().clear();
			pelicula.addImage(pf);
		} catch (Exception e) {
			logger.error("Error al descargar la carátula: {}", e.getMessage());
		}
	}

	@Transactional
	public void delete(Integer id) {
		filmRepo.deleteById(id);
	}

	public boolean existsById(Integer id) {
		return filmRepo.existsById(id);
	}

	public List<Film> findAllById(List<Integer> idsPeliculas) {
		return filmRepo.findAllById(idsPeliculas);
	}

	/**
	 * Save films
	 * 
	 * @param films
	 * @return
	 */
	public Film save(Film film) {
		return filmRepo.save(film);

	}

	public long countAll() {
		return filmRepo.count();
	}

	/**
	 * Complete movies search with all filters available
	 * 
	 * @param filter
	 * @param page
	 * @param field
	 * @param direction
	 * @return
	 */
	public Page<Film> findAllBy(FilterDto filter) {
		Sort sort = buildSort(filter.getOrder(), filter.getDireccion());
		int pageNum = filter.getPage().orElse(0);
		Pageable pageable = PageRequest.of(pageNum, filter.getPageSize(), sort);

		Specification<Film> spec = Specification.unrestricted();

		spec = spec.and(FilmSpecification.byPack(filter.getPack()));
		spec = spec.and(FilmSpecification.byPurchased(filter.getPurchased()));
		spec = spec.and(FilmSpecification.byFromYear(filter.getFromYear()));
		spec = spec.and(FilmSpecification.byToYear(filter.getToYear()));
		spec = spec.and(FilmSpecification.bySteelbook(filter.getSteelbook()));
		spec = spec.and(FilmSpecification.bySlipCover(filter.getSlipCover()));
		spec = spec.and(FilmSpecification.byFormat(filter.getFormat()));

		spec = spec.and(FilmSpecification.byGenre(filter.getGenre()));
		spec = spec.and(FilmSpecification.byLocation(filter.getLocation()));
		spec = spec.and(FilmSpecification.byNotes(filter.getNotes()));
		spec = spec.and(FilmSpecification.vigentes());
		spec = spec.and(FilmSpecification.byTitle(filter.getTitle()));

		// test if has fotos associated as caratula
		if (filter.getHasCover() != null) {
			spec = spec.and(FilmSpecification.hasCover(filter.getHasCover()));
		}

		Specification<Film> textoGroup = null;
		if (StringUtils.hasText(filter.getActor()) || StringUtils.hasText(filter.getDirector())) {
			Specification<Film> actorSpec = FilmSpecification.byActor(filter.getActor());
			Specification<Film> directorSpec = FilmSpecification.byDirector(filter.getDirector());

			textoGroup = actorSpec.or(directorSpec);
		}

		if (textoGroup != null) {
			spec = spec.and(textoGroup); // <-- AND con el resto
		}

		return filmRepo.findAll(spec, pageable);
	}

	public Page<Film> wanted() {
		Sort sort = Sort.by(Sort.Order.desc(MGMT_TITLE));

		Pageable pageable = PageRequest.of(0, 1000, sort);

		Specification<Film> spec = Specification.unrestricted();

		spec = spec.and(FilmSpecification.byPurchased(false));

		return filmRepo.findAll(spec, pageable);
	}

	public Page<Film> findForHome() {

		Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME,
				Sort.by(Sort.Order.desc("lastPurchased")));

		Specification<Film> spec = Specification.unrestricted();

		spec = spec.and(FilmSpecification.byPurchased(true));

		return filmRepo.findAll(spec, pageable);
	}

	public Page<Film> lastForGenre(String generoCodigo) {

		Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME - 1,
				Sort.by(Sort.Order.desc("lastPurchased")));
		Specification<Film> spec = Specification.unrestricted();

		spec = spec.and(FilmSpecification.byPurchased(true));
		spec = spec.and(FilmSpecification.byGenre(generoCodigo));

		return filmRepo.findAll(spec, pageable);
	}

	/**
	 * Reverse intented order in novelty order, because customers reports this in
	 * feedback
	 * 
	 * @param field
	 * @param direction
	 * @return
	 */
	protected static Sort buildSort(final Optional<String> field, final Optional<String> direction) {
		return Sort.by(Direction.fromString(direction.orElse("ASC")), field.isPresent() ? field.get() : MGMT_TITLE);
	}

	public List<Film> findByTitlesInYear(String title, int year) {
		List<Film> result = new ArrayList<>();
		result.addAll(filmRepo.findByTitleAndYear(title, year));
		result.addAll(filmRepo.findByMgmtTitleAndYear(title, year));

		return result;
	}

	public boolean isFilmRegistered(String tconst) {
		logger.info("Comprobando si la película está registrada para imdbId = [{}]", tconst);
		return !filmRepo.findByImdbId(tconst).isEmpty();
	}

	public Optional<Film> findByImdbId(String tconst) {
		logger.info("Buscando imdbId = [{}]", tconst);
		return filmRepo.findByImdbId(tconst);
	}

	public List<Film> findAllOrdered() {
		return filmRepo.findAllByOrderByMgmtTitleAscYearAsc();
	}

	public List<Film> findByTitle(final String title) {
		// reuse repository method that accepts a Pageable - return first 20 matches
		try {
			org.springframework.data.domain.Pageable pg = org.springframework.data.domain.PageRequest.of(0, 20);
			Specification<Film> tituloSpec = FilmSpecification.byTitle(title);

			return filmRepo.findAll(tituloSpec, pg).getContent();
		} catch (Exception e) {
			return java.util.Collections.emptyList();
		}
	}

	public Page<Film> findAll(Pageable pageable) {
		Sort sort = Sort.by(Direction.ASC, MGMT_TITLE);

		Pageable pageable2 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Specification<Film> spec = Specification.unrestricted();

		return filmRepo.findAll(spec, pageable2);
	}

	public Page<Film> getCoversPage(int page, int size, CoversFilter filter) {
		Pageable pageable = PageRequest.of(page, size);

		Specification<Film> spec = CoversSpecs.withFilters(filter);

		return filmRepo.findAll(spec, pageable);

	}

	public Map<Object, Integer> countByCategory() {

		Map<String, Integer> byGenre = filmRepo.countByGenre().stream()
				.collect(Collectors.toMap(row -> ((Genre) row[0]).getCode(), row -> ((Long) row[1]).intValue()));
		return byGenre.entrySet().stream().collect(Collectors.groupingBy(
				e -> Genre.getById(e.getKey()).getCategory(), Collectors.summingInt(Map.Entry::getValue)));
	}

	public Map<Object, Integer> countByGenre() {

		return filmRepo.countByGenre().stream()
				.collect(Collectors.toMap(row -> ((Genre) row[0]).getCode(), row -> ((Long) row[1]).intValue()));
	}
}
