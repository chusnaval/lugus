package lugus.process;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.service.films.EditionService;
import lugus.service.films.FilmService;

@RestController
@RequestMapping("/api/code")
public class CodeUpdaterController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodeUpdaterController.class);
	private final FilmService filmService;
	private final EditionService editionService;

	@Autowired
	public CodeUpdaterController(FilmService filmService, EditionService editionService) {
		this.filmService = filmService;
		this.editionService = editionService;
	}

	@GetMapping("/findDiffs")
	public ResponseEntity<String> findDiffs() {
		List<Film> films = filmService.findAll();
		int total = films.size();
		int matches = 0;
		int diffs = 0;
		for (Film film : films) {
			for (Edition edition : film.getEditions()) {
				String expected = editionService.calculateCompleteCode(edition.getId(), film);
				String actual = edition.getCode();

				if (!actual.equals(expected)) {
					diffs++;
					LOGGER.info("Actual film code doesn't match: {} - {} Actual Code {} Expected {} ", film.getId(),
							film.getTitle(), actual, expected);
				} else {
					matches++;
				}
			}
		}
		LOGGER.info("Matches: {}", matches);
		LOGGER.info("Diffs: {}", diffs);
		LOGGER.info("Total: {}", total);
		return ResponseEntity.ok("Batch finished");
	}

	@PostMapping("/update")
	public ResponseEntity<String> update() {
		List<Film> films = filmService.findAll();
		int total = films.size();
		int matches = 0;
		int diffs = 0;
		for (Film film : films) {
			for (Edition edition : film.getEditions()) {
				String expected = editionService.calculateCompleteCode(edition.getId(), film);
				String actual = edition.getCode();

				if (!actual.equals(expected)) {
					diffs++;
					edition.setCode(expected);
					editionService.update(edition);
					LOGGER.info("Actual film code doesn't match: {} - {} Actual Code {} Expected {} ", film.getId(),
							film.getTitle(), actual, expected);
				} else {
					matches++;
				}
			}
		}
		LOGGER.info("Matches: {}", matches);
		LOGGER.info("Diffs: {}", diffs);
		LOGGER.info("Total: {}", total);
		return ResponseEntity.ok("Batch finished");
	}
}
