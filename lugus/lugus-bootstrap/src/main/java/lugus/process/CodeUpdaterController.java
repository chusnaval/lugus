package lugus.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import lugus.model.films.Edicion;
import lugus.model.films.Pelicula;
import lugus.service.films.EdicionService;
import lugus.service.films.PeliculaService;

@RestController
@RequestMapping("/api/code")
public class CodeUpdaterController {

	private final PeliculaService peliculaService;
	private final EdicionService edicionService;

	@Autowired
	public CodeUpdaterController(PeliculaService peliculaService, EdicionService edicionService) {
		this.peliculaService = peliculaService;
		this.edicionService = edicionService;
	}

	@GetMapping("/findDiffs")
	public ResponseEntity<String> findDiffs() throws JsonProcessingException {
		List<Pelicula> peliculas = peliculaService.findAll();
		int total = peliculas.size();
		int correctos = 0;
		int distintos = 0;
		for (Pelicula pelicula : peliculas) {
			for (Edicion edicion : pelicula.getEditions()) {
				String expected = edicionService.calculateCompleteCode(edicion.getId(), pelicula);
				String actual = edicion.getCodigo();

				if (!actual.equals(expected)) {
					distintos++;
					System.out.println("El código no se corresponde en la pelicula: " + pelicula.getId() + " - "
							+ pelicula.getTitulo() + " Codigo Actual " + actual + " Esperado " + expected);
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
		List<Pelicula> peliculas = peliculaService.findAll();
		int total = peliculas.size();
		int correctos = 0;
		int distintos = 0;
		for (Pelicula pelicula : peliculas) {
			for (Edicion edicion : pelicula.getEditions()) {
				String expected = edicionService.calculateCompleteCode(edicion.getId(), pelicula);
				String actual = edicion.getCodigo();

				if (!actual.equals(expected)) {
					distintos++;
					edicion.setCodigo(expected);
					edicionService.update(edicion);
					System.out.println("El código no se corresponde en la pelicula: " + pelicula.getId() + " - "
							+ pelicula.getTitulo() + " Codigo Actual " + actual + " Esperado " + expected);
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
