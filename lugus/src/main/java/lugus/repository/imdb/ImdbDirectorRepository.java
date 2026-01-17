package lugus.repository.imdb;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.ImdbDirector;

public interface ImdbDirectorRepository extends JpaRepository<ImdbDirector, String> {

	List<ImdbDirector> findByPrimaryName(String name);

}
