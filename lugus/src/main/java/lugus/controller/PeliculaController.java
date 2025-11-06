package lugus.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Formato;
import lugus.model.Genero;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.service.LocalizacionService;
import lugus.service.PeliculaService;

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

	private final PeliculaService service;
	private final LocalizacionService locService;

	public PeliculaController(PeliculaService service, LocalizacionService locService) {
		this.service = service;
		this.locService = locService;
	}

	@GetMapping
	public List<Pelicula> list() {
		return service.findAll();
	}

	@PostMapping
	public Pelicula crear(@Valid @RequestBody PeliculaCreateDto p) {
		return service.crear(p);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Pelicula> findById(@PathVariable Integer id) {
		return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Pelicula> actualizar(@PathVariable Integer id, @Valid @RequestBody PeliculaCreateDto nuevo) {
		return service.findById(id).map(existing -> {
			Formato formato = Formato.getById(nuevo.getFormatoCodigo());
			Genero genero = Genero.getById(nuevo.getGeneroCodigo());

			Localizacion loc = null;
			if (nuevo.getLocalizacionCodigo() != null && !nuevo.getLocalizacionCodigo().isBlank()) {
				loc = locService.findById(nuevo.getLocalizacionCodigo())
						.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
			}

			existing.setTitulo(nuevo.getTitulo());
			existing.setFormato(formato);
			existing.setLocalizacion(loc);
			existing.setAnyo(nuevo.getAnyo());
			existing.setGenero(genero);
			existing.setNotas(nuevo.getNotas());
			existing.setSteelbook(nuevo.isSteelbook());
			existing.setFunda(nuevo.isFunda());
			existing.calcularCodigo();
			service.save(existing);
			return ResponseEntity.ok(existing);
		}).orElse(ResponseEntity.notFound().build());
	}

	

	@GetMapping("/{packId}/contenidos")
	public ResponseEntity<Set<Pelicula>> obtenerContenido(@PathVariable Integer packId) {
		return service.findById(packId).map(p -> ResponseEntity.ok(p.getPeliculasPack()))
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{padreId}/children")
	public ResponseEntity<Pelicula> addChild(@PathVariable Integer padreId, @Valid @RequestBody PeliculaCreateDto dto) {
		Pelicula hijo = service.addChild(padreId, dto);
		return ResponseEntity.ok(hijo);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> borrar(@PathVariable Integer id) {
		if (!service.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
