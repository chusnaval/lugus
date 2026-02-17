package lugus.infrastructure.repository.imdb;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.ImdbDirectorFilm;
import lugus.repository.imdb.ImdbDirectorFilmRepository;

public interface JpaImdbDirectorFilmRepository extends ImdbDirectorFilmRepository, JpaRepository<ImdbDirectorFilm, String> {

}