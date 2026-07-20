package lugus.service.core;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lugus.model.films.Pelicula;
import lugus.repository.films.PeliculaRepository;

@Service
public class SynopsisBatchService {

    private final PeliculaRepository repo;
    private final TranslationService translator;

    public SynopsisBatchService(PeliculaRepository repo, TranslationService translator) {
        this.repo = repo;
        this.translator = translator;
    }

    @Transactional
    public int translateAllPending() {

        List<Pelicula> pending = repo.findAll().stream()
            .filter(p -> p.getSynopsis() != null)
            .filter(p -> !p.isSynopsisTranslated())
            .toList();

        for (Pelicula p : pending) {
            String original = p.getSynopsis();

            String translated = translator.translate(original);

            p.setSynopsis(translated);
            p.setSynopsisTranslated(true);
        }

        return pending.size();
    }
}
