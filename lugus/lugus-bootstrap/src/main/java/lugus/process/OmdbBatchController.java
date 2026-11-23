package lugus.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/omdb/batch")
public class OmdbBatchController {

    private final OmdbBatchExecutor omdbBatchExecutor;

    @Autowired
    public OmdbBatchController(OmdbBatchExecutor omdbBatchExecutor) {
        this.omdbBatchExecutor = omdbBatchExecutor;
    }

    @PostMapping("/update-cache")
    public ResponseEntity<String> fillCache() {
    	omdbBatchExecutor.fillCache();
        return ResponseEntity.ok("Batch initiated");
    }
    
    /**
     * Endpoit to update country in films collection
     * Reads from OMDB. In IMDB export info we haven't the neccesary country info.
     * We want this info for recommendation utils
     */
    @PostMapping("/update-country")
    public ResponseEntity<String> updateCountry() {
		omdbBatchExecutor.updateCountry();
		return ResponseEntity.ok("Update country batch initiated");
	}
    
    /**
     * Endpoit to update length in films collection
     * Reads from OMDB. In IMDB export info we haven't the neccesary length info.
     * We want this info for recommendation utils
     */
    @PostMapping("/update-runtime")
    public ResponseEntity<String> updateRuntime() {
		omdbBatchExecutor.updateRuntime();
		return ResponseEntity.ok("Update length batch initiated");
	}
    
   /**
     * Endpoit to update cover in films collection
     * Reads from OMDB. In IMDB export info we haven't the neccesary cover info.
     * We want this info for recommendation utils
     */
    @PostMapping("/update-covers")
    public ResponseEntity<String> updateCovers() {
		omdbBatchExecutor.updateCovers();
		return ResponseEntity.ok("Update covers batch initiated");
	}
}
