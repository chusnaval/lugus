package lugus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lugus.model.imdb.ImdbTitleAkas;

public interface ImdbTitleAkasRepository extends JpaRepository<ImdbTitleAkas, String> {

	@Query("SELECT i FROM ImdbTitleAkas i WHERE i.titleid = :titleId and i.region = 'ES' and i.language is null ")
	Optional<ImdbTitleAkas> findByTitleId(@Param("titleId")  String titleId);

}
