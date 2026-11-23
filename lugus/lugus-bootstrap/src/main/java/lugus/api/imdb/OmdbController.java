package lugus.api.imdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.dto.core.CountryCode;
import lugus.model.films.Film;
import lugus.service.films.FilmService;
import lugus.service.imdb.OmdbCacheService;

@RestController
@RequestMapping("/api/omdb")
public class OmdbController {

	private final String apiKey;

	private final OmdbCacheService cacheService;

	private final FilmService filmService;

	private final ObjectMapper mapper;

	public OmdbController(@Value("${omdb.api.key}") final String apiKey, final OmdbCacheService cacheService,
			final ObjectMapper mapper,
			FilmService filmService) {
		this.apiKey = apiKey;
		this.cacheService = cacheService;
		this.mapper = mapper;
		this.filmService = filmService;
	}

	@GetMapping("/{imdbId}")
	public ResponseEntity<Map<String, Object>> getFullOmdbJson(@PathVariable String imdbId) {

		var cached = cacheService.getFromCache(imdbId);

		if (cached != null) {
			Map<String, Object> json = mapper.convertValue(cached.getJson(), new TypeReference<Map<String, Object>>() {});
			return ResponseEntity.ok(json);
		}

		Map<String, Object> json = recoverOmdbAndGetJson(imdbId);

		Optional<Film> optFilm = filmService.findByImdbId(imdbId);
		if (optFilm.isPresent() && imdbId.equals(optFilm.get().getImdbId())) {
			updateWithOmdb(optFilm.get());
		}

		return ResponseEntity.ok(json);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> recoverOmdbAndGetJson(String imdbId) {
		String url = "https://www.omdbapi.com/?i=" + imdbId + "&apikey=" + apiKey + "&plot=full";
		final RestTemplate rest = new RestTemplate();
		Map<String, Object> json = rest.getForObject(url, Map.class);
		cacheService.saveToCache(imdbId, json);
		return json;
	}

	private void updateWithOmdb(Film p) {
		var cached = cacheService.getFromCache(p.getImdbId());
		JsonNode node = mapper.valueToTree(cached.getJson());
		boolean hasChanged = false;

		if (p.getCountry() == null || p.getCountry().isBlank()) {
			String country = node.get("Country").asText();
			// optional, if there's info, it's one or many sepparated by commas
			List<String> values = new ArrayList<>();
			if (country != null) {
				String[] countries = country.split(",");
				for (int i = 0; i < countries.length; i++) {
					values.add(CountryCode.fromString(countries[i]).getCode());
				}
			}

			// we save name sepparated by commmas in only one field
			p.setCountry(String.join(",", values));
			hasChanged = true;
		}

		if (p.getSynopsis() == null || p.getSynopsis().isBlank()) {
			p.setSynopsis(node.get("Plot").asText());
			hasChanged = true;
		}

		if (p.getDuration() == 0) {
			// we have nn min. format, and we uso only int
			String runtime = node.get("Runtime").asText();

			Matcher m = Pattern.compile("(\\d+)").matcher(runtime);
			int minutes = m.find() ? Integer.parseInt(m.group(1)) : 0;

			p.setDuration(minutes);
			hasChanged = true;
		}

		if (hasChanged) {
			filmService.save(p);
		}
	}

}
