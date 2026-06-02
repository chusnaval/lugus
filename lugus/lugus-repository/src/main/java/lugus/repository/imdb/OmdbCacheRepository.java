package lugus.repository.imdb;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lugus.model.imdb.OmdbCache;

public interface OmdbCacheRepository {

	OmdbCache save(OmdbCache entry);

	Optional<OmdbCache> findById(String imdbId);

	@Modifying
	@Query(value = "UPDATE lugus.omdb_cache SET json = CAST(:json AS jsonb) WHERE imdb_id = :imdbId", nativeQuery = true)
	void updateJson(@Param("imdbId") String imdbId, @Param("json") String json);


}
