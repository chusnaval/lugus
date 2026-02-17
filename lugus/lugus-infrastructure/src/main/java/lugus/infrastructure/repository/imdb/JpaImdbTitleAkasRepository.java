package lugus.infrastructure.repository.imdb;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lugus.model.imdb.ImdbTitleAkas;
import lugus.repository.imdb.ImdbTitleAkasRepository;

public interface JpaImdbTitleAkasRepository extends ImdbTitleAkasRepository, JpaRepository<ImdbTitleAkas, String> {

	@Override
	@Query("SELECT i FROM ImdbTitleAkas i WHERE i.titleid = :titleId and i.region = 'ES' and i.language is null ")
	Optional<ImdbTitleAkas> findByTitleId(@Param("titleId") String titleId);

}