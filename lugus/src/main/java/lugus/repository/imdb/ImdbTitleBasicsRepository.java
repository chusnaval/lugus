package lugus.repository.imdb;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.ImdbTitleBasics;

public interface ImdbTitleBasicsRepository extends JpaRepository<ImdbTitleBasics, String> {

}
