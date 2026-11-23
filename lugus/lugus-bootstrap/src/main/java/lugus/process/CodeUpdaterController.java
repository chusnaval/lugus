package lugus.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.service.films.EdicionService;
import lugus.service.films.FilmService;

@RestController
@RequestMapping("/api/code")
public class CodeUpdaterController {

	private final FilmService peliculaService;
	private final EdicionService edicionService;

	@Autowired
	public CodeUpdaterController(FilmService peliculaService, EdicionService edicionService) {
		this.peliculaService = peliculaService;
		this.edicionService = edicionService;
	}

	@GetMapping("/findDiffs")
	public ResponseEntity<String> findDiffs() throws JsonProcessingException {
		List<Film> peliculas = peliculaService.findAll();
		int total = peliculas.size();
		int correctos = 0;
		int distintos = 0;
		for (Film pelicula : peliculas) {
			for (Edition edicion : pelicula.getEditions()) {
				String expected = edicionService.calculateCompleteCode(edicion.getId(), pelicula);
				String actual = edicion.getCode();

				if (!actual.equals(expected)) {
					distintos++;
					System.out.println("El código no se corresponde en la pelicula: " + pelicula.getId() + " - "
							+ pelicula.getTitle() + " Codigo Actual " + actual + " Esperado " + expected);
				}else {
					correctos++;
				}
			}
		}
		System.out.println("Correctos: " + correctos);
		System.out.println("Incorrectos: " + distintos);
		System.out.println("Total: " + total);
		return ResponseEntity.ok("Batch terminado");
	}
	
	@PostMapping("/update")
	public ResponseEntity<String> update() throws JsonProcessingException {
		List<Film> peliculas = peliculaService.findAll();
		int total = peliculas.size();
		int correctos = 0;
		int distintos = 0;
		for (Film pelicula : peliculas) {
			for (Edition edicion : pelicula.getEditions()) {
				String expected = edicionService.calculateCompleteCode(edicion.getId(), pelicula);
				String actual = edicion.getCode();

				if (!actual.equals(expected)) {
					distintos++;
					edicion.setCode(expected);
					edicionService.update(edicion);
					System.out.println("El código no se corresponde en la pelicula: " + pelicula.getId() + " - "
							+ pelicula.getTitle() + " Codigo Actual " + actual + " Esperado " + expected);
				}else {
					correctos++;
				}
			}
		}
		System.out.println("Correctos: " + correctos);
		System.out.println("Incorrectos: " + distintos);
		System.out.println("Total: " + total);
		return ResponseEntity.ok("Batch terminado");
	}
}
