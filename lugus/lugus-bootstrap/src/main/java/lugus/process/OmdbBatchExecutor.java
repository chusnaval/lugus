package lugus.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lugus.dto.core.CountryCode;
import lugus.model.films.Pelicula;
import lugus.model.imdb.OmdbCache;
import lugus.model.series.Serie;
import lugus.model.titles.Title;
import lugus.service.films.PeliculaService;
import lugus.service.imdb.OmdbCacheService;
import lugus.service.series.SeriesService;
import lugus.service.titles.TitlesService;

@Service
public class OmdbBatchExecutor {

	private final PeliculaService peliculaService;
	private final SeriesService serieService;
	private final OmdbCacheService cacheService;
	private final TitlesService titlesService;
	private final String apiKey;
	private final RestTemplate rest = new RestTemplate();
	private final ObjectMapper mapper;

	@Autowired
	public OmdbBatchExecutor(@Value("${omdb.api.key}") String apiKey, PeliculaService peliculaService,
			SeriesService serieService, TitlesService titlesService, OmdbCacheService cacheService,
			ObjectMapper mapper) {
		this.peliculaService = peliculaService;
		this.serieService = serieService;
		this.cacheService = cacheService;
		this.titlesService = titlesService;
		this.apiKey = apiKey;
		this.mapper = mapper;
	}

	@Transactional
	public void fillCache() throws JsonProcessingException {
		updatePeliculas();
		System.out.println("✔ Batch películas completado");
		updateSeries();
		System.out.println("✔ Batch series completado");
		updateTitles();
		System.out.println("✔ Batch todos completado");
	}

	private void updateTitles() throws JsonProcessingException {
		List<Title> title = titlesService.findAll();

		int total = title.size();
		int index = 1;

		for (Title s : title) {
			if (s.getImdb() == null) {
				System.out.println("Saltando title sin IMDb: " + s.getTitle());
				continue;
			}
			String imdbId = s.getImdb().getTconst();

			if (imdbId == null || imdbId.isBlank()) {
				System.out.println("Saltando title sin IMDb ID: " + s.getTitle());
				continue;
			}

			OmdbCache cached = cacheService.getFromCache(imdbId);
			if (cached != null) {
				System.out.println(index + "/" + total + " — Ya cacheada: " + imdbId);
				index++;
				continue;
			}

			extraer(total, index, imdbId);

			index++;
		}

	}

	private void updateSeries() throws JsonProcessingException {
		List<Serie> series = serieService.findAll();

		int total = series.size();
		int index = 1;

		for (Serie s : series) {
			String imdbId = s.getImdbId();

			if (imdbId == null || imdbId.isBlank()) {
				System.out.println("Saltando serie sin IMDb ID: " + s.getTitulo());
				continue;
			}

			OmdbCache cached = cacheService.getFromCache(imdbId);
			if (cached != null) {
				System.out.println(index + "/" + total + " — Ya cacheada: " + imdbId);
				index++;
				continue;
			}

			extraer(total, index, imdbId);

			index++;
		}
	}

	@SuppressWarnings("unchecked")
	private void extraer(int total, int index, String imdbId) {
		try {
			System.out.println(index + "/" + total + " — Cacheando: " + imdbId);
			String url = "https://www.omdbapi.com/?i=" + imdbId + "&apikey=" + apiKey + "&plot=full";
			Map<String, Object> json = rest.getForObject(url, Map.class);

			cacheService.saveToCache(imdbId, json);

			Thread.sleep(500); // evitar rate limit
		} catch (Exception e) {
			System.err.println("Error cacheando " + imdbId + ": " + e.getMessage());
		}
	}

	private void updatePeliculas() throws JsonProcessingException {
		List<Pelicula> peliculas = peliculaService.findAll();

		int total = peliculas.size();
		int index = 1;

		for (Pelicula p : peliculas) {
			String imdbId = p.getImdbId();

			if (imdbId == null || imdbId.isBlank()) {
				System.out.println("Saltando película sin IMDb ID: " + p.getTitulo());
				continue;
			}

			OmdbCache cached = cacheService.getFromCache(imdbId);
			if (cached != null) {
				System.out.println(index + "/" + total + " — Ya cacheada: " + imdbId);
				index++;
				continue;
			}

			extraer(total, index, imdbId);

			index++;
		}
	}

	public void updateCountry() {
		int count = peliculaService.findAll().size();
		int index = 1;
		for (Pelicula p : peliculaService.findAll()) {
			if (p.getImdbId() == null || p.getImdbId().isBlank()) {
				System.out.println("Saltando película sin IMDb ID: " + p.getTitulo());
				continue;
			}
			if (p.getCountry() != null && !p.getCountry().isBlank()) {
				System.out.println("Saltando película con país ya seteado: " + p.getTitulo());
				continue;
			}
			OmdbCache cached = cacheService.getFromCache(p.getImdbId());
			if(cached == null) {
				extraer(count, index, p.getImdbId());
				cached = cacheService.getFromCache(p.getImdbId());
			}
			if (cached != null) {
				JsonNode node = mapper.valueToTree(cached.getJson());
				String country = node.get("Country").asText();

				// puede tener valor o no
				// o tener uno o varios países separados por coma
				List<String> values = new ArrayList<>();
				if (country != null) {
					String[] countries = country.split(",");
					for (int i = 0; i < countries.length; i++) {
						values.add(CountryCode.fromString(countries[i]).getCode());
					}
				}

				// los guardamosen un campo separados por coma
				p.setCountry(String.join(",", values));

				peliculaService.save(p);
				System.out.println("Actualizada película: " + p.getTitulo() + " con país: " + country + " en posición "
						+ index + "/" + count);
			} else {
				System.out.println("No se encontró en cache, saltando: " + p.getTitulo());
			}
		}
		System.out.println(
				"Terminado batch de actualización de país para películas: " + count + " películas procesadas.");
	}

	public void updateRuntime() {
		int count = peliculaService.findAll().size();
		int index = 1;
		for (Pelicula p : peliculaService.findAll()) {
			if (p.getImdbId() == null || p.getImdbId().isBlank()) {
				System.out.println("Saltando película sin IMDb ID: " + p.getTitulo());
				continue;
			}
			if (p.getDuration() != 0 ) {
				System.out.println("Saltando película con duracion ya seteado: " + p.getTitulo());
				continue;
			}
			OmdbCache cached = cacheService.getFromCache(p.getImdbId());
			if(cached == null) {
				extraer(count, index, p.getImdbId());
				cached = cacheService.getFromCache(p.getImdbId());
			}
			if (cached != null) {
				JsonNode node = mapper.valueToTree(cached.getJson());
				String runtime = node.get("Runtime").asText();

				Matcher m = Pattern.compile("(\\d+)").matcher(runtime);
				int minutes = m.find() ? Integer.parseInt(m.group(1)) : 0;
				
				p.setDuration(minutes);

				peliculaService.save(p);
				System.out.println("Actualizada película: " + p.getTitulo() + " con runtime: " + minutes + " en posición "
						+ index++ + "/" + count);
			} else {
				System.out.println("No se encontró en cache, saltando: " + p.getTitulo());
			}
		}
		System.out.println(
				"Terminado batch de actualización de duracion para películas: " + count + " películas procesadas.");

	}

	public void updateCovers() {
		int count = titlesService.findAll().size();
		int index = 1;
		for (Title p : titlesService.findAll()) {
			if (p.getImdb() == null) {
				System.out.println("Saltando title sin IMDb ID: " + p.getTitle());
				continue;
			}
			if (p.getPosterUrl() != null && !p.getPosterUrl().isBlank() && !"./covers/placeholder.png".equals(p.getPosterUrl())) {
				System.out.println("Saltando title con poster ya seteado: " + p.getTitle());
				continue;
			}
			OmdbCache cached = cacheService.getFromCache(p.getImdb().getTconst());
			if(cached == null) {
				extraer(count, index, p.getImdb().getTconst());
				cached = cacheService.getFromCache(p.getImdb().getTconst());
			}
			
			if (cached != null) {
				JsonNode node = mapper.valueToTree(cached.getJson());
				if(node.get("Poster")==null) {
					System.out.println("Saltando title sin Poster: " + p.getTitle());
					continue;
				}
				String poster = node.get("Poster").asText();
				
				p.setPosterUrl(poster);

				titlesService.save(p);
				System.out.println("Actualizada title: " + p.getTitle() + " con poster: " + poster + " en posición "
						+ index++ + "/" + count);
			} else {
				System.out.println("No se encontró en cache, saltando: " + p.getTitle());
			}
		}
		System.out.println(
				"Terminado batch de actualización de posters para titles: " + count + " titles procesadas.");

		
	}
}
