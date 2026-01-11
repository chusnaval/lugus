package lugus.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbDirectorFilm;
import lugus.service.ImdbDirectorFilmService;

@Controller
@RequestMapping("/directors")
@RequiredArgsConstructor
public class ImdbDirectorFilmController {

	private final ImdbDirectorFilmService imdbDirectorFilmService;

	@GetMapping(value = "/find/film/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<ImdbDirectorFilm> findByTitle(@PathVariable final String title) {
		String aux = "";
		if (title != null && !title.isBlank()) {

			Optional<String> longest = Arrays.stream(title.split("[^\\p{L}]+")).filter(word -> !word.isEmpty())
					.max(Comparator.comparingInt(String::length));
			if (longest.isPresent()) {
				aux = longest.get();
			}
		}
		return imdbDirectorFilmService.findByTitle(aux.toLowerCase());

	}

}
