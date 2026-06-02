package lugus.api.imdb;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.service.imdb.OmdbCacheService;

@RestController
@RequestMapping("/api/omdb")
public class OmdbController {

	private String apiKey;

	private final OmdbCacheService cacheService;

	private final ObjectMapper mapper;

	public OmdbController(@Value("${omdb.api.key}") String apiKey, OmdbCacheService cacheService, ObjectMapper mapper) {
		this.apiKey = apiKey;
		this.cacheService = cacheService;
		this.mapper = mapper;
	}

	@GetMapping("/{imdbId}")
	public ResponseEntity<Map<String, Object>> getFullOmdbJson(@PathVariable String imdbId)
			throws JsonProcessingException {

		var cached = cacheService.getFromCache(imdbId);
		if (cached != null) {

			Map<String, Object> json = mapper.convertValue(cached.getJson(), Map.class);
			return ResponseEntity.ok(json);
		}
		String url = "https://www.omdbapi.com/?i=" + imdbId + "&apikey=" + apiKey + "&plot=full";
		final RestTemplate rest = new RestTemplate();
		Map<String, Object> json = rest.getForObject(url, Map.class);
		cacheService.saveToCache(imdbId, json);

		return ResponseEntity.ok(json);
	}
}
