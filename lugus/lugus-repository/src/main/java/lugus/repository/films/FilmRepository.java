package lugus.repository.films;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;

import lugus.model.films.Film;

public interface FilmRepository {

	long count();
	
	List<Film> findAll();

	Optional<Film> findById(final Integer id);

	Film save(final Film film);

	void deleteById(final Integer id);

	boolean existsById(final Integer id);

	List<Film> findAllById(final Iterable<Integer> ids);

	Page<Film> findAll(final Specification<Film> spec, final Pageable pageable);

	List<Film> findByTitle(final String title, final Pageable pageable);

	List<Film> findByTitleAndYear(final String title, final int year);

	List<Film> findByMgmtTitleAndYear(final String mgmtTitle, final int year);

	Optional<Film> findByImdbId(final String tconst);

	List<Film> findAllByOrderByMgmtTitleAscYearAsc();

	List<Film> findByTitleContainingIgnoreCase(final String query);

	@Query("SELECT p.genre, COUNT(p) FROM Film p GROUP BY p.genre")
	List<Object[]> countByGenre();

}