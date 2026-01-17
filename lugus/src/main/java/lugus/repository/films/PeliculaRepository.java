package lugus.repository.films;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.films.Pelicula;


public interface PeliculaRepository extends JpaRepository<Pelicula, Integer>, JpaSpecificationExecutor<Pelicula> {

	long count();

	List<Pelicula> findByTitulo(String titulo, Pageable pageable);

	List<Pelicula> findByTituloAndAnyo(final String titulo, final int anyo);

	List<Pelicula> findByTituloGestAndAnyo(final String titulo, final int anyo);

}
