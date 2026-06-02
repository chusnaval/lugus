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
}
