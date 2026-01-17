package lugus.repository.imdb;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.ImdbDirectorFilm;

public interface ImdbDirectorFilmRepository extends JpaRepository<ImdbDirectorFilm, String> {

	List<ImdbDirectorFilm> findByTitleContainingIgnoreCase(final String title);
	
}
