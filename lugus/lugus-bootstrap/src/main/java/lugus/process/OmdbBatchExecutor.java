package lugus.process;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.transaction.Transactional;
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

	@Autowired
	public OmdbBatchExecutor(@Value("${omdb.api.key}") String apiKey, PeliculaService peliculaService,
			SeriesService serieService, TitlesService titlesService, OmdbCacheService cacheService) {
		this.peliculaService = peliculaService;
		this.serieService = serieService;
		this.cacheService = cacheService;
		this.titlesService = titlesService;
		this.apiKey = apiKey;
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
			if(s.getImdb() == null) {
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
}
