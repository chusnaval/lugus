package lugus.service.films;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.dto.core.CountryCode;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.FilmDto;
import lugus.dto.films.PeliculaChildDto;
import lugus.dto.filters.CoversFilter;
import lugus.dto.filters.CoversSpecs;
import lugus.infrastructure.repository.films.PeliculaSpecification;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.repository.films.PeliculaRepository;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.service.imdb.OmdbCacheService;
import lugus.service.titles.TitlesService;
import lugus.service.user.CurrentUserProvider;

@Service
@RequiredArgsConstructor
public class PeliculaService {

	private static final int NUM_ELEMENTS_PER_HOME = 5;
	private final PeliculaRepository peliculaRepo;
	private final LocationService locService;
	private final SourceService sourceService;
	private final CurrentUserProvider currentUserProvider;
	private final TitlesService titlesService;
	private final ImdbTitleBasicsService imdbTitleBasicsService;
	private final OmdbCacheService cacheService;
	private final ObjectMapper jsonMapper;

	public List<Pelicula> findAll() {
		return peliculaRepo.findAll();
	}

	/**
	 * Return a film by its id
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Pelicula> findById(Integer id) {
		if (id != null) {
			return peliculaRepo.findById(id);
		}
		return Optional.empty();
	}

	public Pelicula updateTrailer(Integer id, String url) {
		Pelicula p = peliculaRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));

		p.setTrailerUrl(url);
		return peliculaRepo.save(p);
	}

	
	@Transactional
	public Pelicula update(Integer id, FilmDto dto, String apiKey) throws IOException, URISyntaxException {

		Pelicula p = peliculaRepo.findById(id).orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
		Optional<Location> loc = locService.findById(dto.getLocation());

		p.setTitulo(dto.getTitle());
		p.setTituloGest(dto.getTitleMgmt());
		p.setAnyo(dto.getYear());
		p.setImdbId(dto.getImdbId());
		p.setTsModif(Instant.now());
		p.setFormato(Formato.getById(Short.valueOf(dto.getFormat().getCodigo())));
		p.setGenero(Genero.getById(dto.getGenreCode()));
		if (loc.isPresent()) {
			p.setLocation(loc.get());
		}
		p.setCodigo(dto.getMgmtCode());
		p.setComprado(dto.isOwned());
		p.setPack(dto.isPack());
		p.setFunda(dto.isSlipcover());
		p.setSteelbook(dto.isSteelbook());
		p.setNotas(dto.getNotes());
		addCaratula(p, dto.getCoverSrc());
		Pelicula saved = peliculaRepo.save(p);

		// Sincronizar Title si existe
		titlesService.findByPelicula_Id(id).ifPresent(title -> {
			title.setTitle(p.getTitulo());
			title.setYear(p.getAnyo());
			title.setPosterUrl(dto.getCoverSrc());

			// actualizar IMDB si ha cambiado
			if (dto.getImdbId() != null) {
				imdbTitleBasicsService.findByTconst(dto.getImdbId()).ifPresent(title::setImdb);
			}

			titlesService.save(title);
		});

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
			saved.setSynopsis((String) node.get("Plot").asText());
			save(saved);
		}
		return p;
	}

	public void addCaratula(Pelicula pelicula, String url) throws IOException, URISyntaxException {

		final DwFotoServiceI dwFotoService = new DwFotoService();
		Optional<Source> sourceObj = sourceService.findById(sourceService.calcularIdSource(url));
		PeliculaFoto pf = new PeliculaFoto();
		pf.setUrl(url);
		if (sourceObj.isPresent()) {
			pf.setSource(sourceObj.get());
		}
		pf.setFoto(dwFotoService.descargar(sourceObj.get().getId(), url));
		pf.setCaratula(true);

		pelicula.getPeliculaFotos().clear();
		pelicula.addCaratula(pf);
	}


	@Transactional
	public void delete(Integer id) {
		peliculaRepo.deleteById(id);
	}

	@Transactional
	public Pelicula addChild(Integer padreId, @Valid PeliculaChildDto dto) {
		Pelicula padre = peliculaRepo.findById(padreId)
				.orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));
		String username = currentUserProvider.currentUsername();
		Pelicula hijo = add(padre, dto, username);
		hijo.setPadre(padre);
		padre.getPeliculasPack().add(hijo);
		peliculaRepo.save(padre);
		return hijo;
	}

	private Pelicula add(Pelicula padre, PeliculaChildDto dto, String username) {

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());
		Pelicula p = Pelicula.builder().titulo(dto.getTitulo()).tituloGest(dto.getTitulo()).anyo(dto.getAnyo())
				.formato(formato).genero(genero).pack(false).steelbook(padre.isSteelbook()).funda(padre.isFunda())
				.comprado(padre.isComprado()).notas(padre.getNotas()).location(padre.getLocation()).usrAlta(username)
				.tsAlta(Instant.now()).build();
		p.calcularCodigoInicial();
		calculateCodeSuffix(p);

		return peliculaRepo.save(p);

	}

	public void calculateCodeSuffix(Pelicula p) {
		// we must find if the code starts exists in not pack films,
		// and if not exists we add "-1" to the code,
		// and if exists we add "-2", and so on, until we find a code that not exists
		boolean codeExists = true;
		int suffix = 1;
		String baseCode = p.getCodigo();
		while (codeExists) {
			if (peliculaRepo.existsByCodigoAndPack(p.getCodigo(), false)) {
				p.setCodigo(baseCode + "-" + suffix);
				suffix++;
			} else {
				codeExists = false;
			}
		}
	}

	public boolean existsById(Integer id) {
		return peliculaRepo.existsById(id);
	}

	public List<Pelicula> findAllById(List<Integer> idsPeliculas) {
		return peliculaRepo.findAllById(idsPeliculas);
	}

	/**
	 * Save films
	 * 
	 * @param films
	 * @return
	 */
	public Pelicula save(Pelicula film) {
		return peliculaRepo.save(film);

	}

	public long contarTodas() {
		return peliculaRepo.count();
	}

	public long contarTodasCompradas() {
		return peliculaRepo.countByCompradoAndPack(true, false);
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
	public Page<Pelicula> findAllBy(FiltrosDto filter) {
		Sort sort = buildSort(filter.getOrden(), filter.getDireccion());
		int pageNum = filter.getPagina().orElse(0);
		Pageable pageable = PageRequest.of(pageNum, filter.getPageSize(), sort);

		Specification<Pelicula> spec = Specification.where(null);

		spec = spec.and(PeliculaSpecification.porPack(filter.getPack()));
		spec = spec.and(PeliculaSpecification.porComprado(filter.getComprado()));
		spec = spec.and(PeliculaSpecification.porFromAnyo(filter.getFromAnyo()));
		spec = spec.and(PeliculaSpecification.porToAnyo(filter.getToAnyo()));
		spec = spec.and(PeliculaSpecification.porSteelbook(filter.getSteelbook()));
		spec = spec.and(PeliculaSpecification.porFunda(filter.getFunda()));
		spec = spec.and(PeliculaSpecification.porFormato(filter.getFormato()));

		spec = spec.and(PeliculaSpecification.porGenero(filter.getGenero()));
		spec = spec.and(PeliculaSpecification.byLocation(filter.getLocation()));
		spec = spec.and(PeliculaSpecification.porNotas(filter.getNotas()));
		spec = spec.and(PeliculaSpecification.vigentes());
		spec = spec.and(PeliculaSpecification.porTitulo(filter.getTitulo()));

		// test if has fotos associated as caratula
		if (filter.getTieneCaratula() != null) {
			spec = spec.and(PeliculaSpecification.tieneCaratula(filter.getTieneCaratula()));
		}

		Specification<Pelicula> textoGroup = null;
		if (StringUtils.hasText(filter.getActor()) || StringUtils.hasText(filter.getDirector())) {
			Specification<Pelicula> actorSpec = PeliculaSpecification.porActor(filter.getActor());
			Specification<Pelicula> directorSpec = PeliculaSpecification.porDirector(filter.getDirector());

			textoGroup = Specification.where(actorSpec).or(directorSpec);
		}

		if (textoGroup != null) {
			spec = spec.and(textoGroup); // <-- AND con el resto
		}

		return peliculaRepo.findAll(spec, pageable);
	}

	public Page<Pelicula> wanted() {
		Sort sort = Sort.by(Sort.Order.desc("tituloGest"));

		Pageable pageable = PageRequest.of(0, 1000, sort);

		Specification<Pelicula> spec = Specification.where(null);

		spec = spec.and(PeliculaSpecification.porComprado(false));

		return peliculaRepo.findAll(spec, pageable);
	}

	public Page<Pelicula> findForHome() {
		Sort sort = buildSort(Optional.of("compra"), Optional.of("ASC"));

		Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME, sort);

		Specification<Pelicula> spec = Specification.where(null);

		spec = spec.and(PeliculaSpecification.porComprado(true));

		return peliculaRepo.findAll(spec, pageable);
	}

	public Page<Pelicula> lastForGenre(String generoCodigo) {
		Sort sort = buildSort(Optional.of("compra"), Optional.of("ASC"));

		Pageable pageable = PageRequest.of(0, 4, sort);

		Specification<Pelicula> spec = Specification.where(null);

		spec = spec.and(PeliculaSpecification.porComprado(true));
		spec = spec.and(PeliculaSpecification.porGenero(generoCodigo));

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

		if (field.isPresent() && "compra".equals(field.get())) {

			Direction dir = Direction.DESC;
			Sort.Order orderCompra = new Sort.Order(dir, "tsCompra").with(Sort.NullHandling.NULLS_LAST);
			Sort.Order orderModif = new Sort.Order(dir, "tsModif").with(Sort.NullHandling.NULLS_LAST);
			Sort.Order orderAlta = new Sort.Order(dir, "tsAlta").with(Sort.NullHandling.NULLS_LAST);

			sort = Sort.by(orderCompra, orderModif, orderAlta);

		} else {
			sort = Sort.by(Direction.fromString(direction.orElse("ASC")),
					field.isPresent() ? field.get() : "tituloGest");
		}
		return sort;
	}

	public List<Pelicula> findByTitlesInYear(String title, int year) {
		List<Pelicula> result = new ArrayList<>();
		result.addAll(peliculaRepo.findByTituloAndAnyo(title, year));
		result.addAll(peliculaRepo.findByTituloGestAndAnyo(title, year));

		return result;
	}

	public int updateLocationForAll(String oldLocation, String newLocation) {
		return peliculaRepo.updateLocationByCode(oldLocation, newLocation);

	}

	public boolean isFilmRegistered(String tconst) {
		System.out.println("Comprobando si la película está registrada para imdbId = [" + tconst + "]");
		return !peliculaRepo.findByImdbId(tconst).isEmpty();
	}

	public List<Pelicula> findByImdbId(String tconst) {
		System.out.println("Buscando imdbId = [" + tconst + "]");
		return peliculaRepo.findByImdbId(tconst);
	}

	public List<Pelicula> findAllOrdered() {
		return peliculaRepo.findAllByOrderByTituloGestAscAnyoAsc();
	}

	public List<Pelicula> findByTitulo(String titulo) {
		// reuse repository method that accepts a Pageable - return first 20 matches
		try {
			org.springframework.data.domain.Pageable pg = org.springframework.data.domain.PageRequest.of(0, 20);
			Specification<Pelicula> tituloSpec = PeliculaSpecification.porTitulo(titulo);

			Specification<Pelicula> textoGroup = Specification.where(tituloSpec);
			return peliculaRepo.findAll(textoGroup, pg).getContent();
		} catch (Exception e) {
			return java.util.Collections.emptyList();
		}
	}

	public int addedInLastDays(int days) {
		Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);
		return peliculaRepo.countByTsAltaAfter(limit);
	}

	public Page<Pelicula> findAll(Pageable pageable) {
		Sort sort = Sort.by(Direction.ASC, "tituloGest");

		Pageable pageable2 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Specification<Pelicula> spec = Specification.where(null);

		return peliculaRepo.findAll(spec, pageable2);
	}

	public Page<Pelicula> getCoversPage(int page, int size, CoversFilter filter) {
		Pageable pageable = PageRequest.of(page, size);

		Specification<Pelicula> spec = CoversSpecs.withFilters(filter);

		return peliculaRepo.findAll(spec, pageable);

	}

	public int contarPorFormato(Formato format) {
		return peliculaRepo.countByFormatoAndCompradoAndPack(format, true, false);
	}

	public int contarNoCompradas() {
		return peliculaRepo.countByCompradoAndPack(false, false);
	}

	public Map<Object, Integer> contarPorCategoria() {

		Map<String, Integer> porGenero = peliculaRepo.countByGenero().stream()
				.collect(Collectors.toMap(row -> ((Genero) row[0]).getCodigo(), row -> ((Long) row[1]).intValue()));
		return porGenero.entrySet().stream().collect(Collectors.groupingBy(
				e -> Genero.getById(e.getKey()).getCategoria(), Collectors.summingInt(Map.Entry::getValue)));
	}
}
