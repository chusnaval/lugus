package lugus;

import java.util.List;
import java.util.Set;

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

@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {

	private final PeliculaService service;

    public PeliculaController(PeliculaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pelicula> list() {
        return service.findAll();
    }

    @PostMapping
    public Pelicula crear(@Valid @RequestBody Pelicula p) {
        return service.save(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pelicula> findById(@PathVariable Integer id) {
        return service.findById(id)
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pelicula> actualizar(@PathVariable Integer id,
    		@Valid  @RequestBody Pelicula nuevo) {
        return service.findById(id).map(existing -> {
            existing.setTitulo(nuevo.getTitulo());
            existing.setFormato(nuevo.getFormato());
            existing.setLocalizacion(nuevo.getLocalizacion());
            existing.setAnyo(nuevo.getAnyo());
            existing.setGenero(nuevo.getGenero());
            existing.setCodigo(nuevo.getCodigo());
            existing.setNotas(nuevo.getNotas());
            existing.setSteelbook(nuevo.isSteelbook());
            existing.setFunda(nuevo.isFunda());
            service.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{packId}/contenidos")
    public ResponseEntity<Set<Pelicula>> obtenerContenido(@PathVariable Integer packId) {
        return service.findById(packId)
                   .map(p -> ResponseEntity.ok(p.getPeliculasPack()))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{packId}/agregar")
    public ResponseEntity<Pelicula> agregarAlPack(
            @PathVariable Integer packId,
            @RequestBody List<Integer> idsPeliculas) {

        return service.findById(packId).map(pack -> {
            List<Pelicula> peliculas = service.findAllById(idsPeliculas);
            pack.getPeliculasPack().addAll(peliculas);
            service.save(pack);
            return ResponseEntity.ok(pack);
        }).orElse(ResponseEntity.notFound().build());
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
