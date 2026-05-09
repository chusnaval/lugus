package lugus.infrastructure.repository.imdb;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.ImdbBasics;
import lugus.repository.imdb.ImdbBasicsRepository;

/**
 * 
 */
public interface JpaImdbBasicsRepository extends ImdbBasicsRepository , JpaRepository<ImdbBasics, String> {

}
