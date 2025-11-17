package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Director;

public interface DirectorRepository extends JpaRepository<Director, Integer> {

	List<Director> findByPeliculaId(int id);

}
