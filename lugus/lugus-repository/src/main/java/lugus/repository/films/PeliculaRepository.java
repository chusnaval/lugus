package lugus.repository.films;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;

import lugus.model.films.Film;

public interface PeliculaRepository {

	long count();
	
	List<Film> findAll();

	Optional<Film> findById(Integer id);

	Film save(Film pelicula);

	void deleteById(Integer id);

	boolean existsById(Integer id);

	List<Film> findAllById(Iterable<Integer> ids);

	Page<Film> findAll(Specification<Film> spec, Pageable pageable);

	List<Film> findByTitle(final String title, Pageable pageable);

	List<Film> findByTitleAndYear(final String title, final int year);

	List<Film> findByMgmntTitleAndYear(final String mgmntTitle, final int year);

	Optional<Film> findByImdbId(String tconst);

	List<Film> findAllByOrderByMgmntTitleAscYearAsc();

	List<Film> findByTitleContainingIgnoreCase(String query);

	@Query("SELECT p.genre, COUNT(p) FROM Film p GROUP BY p.genre")
	List<Object[]> countByGenre();

}