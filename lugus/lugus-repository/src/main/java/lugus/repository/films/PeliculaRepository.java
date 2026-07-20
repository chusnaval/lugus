package lugus.repository.films;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;

import lugus.model.films.Pelicula;

public interface PeliculaRepository {

	long count();
	
	List<Pelicula> findAll();

	Optional<Pelicula> findById(Integer id);

	Pelicula save(Pelicula pelicula);

	void deleteById(Integer id);

	boolean existsById(Integer id);

	List<Pelicula> findAllById(Iterable<Integer> ids);

	Page<Pelicula> findAll(Specification<Pelicula> spec, Pageable pageable);

	List<Pelicula> findByTitulo(String titulo, Pageable pageable);

	List<Pelicula> findByTituloAndAnyo(final String titulo, final int anyo);

	List<Pelicula> findByTituloGestAndAnyo(final String titulo, final int anyo);

	List<Pelicula> findByImdbId(String tconst);

	List<Pelicula> findAllByOrderByTituloGestAscAnyoAsc();

	List<Pelicula> findByTituloContainingIgnoreCase(String query);

	@Query("SELECT p.genero, COUNT(p) FROM Pelicula p GROUP BY p.genero")
	List<Object[]> countByGenero();

}