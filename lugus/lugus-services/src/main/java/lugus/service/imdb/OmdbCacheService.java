package lugus.service.imdb;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.model.imdb.OmdbCache;
import lugus.repository.imdb.OmdbCacheRepository;

@Service
public class OmdbCacheService {

	private final OmdbCacheRepository repo;

	private final ObjectMapper mapper;
	
	@Autowired
	public OmdbCacheService(final OmdbCacheRepository repo, final ObjectMapper mapper){
		this.repo = repo;
		this.mapper = mapper;
	}

	public void saveToCache(String imdbId, Map<String, Object> json) {

		JsonNode node = mapper.valueToTree(json);

		repo.save(new OmdbCache(imdbId, node));
	}

	public OmdbCache getFromCache(String imdbId) {

		return repo.findById(imdbId).orElse(null);
	}

}
