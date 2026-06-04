package lugus.api.imdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.dto.core.CountryCode;
import lugus.model.films.Pelicula;
import lugus.service.films.PeliculaService;
import lugus.service.imdb.OmdbCacheService;

@RestController
@RequestMapping("/api/omdb")
public class OmdbController {

	private String apiKey;

	private final OmdbCacheService cacheService;
	
	private final PeliculaService peliculaService;

	private final ObjectMapper mapper;

	public OmdbController(@Value("${omdb.api.key}") String apiKey, OmdbCacheService cacheService, ObjectMapper mapper, PeliculaService peliculaService) {
		this.apiKey = apiKey;
		this.cacheService = cacheService;
		this.mapper = mapper;
		this.peliculaService = peliculaService;
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
		
		List<Pelicula> peliculaOpt = peliculaService.findByImdbId(imdbId);
			if (peliculaOpt.isEmpty()) {
				for(Pelicula p : peliculaOpt) {
					if(imdbId.equals(p.getImdbId())) {
						JsonNode node = mapper.valueToTree(cached.getJson());
						String country = node.get("Country").asText();
						
						// puede tener valor o no
						// o tener uno o varios países separados por coma
						List<String> values = new ArrayList<>();
						if(country != null) {	
							String[] countries = country.split(",");
							for (int i = 0; i < countries.length; i++) {
								values.add(CountryCode.fromString(countries[i]).getCode());
							}
						}
						
						// los guardamosen un campo separados por coma
						p.setCountry(String.join(",", values));
						p.setSynopsis((String) node.get("Plot").asText());
						peliculaService.save(p);
					}
				}
			
			}

		return ResponseEntity.ok(json);
	}
	
	
}
