package lugus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Pelicula;

public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {

}
