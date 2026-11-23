package lugus.service.films;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import lugus.dto.core.FiltrosDto;
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
import lugus.repository.films.PeliculaRepository;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.service.imdb.OmdbCacheService;
import lugus.service.titles.TitlesService;

@Service
@RequiredArgsConstructor
public class PeliculaService {

	private static final int NUM_ELEMENTS_PER_HOME = 5;
	private final PeliculaRepository peliculaRepo;
	private final LocationService locService;
	private final SourceService sourceService;
	private final TitlesService titlesService;
	private final ImdbTitleBasicsService imdbTitleBasicsService;
	private final OmdbCacheService cacheService;
	private final ObjectMapper jsonMapper;
	private final EdicionService edicionService;

	public List<Film> findAll() {
		return peliculaRepo.findAll();
	}

	/**
	 * Return a film by its id
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Film> findById(Integer id) {
		if (id != null) {
			return peliculaRepo.findById(id);
		}
		return Optional.empty();
	}

	public Film updateTrailer(Integer id, String url) {
		Film p = peliculaRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));

		p.setTrailerUrl(url);
		return peliculaRepo.save(p);
	}

	@Transactional
	public Film update(final Integer id, final FilmDto dto, final String apiKey) throws IOException, URISyntaxException {

		Film p = peliculaRepo.findById(id).orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
		
		// Check if there are differences in the code based on title, year, or genre
		// TODO dont expose the fields that are used to calculate the code, and use a method in Pelicula to check if the code is different
		boolean isDiffInCode = p.isDiffInCode(dto.getTitleMgmt(), dto.getYear(), dto.getGenreCode());
		
		p.setTitle(dto.getTitle());
		p.setMgmntTitle(dto.getTitleMgmt());
		p.setYear(dto.getYear());
		p.setImdbId(dto.getImdbId());
		p.setGenre(Genre.getById(dto.getGenreCode()));
		p.setDuration(dto.getDuration());

		updateEditions(dto, p, isDiffInCode);

		addCaratula(p, dto.getCoverSrc());
		Film saved = peliculaRepo.save(p);

		// Sincronizar Title si existe
		titlesService.findByFilm_Id(id).ifPresent(title -> {
			title.setTitle(p.getTitle());
			title.setYear(p.getYear());
			title.setPosterUrl(dto.getCoverSrc());

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
			Edition edicion = edicionService.findById(edto.getId());

			Optional<Location> loc = locService.findById(edto.getLocation());
			edicion.setTsModif(Instant.now());
			edicion.setFormat(Format.getById(Short.valueOf(edto.getFormat().getCodigo())));
			if (loc.isPresent()) {
				edicion.setLocation(loc.get());
			}
			edicion.setCode(edto.getMgmtCode());
			edicion.setPurchased(edto.isOwned());
			edicion.setSlipCover(edto.isSlipcover());
			edicion.setSteelbook(edto.isSteelbook());
			edicion.setNotes(edto.getNotes());

			p.addEdition(edicion);
		}
		
		if(updateCode)
		{
			for (Edition edicion : p.getEditions()) {
				String expected = edicionService.calculateCompleteCode(edicion.getId(), p);
				edicion.setCode(expected);
			}
		}
	}

	public void addCaratula(Film pelicula, String url) throws IOException, URISyntaxException {

		try {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			Optional<Source> sourceObj = sourceService.findById(sourceService.calcularIdSource(url));
			FilmImage pf = new FilmImage();
			pf.setUrl(url);
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setContent(dwFotoService.descargar(sourceObj.get().getId(), url));
			pf.setCover(true);
			
			pelicula.getImages().clear();
			pelicula.addImage(pf);
		}catch (Exception e) {
			System.err.println("Error al descargar la carátula: " + e.getMessage());
		}
	}

	@Transactional
	public void delete(Integer id) {
		peliculaRepo.deleteById(id);
	}

	public boolean existsById(Integer id) {
		return peliculaRepo.existsById(id);
	}

	public List<Film> findAllById(List<Integer> idsPeliculas) {
		return peliculaRepo.findAllById(idsPeliculas);
	}

	/**
	 * Save films
	 * 
	 * @param films
	 * @return
	 */
	public Film save(Film film) {
		return peliculaRepo.save(film);

	}

	public long contarTodas() {
		return peliculaRepo.count();
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
	public Page<Film> findAllBy(FiltrosDto filter) {
		Sort sort = buildSort(filter.getOrden(), filter.getDireccion());
		int pageNum = filter.getPagina().orElse(0);
		Pageable pageable = PageRequest.of(pageNum, filter.getPageSize(), sort);

		Specification<Film> spec = Specification.where(null);

		spec = spec.and(FilmSpecification.byPack(filter.getPack()));
		spec = spec.and(FilmSpecification.byPurchased(filter.getComprado()));
		spec = spec.and(FilmSpecification.byFromYear(filter.getFromAnyo()));
		spec = spec.and(FilmSpecification.byToYear(filter.getToAnyo()));
		spec = spec.and(FilmSpecification.bySteelbook(filter.getSteelbook()));
		spec = spec.and(FilmSpecification.bySlipCover(filter.getFunda()));
		spec = spec.and(FilmSpecification.byFormat(filter.getFormato()));

		spec = spec.and(FilmSpecification.byGenre(filter.getGenero()));
		spec = spec.and(FilmSpecification.byLocation(filter.getLocation()));
		spec = spec.and(FilmSpecification.byNotes(filter.getNotas()));
		spec = spec.and(FilmSpecification.vigentes());
		spec = spec.and(FilmSpecification.byTitle(filter.getTitulo()));

		// test if has fotos associated as caratula
		if (filter.getTieneCaratula() != null) {
			spec = spec.and(FilmSpecification.hasCover(filter.getTieneCaratula()));
		}

		Specification<Film> textoGroup = null;
		if (StringUtils.hasText(filter.getActor()) || StringUtils.hasText(filter.getDirector())) {
			Specification<Film> actorSpec = FilmSpecification.byActor(filter.getActor());
			Specification<Film> directorSpec = FilmSpecification.byDirector(filter.getDirector());

			textoGroup = Specification.where(actorSpec).or(directorSpec);
		}

		if (textoGroup != null) {
			spec = spec.and(textoGroup); // <-- AND con el resto
		}

		return peliculaRepo.findAll(spec, pageable);
	}

	public Page<Film> wanted() {
		Sort sort = Sort.by(Sort.Order.desc("mgmntTitle"));

		Pageable pageable = PageRequest.of(0, 1000, sort);

		Specification<Film> spec = Specification.where(null);

		spec = spec.and(FilmSpecification.byPurchased(false));

		return peliculaRepo.findAll(spec, pageable);
	}

	public Page<Film> findForHome() {

	    Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME,
	        Sort.by(Sort.Order.desc("lastPurchased"))
	    );

	    Specification<Film> spec = Specification.where(null);

	    spec = spec.and(FilmSpecification.byPurchased(true));

	    return peliculaRepo.findAll(spec, pageable);
	}



	public Page<Film> lastForGenre(String generoCodigo) {

	    Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME-1,
		        Sort.by(Sort.Order.desc("lastPurchased"))
		    );
		Specification<Film> spec = Specification.where(null);

		spec = spec.and(FilmSpecification.byPurchased(true));
		spec = spec.and(FilmSpecification.byGenre(generoCodigo));

		return peliculaRepo.findAll(spec, pageable);
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
		Sort sort;
		// when order by "compra", we want to order by tsModif and tsAlta,
		// when ts modif is null, we want to order by tsAlta, and in desc
		// because we want to see first the last modified, and when is null, the last
		// created

//		if (field.isPresent() && "compra".equals(field.get())) {
//
//			Direction dir = Direction.DESC;
//			Sort.Order orderCompra = new Sort.Order(dir, "editions.tsCompra").with(Sort.NullHandling.NULLS_LAST);
//			Sort.Order orderModif = new Sort.Order(dir, "editions.tsModif").with(Sort.NullHandling.NULLS_LAST);
//			Sort.Order orderAlta = new Sort.Order(dir, "editions.tsAlta").with(Sort.NullHandling.NULLS_LAST);
//
//			sort = Sort.by(orderCompra, orderModif, orderAlta);
//
//		} else {
		sort = Sort.by(Direction.fromString(direction.orElse("ASC")), field.isPresent() ? field.get() : "tituloGest");
		// }
		return sort;
	}

	public List<Film> findByTitlesInYear(String title, int year) {
		List<Film> result = new ArrayList<>();
		result.addAll(peliculaRepo.findByTitleAndYear(title, year));
		result.addAll(peliculaRepo.findByMgmntTitleAndYear(title, year));

		return result;
	}

	public boolean isFilmRegistered(String tconst) {
		System.out.println("Comprobando si la película está registrada para imdbId = [" + tconst + "]");
		return !peliculaRepo.findByImdbId(tconst).isEmpty();
	}
	public Optional<Film> findByImdbId(String tconst) {
		System.out.println("Buscando imdbId = [" + tconst + "]");
		return peliculaRepo.findByImdbId(tconst);
	}

	public List<Film> findAllOrdered() {
		return peliculaRepo.findAllByOrderByMgmntTitleAscYearAsc();
	}

	public List<Film> findByTitulo(String titulo) {
		// reuse repository method that accepts a Pageable - return first 20 matches
		try {
			org.springframework.data.domain.Pageable pg = org.springframework.data.domain.PageRequest.of(0, 20);
			Specification<Film> tituloSpec = FilmSpecification.byTitle(titulo);

			Specification<Film> textoGroup = Specification.where(tituloSpec);
			return peliculaRepo.findAll(textoGroup, pg).getContent();
		} catch (Exception e) {
			return java.util.Collections.emptyList();
		}
	}

	public Page<Film> findAll(Pageable pageable) {
		Sort sort = Sort.by(Direction.ASC, "tituloGest");

		Pageable pageable2 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Specification<Film> spec = Specification.where(null);

		return peliculaRepo.findAll(spec, pageable2);
	}

	public Page<Film> getCoversPage(int page, int size, CoversFilter filter) {
		Pageable pageable = PageRequest.of(page, size);

		Specification<Film> spec = CoversSpecs.withFilters(filter);

		return peliculaRepo.findAll(spec, pageable);

	}

	public Map<Object, Integer> countByCategory() {

		Map<String, Integer> porGenero = peliculaRepo.countByGenre().stream()
				.collect(Collectors.toMap(row -> ((Genre) row[0]).getCodigo(), row -> ((Long) row[1]).intValue()));
		return porGenero.entrySet().stream().collect(Collectors.groupingBy(
				e -> Genre.getById(e.getKey()).getCategoria(), Collectors.summingInt(Map.Entry::getValue)));
	}

	public Map<Object, Integer> countByGenre() {
		
		return peliculaRepo.countByGenre().stream()
				.collect(Collectors.toMap(row -> ((Genre) row[0]).getCodigo(), row -> ((Long) row[1]).intValue()));
	}
}
