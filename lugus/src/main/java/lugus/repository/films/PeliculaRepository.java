package lugus.repository.films;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import lugus.model.films.Pelicula;

public interface PeliculaRepository extends JpaRepository<Pelicula, Integer>, JpaSpecificationExecutor<Pelicula> {

	long count();

	List<Pelicula> findByTitulo(String titulo, Pageable pageable);

	List<Pelicula> findByTituloAndAnyo(final String titulo, final int anyo);

	List<Pelicula> findByTituloGestAndAnyo(final String titulo, final int anyo);

	@Modifying
	@Transactional
	@Query(value = """
			UPDATE lugus.peliculas
			   SET localizacion_codigo = :newCode
			 WHERE localizacion_codigo = :oldCode
			""", nativeQuery = true)
	int updateLocationByCode(@Param("oldCode") String oldCode, @Param("newCode") String newCode);

}
