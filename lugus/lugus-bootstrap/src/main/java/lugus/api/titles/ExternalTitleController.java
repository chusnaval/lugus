package lugus.api.titles;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lugus.dto.external.ExternalTitleDto;
import lugus.service.titles.ExternalTitleService;

@RestController
@RequestMapping("/v1/api/external")
public class ExternalTitleController {

    private final ExternalTitleService service;

    public ExternalTitleController(ExternalTitleService service) {
        this.service = service;
    }

    @GetMapping("/{tconst}")
    public ExternalTitleDto get(@PathVariable String tconst) {
        return service.getExternalTitle(tconst);
    }
}
