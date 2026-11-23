package lugus.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lugus.dto.core.CountryCode;
import lugus.model.films.Film;
import lugus.model.imdb.OmdbCache;
import lugus.model.series.Series;
import lugus.model.titles.Title;
import lugus.service.films.FilmService;
import lugus.service.imdb.OmdbCacheService;
import lugus.service.series.SeriesService;
import lugus.service.titles.TitlesService;

@Service
public class OmdbBatchExecutor {
	private static final Logger LOGGER = LoggerFactory.getLogger(OmdbBatchExecutor.class);

	private final FilmService filmService;
	private final SeriesService seriesService;
	private final OmdbCacheService cacheService;
	private final TitlesService titlesService;
	private final String apiKey;
	private final RestTemplate rest = new RestTemplate();
	private final ObjectMapper mapper;

	@Autowired
	public OmdbBatchExecutor(@Value("${omdb.api.key}") final String apiKey, final FilmService filmService,
			final SeriesService seriesService, final TitlesService titlesService, final OmdbCacheService cacheService,
			final ObjectMapper mapper) {
		this.filmService = filmService;
		this.seriesService = seriesService;
		this.cacheService = cacheService;
		this.titlesService = titlesService;
		this.apiKey = apiKey;
		this.mapper = mapper;
	}

	@Transactional
	public void fillCache() {
		updatePeliculas();
		LOGGER.info("✔ Batch películas completado");
		updateSeries();
		LOGGER.info("✔ Batch series completado");
		updateTitles();
		LOGGER.info("✔ Batch todos completado");
	}

	private void updateTitles() {
		List<Title> title = titlesService.findAll();

		int total = title.size();
		int index = 1;

		for (Title s : title) {
			if (s.getImdb() == null) {
				LOGGER.info("Saltando title sin IMDb: {} ", s.getTitle());
			} else {
				String imdbId = s.getImdb().getTconst();

				if (imdbId == null || imdbId.isBlank()) {
					LOGGER.info("Saltando title sin IMDb ID: {} ", s.getTitle());
				} else {
					OmdbCache cached = cacheService.getFromCache(imdbId);
					if (cached != null) {
						LOGGER.info("{}/{} — Ya cacheada: {} ", index, total, imdbId);
					} else {
						extract(total, index, imdbId);
					}
				}
			}

			index++;
		}

	}

	private void updateSeries() {
		List<Series> series = seriesService.findAll();

		int total = series.size();
		int index = 1;

		for (Series s : series) {
			String imdbId = s.getImdbId();

			if (imdbId == null || imdbId.isBlank()) {
				LOGGER.info("Saltando serie sin IMDb ID: {} ", s.getTitle());
			} else {
				OmdbCache cached = cacheService.getFromCache(imdbId);
				if (cached != null) {
					LOGGER.info("{}/{} — Ya cacheada: {} ", index, total, imdbId);
				} else {
					extract(total, index, imdbId);
				}
			}

			index++;
		}
	}

	@SuppressWarnings("unchecked")
	private void extract(int total, int index, String imdbId) {
		try {
			LOGGER.info("{}/{} — Cacheando: {} ", index, total, imdbId);
			String url = "https://www.omdbapi.com/?i=" + imdbId + "&apikey=" + apiKey + "&plot=full";
			Map<String, Object> json = rest.getForObject(url, Map.class);

			cacheService.saveToCache(imdbId, json);

			Thread.sleep(500); // due to rate limits
		} catch (RestClientException e) {
			LOGGER.error("Error consultando OMDb para {}: {}", imdbId, e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.warn("Interrumpida la espera al cachear {}: {}", imdbId, e.getMessage(), e);
		}
	}

	private void updatePeliculas() {
		List<Film> films = filmService.findAll();

		int total = films.size();
		int index = 1;

		for (Film p : films) {
			String imdbId = p.getImdbId();

			if (imdbId == null || imdbId.isBlank()) {
				LOGGER.info("Saltando película sin IMDb ID: {} ", p.getTitle());
			} else {
				OmdbCache cached = cacheService.getFromCache(imdbId);
				if (cached != null) {
					LOGGER.info("{}/{} — Ya cacheada: {} ", index, total, imdbId);
				} else {
					extract(total, index, imdbId);
				}
			}

			index++;
		}
	}

	public void updateCountry() {
		int count = filmService.findAll().size();
		int index = 1;
		for (Film p : filmService.findAll()) {
			if (p.getImdbId() == null || p.getImdbId().isBlank()) {
				LOGGER.info("Saltando película sin IMDb ID: {} ", p.getTitle());
			} else if (p.getCountry() != null && !p.getCountry().isBlank()) {
				LOGGER.info("Saltando película con país ya seteado: {} ", p.getTitle());
			} else {
				OmdbCache cached = obtainCachedFilm(count, index, p);
				if (cached != null) {
					String country = updateCountryFromOmdb(p, cached);
					LOGGER.info("Actualizada película: {} con país: {} en posición {}/{} ", p.getTitle(), country,
							index,
							count);
				} else {
					LOGGER.info("No se encontró en cache, saltando: {} ", p.getTitle());
				}
			}
			index++;
		}
		LOGGER.info(
				"Terminado batch de actualización de país para películas: {} películas procesadas.", count);
	}

	private String updateCountryFromOmdb(Film p, OmdbCache cached) {
		JsonNode node = mapper.valueToTree(cached.getJson());
		String country = node.get("Country").asText();

		// Optional info, if there's info, its one or many
		List<String> values = new ArrayList<>();
		if (country != null) {
			String[] countries = country.split(",");
			for (int i = 0; i < countries.length; i++) {
				values.add(CountryCode.fromString(countries[i]).getCode());
			}
		}

		// we save joined but separated by commas
		p.setCountry(String.join(",", values));

		filmService.save(p);
		return country;
	}

	public void updateRuntime() {
		int count = filmService.findAll().size();
		int index = 1;
		for (Film p : filmService.findAll()) {
			if (p.getImdbId() == null || p.getImdbId().isBlank()) {
				LOGGER.info("Saltando película sin IMDb ID: {} ", p.getTitle());
			} else if (p.getDuration() != 0) {
				LOGGER.info("Saltando película con duracion ya seteado: {} ", p.getTitle());
			} else {
				OmdbCache cached = obtainCachedFilm(count, index, p);
				if (cached != null) {
					JsonNode node = mapper.valueToTree(cached.getJson());
					String runtime = node.get("Runtime").asText();

					Matcher m = Pattern.compile("(\\d+)").matcher(runtime);
					int minutes = m.find() ? Integer.parseInt(m.group(1)) : 0;

					p.setDuration(minutes);

					filmService.save(p);
					LOGGER.info("Actualizada película: {} con runtime: {} en posición {}/{} ", p.getTitle(), minutes,
							index,
							count);
				} else {
					LOGGER.info("No se encontró en cache, saltando: {} ", p.getTitle());
				}
			}
		}
		LOGGER.info(
				"Terminado batch de actualización de duracion para películas: {} películas procesadas.", count);

	}

	private OmdbCache obtainCachedFilm(int count, int index, Film p) {
		OmdbCache cached = cacheService.getFromCache(p.getImdbId());
		if (cached == null) {
			extract(count, index, p.getImdbId());
			cached = cacheService.getFromCache(p.getImdbId());
		}
		return cached;
	}

	public void updateCovers() {
		int count = titlesService.findAll().size();
		int index = 1;
		for (Title p : titlesService.findAll()) {
			if (p.getImdb() == null || (!StringUtils.hasText(p.getUrlCover())
					&& !"./covers/placeholder.png".equals(p.getUrlCover()))) {
				if (p.getImdb() == null) {
					LOGGER.info("Saltando title sin IMDb ID: {} ", p.getTitle());
				} else {
					LOGGER.info("Saltando title con poster ya seteado: {} ", p.getTitle());
				}
				continue;
			}
			OmdbCache cached = obtainCachedTitle(count, index, p);

			if (cached != null) {
				obtainCoverFromOmdb(count, index, p, cached);
			} else {
				LOGGER.info("No se encontró cache, saltando: {} ", p.getTitle());
			}
		}
		LOGGER.info(
				"Terminado batch de actualización de posters para titles: {} titles procesadas.", count);

	}

	private void obtainCoverFromOmdb(int count, int index, Title p, OmdbCache cached) {
		JsonNode node = mapper.valueToTree(cached.getJson());
		if (node.get("Poster") == null) {
			LOGGER.info("Saltando title sin Poster: {} ", p.getTitle());
		} else {
			String poster = node.get("Poster").asText();

			p.setUrlCover(poster);

			titlesService.save(p);
			LOGGER.info("Actualizada película: {} con poster: {} en posición {}/{} ", p.getTitle(), poster,
					index,
					count);
		}
	}

	private OmdbCache obtainCachedTitle(int count, int index, Title p) {
		OmdbCache cached = cacheService.getFromCache(p.getImdb().getTconst());
		if (cached == null) {
			extract(count, index, p.getImdb().getTconst());
			cached = cacheService.getFromCache(p.getImdb().getTconst());
		}
		return cached;
	}
}
