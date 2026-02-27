package lugus.repository.films;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

	int updateLocationByCode(String oldCode, String newCode);

}
