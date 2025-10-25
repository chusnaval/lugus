package lugus.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Pelicula;
public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

	
	int countByPack(boolean value);

	List<Pelicula> findByTitulo(String titulo, Pageable pageable);

}
