package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.films.PeliculasOtros;


public interface PeliculasOtrosRepository extends JpaRepository<PeliculasOtros, Integer> {

	List<PeliculasOtros> findByImdbId(String tconst);

}
