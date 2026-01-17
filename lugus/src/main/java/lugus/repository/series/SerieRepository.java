package lugus.repository.series;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.series.Serie;


public interface SerieRepository extends JpaRepository<Serie, Integer>{

	Page<Serie> findAll(Specification<Serie> spec, Pageable pageable);

	Collection<? extends Serie> findByTituloAndAnyoInicio(String title, Integer year);

	Collection<? extends Serie> findByTituloGestAndAnyoInicio(String title, Integer year);

}
