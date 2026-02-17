package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import lugus.model.films.Pelicula;
import lugus.repository.films.PeliculaRepository;

public interface JpaPeliculaRepository extends PeliculaRepository, JpaRepository<Pelicula, Integer>, JpaSpecificationExecutor<Pelicula> {

	@Override
	@Modifying
	@Transactional
	@Query(value = """
			UPDATE lugus.peliculas
			   SET localizacion_codigo = :newCode
			 WHERE localizacion_codigo = :oldCode
			""", nativeQuery = true)
	int updateLocationByCode(@Param("oldCode") String oldCode, @Param("newCode") String newCode);

}