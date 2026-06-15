package lugus.service.titles;

import org.springframework.stereotype.Service;

import lugus.dto.external.ExternalTitleDto;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.imdb.OmdbCache;
import lugus.repository.imdb.ImdbTitleAkasRepository;
import lugus.repository.imdb.ImdbTitleBasicsRepository;
import lugus.repository.imdb.OmdbCacheRepository;

@Service
public class ExternalTitleService {

    private final ImdbTitleBasicsRepository basicsRepo;
    private final ImdbTitleAkasRepository akasRepo;
    private final OmdbCacheRepository omdbRepo;

    public ExternalTitleService(
    		ImdbTitleBasicsRepository basicsRepo,
    		ImdbTitleAkasRepository akasRepo,
    		OmdbCacheRepository omdbRepo
    ) {
        this.basicsRepo = basicsRepo;
        this.akasRepo = akasRepo;
        this.omdbRepo = omdbRepo;
    }

    public ExternalTitleDto getExternalTitle(String tconst) {

    	ImdbTitleBasics basics = basicsRepo.findById(tconst)
            .orElseThrow(() -> new RuntimeException("Title no encontrado"));

        ImdbTitleAkas originalAka = akasRepo.findByTitleId(tconst).orElse(null);

        OmdbCache omdb = omdbRepo.findById(tconst).orElse(null);

        return new ExternalTitleDto(
            basics.getTconst(),
            basics.getPrimarytitle(),
            originalAka != null ? originalAka.getTitle() : basics.getPrimarytitle(),
            basics.getTitletype(),
            basics.getStartyear(),
            basics.getEndyear(),
            String.valueOf(basics.getRuntimeminutes()),
            omdb!=null? omdb.getJson().get("Genre").asText():"",
            omdb!=null?omdb.getJson().get("imdbRating").asText():"",
            omdb!=null?omdb.getJson().get("imdbVotes").asText():"",
            omdb!=null?omdb.getJson().get("Poster").asText():"",
            omdb!=null?omdb.getJson().get("Plot").asText():"",
            omdb!=null?omdb.getJson().get("Director").asText():"",
            omdb!=null?omdb.getJson().get("Actors").asText():""
        );
    }
}
