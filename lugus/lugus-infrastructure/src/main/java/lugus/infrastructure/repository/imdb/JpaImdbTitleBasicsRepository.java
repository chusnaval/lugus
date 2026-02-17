package lugus.infrastructure.repository.imdb;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.ImdbTitleBasics;
import lugus.repository.imdb.ImdbTitleBasicsRepository;

public interface JpaImdbTitleBasicsRepository extends ImdbTitleBasicsRepository, JpaRepository<ImdbTitleBasics, String> {

}