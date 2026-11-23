package lugus.service.titles;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.SearchTitleResultDto;
import lugus.model.films.Film;
import lugus.model.series.Series;
import lugus.model.titles.Title;
import lugus.model.values.TitleType;
import lugus.repository.films.FilmRepository;
import lugus.repository.imdb.ImdbTitleBasicsRepository;
import lugus.repository.series.SerieRepository;
import lugus.repository.titles.TitleRepository;

@Service
@RequiredArgsConstructor
public class TitleCreationService {
    
    private static final Logger logger = LogManager.getLogger();

    private final TitleRepository titleRepo;
    private final FilmRepository filmRepo;
    private final SerieRepository serieRepo;
    private final ImdbTitleBasicsRepository imdbRepo;

    public Title getOrCreateTitle(SearchTitleResultDto dto) {

       


        // 1) Already exists in titles
        if (dto.getTitleId() != null) {
            return titleRepo.findById(dto.getTitleId())
                    .orElseThrow(() -> new RuntimeException("Title not found"));
        }

        // 2) if is a MOVIE there is a film in DB
        if (dto.getSource().equals("MOVIE")) {
            Optional<Film> optFilm = filmRepo.findByImdbId(dto.getImdbId());
            Film p = optFilm.get();
            Title t = new Title();
            t.setType(TitleType.MOVIE);
            t.setFilm(p);
            t.setTitle(p.getTitle());
            t.setYear(p.getYear());
            t.setUrlCover(p.getCoverUrl());
            if(p.getImdbId() != null) {
            	t.setImdb(imdbRepo.findById(p.getImdbId()).orElse(null));
            }else if(dto.getImdbId() != null) {
				t.setImdb(imdbRepo.findById(dto.getImdbId()).orElse(null));
			}else {
				logger.info("No IMDB ID for {}", p.getTitle());
			}
            return titleRepo.save(t);
        }

        // 3) its a series in the collection
        if (dto.getSource().equals("SERIES")) {
            Series s = serieRepo.findByImdbId(dto.getImdbId())
                    .orElseThrow();
            Title t = new Title();
            t.setType(TitleType.SERIES);
            t.setSeries(s);
            t.setTitle(s.getTitle());
            t.setYear(s.getStartYear());
            t.setUrlCover(s.getCoverUrl());
            t.setImdb(imdbRepo.findById(dto.getImdbId()).orElse(null));
            return titleRepo.save(t);
        }

        // 4) External to collection IMDB code
        if (dto.getSource().equals("IMDB")) {
            var imdb = imdbRepo.findById(dto.getImdbId())
                    .orElseThrow();

            Title t = new Title();
            t.setType(TitleType.EXTERNAL);
            t.setImdb(imdb);
            t.setTitle(imdb.getPrimarytitle());
            t.setYear(Integer.valueOf(imdb.getStartyear()));
            t.setUrlCover("./covers/placeholder.png"); 
            return titleRepo.save(t);
        }

        throw new RuntimeException("Unknown source");
    }
}
