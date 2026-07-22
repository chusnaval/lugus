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
import lugus.infrastructure.repository.films.PeliculaSpecification;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.films.Edicion;
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

		p.setTitulo(dto.getTitle());
		p.setTituloGest(dto.getTitleMgmt());
		p.setAnyo(dto.getYear());
		p.setImdbId(dto.getImdbId());
		p.setGenero(Genero.getById(dto.getGenreCode()));
		p.setDuration(dto.getDuration());

		for (EditionDto edto : dto.getEditions()) {
			Edicion edicion = edicionService.findById(edto.getId());

			Optional<Location> loc = locService.findById(edto.getLocation());
			edicion.setTsModif(Instant.now());
			edicion.setFormato(Formato.getById(Short.valueOf(edto.getFormat().getCodigo())));
			if (loc.isPresent()) {
				edicion.setLocation(loc.get());
			}
			edicion.setCodigo(edto.getMgmtCode());
			edicion.setComprado(edto.isOwned());
			edicion.setFunda(edto.isSlipcover());
			edicion.setSteelbook(edto.isSteelbook());
			edicion.setNotas(edto.getNotes());

			p.addEdicion(edicion);
		}

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
				saved.setSynopsis((String) node.get("Plot").asText());
				save(saved);
			}
		}
		return p;
	}

	public void addCaratula(Pelicula pelicula, String url) throws IOException, URISyntaxException {

		try {
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
		spec = spec.and(PeliculaSpecification.porLocalizacion(filter.getLocation()));
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

	    Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME,
	        Sort.by(Sort.Order.desc("ultimaCompra"))
	    );

	    Specification<Pelicula> spec = Specification.where(null);

	    spec = spec.and(PeliculaSpecification.porComprado(true));

	    return peliculaRepo.findAll(spec, pageable);
	}



	public Page<Pelicula> lastForGenre(String generoCodigo) {

	    Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME-1,
		        Sort.by(Sort.Order.desc("ultimaCompra"))
		    );
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

	public List<Pelicula> findByTitlesInYear(String title, int year) {
		List<Pelicula> result = new ArrayList<>();
		result.addAll(peliculaRepo.findByTituloAndAnyo(title, year));
		result.addAll(peliculaRepo.findByTituloGestAndAnyo(title, year));

		return result;
	}

	public boolean isFilmRegistered(String tconst) {
		System.out.println("Comprobando si la película está registrada para imdbId = [" + tconst + "]");
		return !peliculaRepo.findByImdbId(tconst).isEmpty();
	}

	@Deprecated
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

	public Map<Object, Integer> contarPorCategoria() {

		Map<String, Integer> porGenero = peliculaRepo.countByGenero().stream()
				.collect(Collectors.toMap(row -> ((Genero) row[0]).getCodigo(), row -> ((Long) row[1]).intValue()));
		return porGenero.entrySet().stream().collect(Collectors.groupingBy(
				e -> Genero.getById(e.getKey()).getCategoria(), Collectors.summingInt(Map.Entry::getValue)));
	}

	public Map<Object, Integer> contarPorGenero() {
		
		return peliculaRepo.countByGenero().stream()
				.collect(Collectors.toMap(row -> ((Genero) row[0]).getCodigo(), row -> ((Long) row[1]).intValue()));
	}
}
