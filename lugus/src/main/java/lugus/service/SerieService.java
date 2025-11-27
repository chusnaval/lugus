package lugus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.dto.FiltrosDto;
import lugus.model.Serie;
import lugus.repository.SerieSpecification;
import lugus.repository.SerieRepository;

@Service
@RequiredArgsConstructor
public class SerieService {

	private static final int NUM_ELEMENTS_PER_HOME = 5;
	private static final int NUM_ELEMENTS_PER_PAGE = 30;
	private final SerieRepository serieRepo;
	
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
}
