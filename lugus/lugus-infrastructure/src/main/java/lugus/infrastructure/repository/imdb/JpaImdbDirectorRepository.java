package lugus.infrastructure.repository.imdb;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.ImdbDirector;
import lugus.repository.imdb.ImdbDirectorRepository;

public interface JpaImdbDirectorRepository extends ImdbDirectorRepository, JpaRepository<ImdbDirector, String> {

}