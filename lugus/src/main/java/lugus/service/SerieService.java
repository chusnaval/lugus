package lugus.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import lombok.RequiredArgsConstructor;
import lugus.dto.FiltrosDto;
import lugus.dto.SerieCreateDto;
import lugus.model.Formato;
import lugus.model.Fuente;
import lugus.model.Genero;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.model.Serie;
import lugus.model.SerieFoto;
import lugus.repository.SerieSpecification;
import lugus.repository.PeliculaSpecification;
import lugus.repository.SerieRepository;

@Service
@RequiredArgsConstructor
public class SerieService {

	private static final int NUM_ELEMENTS_PER_HOME = 5;
	private static final int NUM_ELEMENTS_PER_PAGE = 30;
	private final SerieRepository serieRepo;
	private final LocalizacionService locService;
	private final FuenteService fuenteService;

	public List<Serie> findAll() {
		return serieRepo.findAll();
	}

	public Optional<Serie> findById(Integer id) {
		return serieRepo.findById(id);
	}

	/**
	 * Save films
	 * 
	 * @param films
	 * @return
	 */
	public Serie save(Serie film) {
		return serieRepo.save(film);

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
	public Page<Serie> findAllBy(FiltrosDto filter) {
		Sort sort = buildSort(filter.getOrden(), filter.getDireccion());

		Pageable pageable = PageRequest.of(filter.getPagina().get(), NUM_ELEMENTS_PER_PAGE, sort);

		Specification<Serie> spec = Specification.where(null);

		spec = spec.and(SerieSpecification.porComprado(filter.getComprado()));
		spec = spec.and(SerieSpecification.porFormato(filter.getFormato()));
		spec = spec.and(SerieSpecification.porGenero(filter.getGenero()));
		spec = spec.and(SerieSpecification.porLocalizacion(filter.getLocalizacion()));
		spec = spec.and(SerieSpecification.porNotas(filter.getNotas()));
		spec = spec.and(SerieSpecification.porTitulo(filter.getTexto()));
		
		return serieRepo.findAll(spec, pageable);
	}

	private Sort buildSort(Optional<String> field, Optional<String> direction) {
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

	public Page<Serie> findForHome() {
		Sort sort = buildSort(Optional.of("compra"), Optional.of("ASC"));

		Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME, sort);

		Specification<Serie> spec = Specification.where(null);

		spec = spec.and(SerieSpecification.porComprado(true));

		return serieRepo.findAll(spec, pageable);
	}

	@Transactional
	public Serie crear(@Valid SerieCreateDto dto, HttpSession session) throws IOException {
		Localizacion loc = findLocalizacion(dto);

		String user = (String) session.getAttribute("usuarioConectado");

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());

		Serie p = Serie.builder().titulo(dto.getTitulo()).tituloGest(dto.getTituloGest())
				.anyoInicio(dto.getAnyoInicio()).anyoFin(dto.getAnyoFin()).formato(formato).genero(genero)
				.comprado(dto.isComprado()).completa(dto.isCompleta()).notas(dto.getNotas()).localizacion(loc).usrAlta(user).tsAlta(Instant.now())
				.build();
		p.calcularCodigo();
		Serie saved = serieRepo.save(p);

		if (dto.getUrl() != null && !dto.getUrl().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			Optional<Fuente> fuenteObj = fuenteService.findById(dto.getFuente());
			SerieFoto pf = new SerieFoto();
			pf.setUrl(dto.getUrl());
			pf.setFuente(fuenteObj.get());
			pf.setFoto(dwFotoService.descargar(dto.getFuente(), dto.getUrl()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			saved = save(saved);
		}

		return saved;
	}
	
	private Localizacion findLocalizacion(SerieCreateDto dto) {
		Localizacion loc = null;
		if (dto.getLocalizacionCodigo() != null && !dto.getLocalizacionCodigo().isBlank()) {
			loc = locService.findById(dto.getLocalizacionCodigo())
					.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
		}
		return loc;
	}

	public List<Serie> findByTitlesInYear(String title, String titleGest, Integer year) {
		List<Serie> result = new ArrayList<Serie>();
		result.addAll(serieRepo.findByTituloAndAnyoInicio(title, year));
		result.addAll(serieRepo.findByTituloGestAndAnyoInicio(title, year));
		
		return result;
	}

}
