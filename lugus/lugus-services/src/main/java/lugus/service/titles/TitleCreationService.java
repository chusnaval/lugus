package lugus.service.titles;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.SearchTitleResultDto;
import lugus.model.films.Pelicula;
import lugus.model.series.Serie;
import lugus.model.titles.Title;
import lugus.model.values.TitleType;
import lugus.repository.films.PeliculaRepository;
import lugus.repository.imdb.ImdbTitleBasicsRepository;
import lugus.repository.series.SerieRepository;
import lugus.repository.titles.TitleRepository;

@Service
@RequiredArgsConstructor
public class TitleCreationService {

    private final TitleRepository titleRepo;
    private final PeliculaRepository peliculaRepo;
    private final SerieRepository serieRepo;
    private final ImdbTitleBasicsRepository imdbRepo;

    public Title getOrCreateTitle(SearchTitleResultDto dto) {

        // 1) Si ya existe en Titles
        if (dto.getTitleId() != null) {
            return titleRepo.findById(dto.getTitleId())
                    .orElseThrow(() -> new RuntimeException("Title not found"));
        }

        // 2) Si es película de colección
        if (dto.getSource().equals("MOVIE")) {
            List<Pelicula> list = peliculaRepo.findByImdbId(dto.getImdbId());
            Pelicula p = list.getFirst();
            Title t = new Title();
            t.setType(TitleType.MOVIE);
            t.setPelicula(p);
            t.setTitle(p.getTitulo());
            t.setYear(p.getAnyo());
            t.setPosterUrl(p.getCoverUrl());
            t.setImdb(imdbRepo.findById(dto.getImdbId()).orElse(null));
            return titleRepo.save(t);
        }

        // 3) Si es serie de colección
        if (dto.getSource().equals("SERIES")) {
            Serie s = serieRepo.findByImdbId(dto.getImdbId())
                    .orElseThrow();
            Title t = new Title();
            t.setType(TitleType.SERIES);
            t.setSerie(s);
            t.setTitle(s.getTitulo());
            t.setYear(s.getAnyoInicio());
            t.setPosterUrl(s.getCoverUrl());
            t.setImdb(imdbRepo.findById(dto.getImdbId()).orElse(null));
            return titleRepo.save(t);
        }

        // 4) Si es IMDB externo
        if (dto.getSource().equals("IMDB")) {
            var imdb = imdbRepo.findById(dto.getImdbId())
                    .orElseThrow();

            Title t = new Title();
            t.setType(TitleType.EXTERNAL);
            t.setImdb(imdb);
            t.setTitle(imdb.getPrimarytitle());
            t.setYear(Integer.valueOf(imdb.getStartyear()));
            t.setPosterUrl("./covers/placeholder.png"); 
            return titleRepo.save(t);
        }

        throw new RuntimeException("Unknown source");
    }
}
