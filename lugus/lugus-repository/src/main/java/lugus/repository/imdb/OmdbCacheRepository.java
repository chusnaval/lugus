package lugus.repository.imdb;

import java.util.Optional;

import lugus.model.imdb.OmdbCache;

public interface OmdbCacheRepository {

	OmdbCache save(OmdbCache entry);

	Optional<OmdbCache> findById(String imdbId);

}
