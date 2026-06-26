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


}