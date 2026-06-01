package lugus.service.imdb;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.model.imdb.OmdbCache;
import lugus.repository.imdb.OmdbCacheRepository;

@Service
public class OmdbCacheService {

    @Autowired
    private OmdbCacheRepository repo;
    
    @Autowired
    private ObjectMapper mapper;
    
	public void saveToCache(String imdbId, Map<String, Object> json) throws JsonProcessingException {
		
		String jsonString = mapper.writeValueAsString(json);
		
		repo.save(new OmdbCache(imdbId, jsonString));
	}

	public OmdbCache getFromCache(String imdbId) {
		
		 return repo.findById(imdbId).orElse(null);
	}


    

  
}
