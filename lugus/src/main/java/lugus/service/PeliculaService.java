package lugus.service;

import lombok.RequiredArgsConstructor;
import lugus.controller.DwFotoService;
import lugus.dto.FiltrosDto;
import lugus.dto.PeliculaChildDto;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Formato;
import lugus.model.Fuente;
import lugus.model.Genero;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.model.PeliculaFoto;
import lugus.repository.PeliculaRepository;
import lugus.repository.PeliculaSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculaService {

	private static final int NUM_ELEMENTS_PER_PAGE = 30;
	private final PeliculaRepository peliculaRepo;
	private final LocalizacionService locService;
	private final FuenteService fuenteService;

	public List<Pelicula> findAll() {
		return peliculaRepo.findAll();
	}

	public Optional<Pelicula> findById(Integer id) {
		return peliculaRepo.findById(id);
	}

	@Transactional
	public Pelicula crear(PeliculaCreateDto dto, HttpSession session) throws IOException {
		Localizacion loc = findLocalizacion(dto);

		String user = (String) session.getAttribute("usuarioConectado");

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());

		Pelicula p = Pelicula.builder().titulo(dto.getTitulo()).tituloGest(dto.getTituloGest()).anyo(dto.getAnyo())
				.formato(formato).genero(genero).pack(dto.isPack()).steelbook(dto.isSteelbook()).funda(dto.isFunda())
				.comprado(dto.isComprado()).notas(dto.getNotas()).localizacion(loc).usrAlta(user).tsAlta(Instant.now())
				.build();
		p.calcularCodigo();
		Pelicula saved = peliculaRepo.save(p);

		if (dto.getUrl() != null && !dto.getUrl().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			Optional<Fuente> fuenteObj = fuenteService.findById(dto.getFuente());
			PeliculaFoto pf = new PeliculaFoto();
			pf.setUrl(dto.getUrl());
			pf.setFuente(fuenteObj.get());
			pf.setFoto(dwFotoService.descargar(dto.getFuente(), dto.getUrl()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			saved = save(saved);
		}

		return saved;
	}

	private Localizacion findLocalizacion(PeliculaCreateDto dto) {
		Localizacion loc = null;
		if (dto.getLocalizacionCodigo() != null && !dto.getLocalizacionCodigo().isBlank()) {
			loc = locService.findById(dto.getLocalizacionCodigo())
					.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
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
				.comprado(padre.isComprado()).notas(padre.getNotas()).localizacion(padre.getLocalizacion())
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
	public Page<Pelicula> findAllBy(FiltrosDto filter, final int page, final String field, final String direction) {
		Sort sort = buildSort(field, direction);

		Pageable pageable = PageRequest.of(page, NUM_ELEMENTS_PER_PAGE, sort);

		Specification<Pelicula> spec = Specification.where(null);

		// by default we want the purchased and separated films
		if (filter.isInitFilter()) {
			spec = spec.and(PeliculaSpecification.porPack(false));
			spec = spec.and(PeliculaSpecification.porComprado(true));
		} else {
			spec = spec.and(PeliculaSpecification.porPack(filter.getPack()));
			spec = spec.and(PeliculaSpecification.porComprado(filter.getComprado()));
		}

		spec = spec.and(PeliculaSpecification.porFromAnyo(filter.getFromAnyo()));
		spec = spec.and(PeliculaSpecification.porToAnyo(filter.getToAnyo()));
		spec = spec.and(PeliculaSpecification.porSteelbook(filter.getSteelbook()));
		spec = spec.and(PeliculaSpecification.porFunda(filter.getFunda()));
		spec = spec.and(PeliculaSpecification.porFormato(filter.getFormato()));
		
		spec = spec.and(PeliculaSpecification.porGenero(filter.getGenero()));
		spec = spec.and(PeliculaSpecification.porLocalizacion(filter.getLocalizacion()));
		spec = spec.and(PeliculaSpecification.porNotas(filter.getNotas()));
		spec = spec.and(PeliculaSpecification.porTieneCaratula(filter.getTieneCaratula()));

		spec = spec.or(PeliculaSpecification.porActor(filter.getTexto()));
		spec = spec.or(PeliculaSpecification.porDirector(filter.getTexto()));
		spec = spec.or(PeliculaSpecification.porTitulo(filter.getTexto()));

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
	protected static Sort buildSort(final String field, final String direction) {
		Sort sort;
		if ("compra".equals(field)) {
			if ("ASC".equalsIgnoreCase(direction)) {
				sort = Sort.by(Sort.Order.desc("tsModif").with(Sort.NullHandling.NULLS_LAST),
						Sort.Order.desc("tsAlta"));
			} else {

				sort = Sort.by(Sort.Order.asc("tsModif").with(Sort.NullHandling.NULLS_FIRST), Sort.Order.asc("tsAlta"));
			}
		} else {
			sort = Sort.by(Direction.fromString(direction), field);
		}
		return sort;
	}
}
