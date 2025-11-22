package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.ImdbDirector;

public interface ImdbDirectorRepository extends JpaRepository<ImdbDirector, String> {

	List<ImdbDirector> findByPrimaryName(String name);

}
