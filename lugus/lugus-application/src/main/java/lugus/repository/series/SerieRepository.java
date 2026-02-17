package lugus.repository.series;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import lugus.model.series.Serie;


public interface SerieRepository {

	List<Serie> findAll();

	Optional<Serie> findById(Integer id);

	Serie save(Serie serie);

	void deleteById(Integer id);

	long count();

	Page<Serie> findAll(Specification<Serie> spec, Pageable pageable);

	Collection<? extends Serie> findByTituloAndAnyoInicio(String title, Integer year);

	Collection<? extends Serie> findByTituloGestAndAnyoInicio(String title, Integer year);

}
