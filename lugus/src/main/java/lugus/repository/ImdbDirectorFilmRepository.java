package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.ImdbDirectorFilm;

public interface ImdbDirectorFilmRepository extends JpaRepository<ImdbDirectorFilm, String> {

	List<ImdbDirectorFilm> findByTitleContainingIgnoreCase(final String title);
	
}
