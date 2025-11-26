package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Fuente;

public interface FuenteRepository extends JpaRepository<Fuente, Integer> {

	List<Fuente> findBySuggestIsNotNull();

}
