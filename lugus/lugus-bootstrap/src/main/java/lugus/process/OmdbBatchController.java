package lugus.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/omdb/batch")
public class OmdbBatchController {

    private final OmdbBatchExecutor omdbBatchExecutor;

    @Autowired
    public OmdbBatchController(OmdbBatchExecutor omdbBatchExecutor) {
        this.omdbBatchExecutor = omdbBatchExecutor;
    }

    @PostMapping("/update-cache")
    public ResponseEntity<String> fillCache() throws JsonProcessingException {
    	omdbBatchExecutor.fillCache();
        return ResponseEntity.ok("Batch iniciado");
    }
    
    /**
     * Endpoit para actualizar los datos de pais de las peliculas
     * a partir de la informacion de OMDB. Esto se hace porque en IMDB
     * no se tiene la informacion de pais, y se necesita para el proceso de recomendacion.
     */
    @PostMapping("/update-country")
    public ResponseEntity<String> updateCountry() throws JsonProcessingException {
		omdbBatchExecutor.updateCountry();
		return ResponseEntity.ok("Batch de actualizacion de pais iniciado");
	}
}
