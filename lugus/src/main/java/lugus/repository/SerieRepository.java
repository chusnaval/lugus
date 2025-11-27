package lugus.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Integer>{

	Page<Serie> findAll(Specification<Serie> spec, Pageable pageable);

}
