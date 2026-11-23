package lugus.service.core;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lugus.model.films.Film;
import lugus.repository.films.FilmRepository;

@Service
public class SynopsisBatchService {

    private final FilmRepository repo;
    private final TranslationService translator;

    public SynopsisBatchService(FilmRepository repo, TranslationService translator) {
        this.repo = repo;
        this.translator = translator;
    }

    @Transactional
    public int translateAllPending() {

        List<Film> pending = repo.findAll().stream()
            .filter(p -> p.getSynopsis() != null)
            .filter(p -> !p.isSynopsisTranslated())
            .toList();

        for (Film p : pending) {
            String original = p.getSynopsis();

            String translated = translator.translate(original);

            p.setSynopsis(translated);
            p.setSynopsisTranslated(true);
        }

        return pending.size();
    }
}
