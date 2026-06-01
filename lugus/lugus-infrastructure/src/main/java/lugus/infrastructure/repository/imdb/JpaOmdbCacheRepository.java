package lugus.infrastructure.repository.imdb;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.imdb.OmdbCache;
import lugus.repository.imdb.OmdbCacheRepository;

public interface JpaOmdbCacheRepository extends OmdbCacheRepository, JpaRepository<OmdbCache, String> {

}
