package lugus.service.films;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.PeliculaChildDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.exception.LugusNotFoundException;
import lugus.model.core.Source;
import lugus.model.core.Location;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.repository.films.PeliculaRepository;
import lugus.repository.films.PeliculaSpecification;
import lugus.service.core.SourceService;
import lugus.service.core.LocationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculaService {

	private static final int NUM_ELEMENTS_PER_HOME = 5;
	private static final int NUM_ELEMENTS_PER_PAGE = 30;
	private final PeliculaRepository peliculaRepo;
	private final LocationService locService;
	private final SourceService sourceService;

	public List<Pelicula> findAll() {
		return peliculaRepo.findAll();
	}

	/**
	 * Return a film by its id
	 * @param id
	 * @return
	 */
	public Optional<Pelicula> findById(Integer id) {
		if(id!=null) {
			return peliculaRepo.findById(id);
		}
		return Optional.empty();
	}

	@Transactional
	public Pelicula crear(PeliculaCreateDto dto, HttpSession session) throws IOException {
		Location loc = findLocation(dto);

		String user = (String) session.getAttribute("usuarioConectado");

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());

		Pelicula p = Pelicula.builder().titulo(dto.getTitulo()).tituloGest(dto.getTituloGest()).anyo(dto.getAnyo())
				.formato(formato).genero(genero).pack(dto.isPack()).steelbook(dto.isSteelbook()).funda(dto.isFunda())
				.comprado(dto.isComprado()).notas(dto.getNotas()).location(loc).usrAlta(user).tsAlta(Instant.now())
				.build();
		p.calcularCodigo();
		Pelicula saved = peliculaRepo.save(p);

		if (dto.getUrl() != null && !dto.getUrl().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			Optional<Source> sourceObj = sourceService.findById(dto.getSource());
			PeliculaFoto pf = new PeliculaFoto();
			pf.setUrl(dto.getUrl());
			pf.setSource(sourceObj.get());
			pf.setFoto(dwFotoService.descargar(dto.getSource(), dto.getUrl()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			saved = save(saved);
		}

		return saved;
	}

	private Location findLocation(PeliculaCreateDto dto) {
		Location loc = null;
		if (dto.getLocationCode() != null && !dto.getLocationCode().isBlank()) {
			loc = locService.findById(dto.getLocationCode())
					.orElseThrow(() -> new LugusNotFoundException(dto.getLocationCode()));
		}
		return loc;
	}

	@Transactional
	public void delete(Integer id) {
		peliculaRepo.deleteById(id);
	}

	@Transactional
	public Pelicula addChild(Integer padreId, @Valid PeliculaChildDto dto, HttpSession session) throws IOException {
		Pelicula padre = peliculaRepo.findById(padreId)
				.orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));
		Pelicula hijo = add(padre, dto, session);
		hijo.setPadre(padre);
		padre.getPeliculasPack().add(hijo);
		peliculaRepo.save(padre);
		return hijo;
	}

	private Pelicula add(Pelicula padre, PeliculaChildDto dto, HttpSession session) {

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());
		String user = (String) session.getAttribute("usuarioConectado");

		Pelicula p = Pelicula.builder().titulo(dto.getTitulo()).tituloGest(dto.getTitulo()).anyo(dto.getAnyo())
				.formato(formato).genero(genero).pack(false).steelbook(padre.isSteelbook()).funda(padre.isFunda())
				.comprado(padre.isComprado()).notas(padre.getNotas()).location(padre.getLocation())
				.usrAlta(user).tsAlta(Instant.now()).build();
		p.calcularCodigo();
		Pelicula saved = peliculaRepo.save(p);

		return saved;
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

		Pageable pageable = PageRequest.of(filter.getPagina().get(), NUM_ELEMENTS_PER_PAGE, sort);

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
		
		Specification<Pelicula> textoGroup = null;
		if (StringUtils.hasText(filter.getTexto())) {
			Specification<Pelicula> actorSpec = PeliculaSpecification.porActor(filter.getTexto());
			Specification<Pelicula> directorSpec = PeliculaSpecification.porDirector(filter.getTexto());
			Specification<Pelicula> tituloSpec = PeliculaSpecification.porTitulo(filter.getTexto());

			textoGroup = Specification.where(actorSpec).or(directorSpec).or(tituloSpec);
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
		if (field.isPresent() && "compra".equals(field.get())) {
			if (direction.isPresent() && "ASC".equalsIgnoreCase(direction.get())) {
				sort = Sort.by(Sort.Order.desc("tsModif").with(Sort.NullHandling.NULLS_LAST),
						Sort.Order.desc("tsAlta"));
			} else {

				sort = Sort.by(Sort.Order.asc("tsModif").with(Sort.NullHandling.NULLS_FIRST), Sort.Order.asc("tsAlta"));
			}
		} else {
			sort = Sort.by(Direction.fromString(direction.orElse("ASC")), field.orElse("tituloGest"));
		}
		return sort;
	}

	public List<Pelicula> findByTitlesInYear(String title, String titleGest, int year) {
		List<Pelicula> result = new ArrayList<Pelicula>();
		result.addAll(peliculaRepo.findByTituloAndAnyo(title, year));
		result.addAll(peliculaRepo.findByTituloGestAndAnyo(title, year));
		
		return result;
	}

	public int updateLocationForAll(String oldLocation, String newLocation) {
		return peliculaRepo.updateLocationByCode(oldLocation, newLocation);
		
	}
	
	
}
