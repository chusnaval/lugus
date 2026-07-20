package lugus.process;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lugus.service.core.SynopsisBatchService;

@RestController
@RequestMapping("/v1/api/batch")
public class TranslateBatchController {

    private final SynopsisBatchService batchService;

    public TranslateBatchController(SynopsisBatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping("/translate-synopsis")
    public String translateAll() {
        int count = batchService.translateAllPending();
        return "Sinopsis traducidas: " + count;
    }
}
