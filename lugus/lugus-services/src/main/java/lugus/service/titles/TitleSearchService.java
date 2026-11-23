package lugus.service.titles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.SearchTitleResultDto;
import lugus.repository.films.FilmRepository;
import lugus.repository.imdb.ImdbTitleBasicsRepository;
import lugus.repository.series.SerieRepository;
import lugus.repository.titles.TitleRepository;

@Service
@RequiredArgsConstructor
public class TitleSearchService {

    private static final String INTERNAL = "INTERNAL";
	private static final String MOVIE = "MOVIE";
	private static final String SERIES = "SERIES";
	private final TitleRepository titleRepo;
    private final FilmRepository filmRepo;
    private final SerieRepository seriesRepo;
    private final ImdbTitleBasicsRepository imdbRepo;
    
    private String[] notAllowedForTypes = {"videoGame","tvEpisode", "tvPilot"};

    public List<SearchTitleResultDto> search(String query) {

        List<SearchTitleResultDto> results = new ArrayList<>();

        // 1) Internal Titles 
        titleRepo.searchByTitleContainingIgnoreCase(query).forEach(t -> {
            SearchTitleResultDto dto = new SearchTitleResultDto();
            dto.setTitleId(t.getId());
            dto.setSource(INTERNAL);
            dto.setTitle(t.getTitle());
            dto.setYear(t.getYear());
            dto.setType(t.getType().name());
            dto.setPosterUrl(t.getUrlCover());
            dto.setImdbId(t.getImdb() != null ? t.getImdb().getTconst() : null);
            results.add(dto);
        });

        // 2) Films in the collection
        filmRepo.findByTitleContainingIgnoreCase(query).forEach(p -> {
            SearchTitleResultDto dto = new SearchTitleResultDto();
            dto.setSource(MOVIE);
            dto.setTitle(p.getTitle());
            dto.setYear(p.getYear());
            dto.setType(MOVIE);
            dto.setPosterUrl(p.getCoverUrl());
            dto.setImdbId(p.getImdbId());
            results.add(dto);
        });

        // 3) series in the collection
        seriesRepo.searchByTitleContainingIgnoreCase(query).forEach(s -> {
            SearchTitleResultDto dto = new SearchTitleResultDto();
            dto.setSource(SERIES);
            dto.setTitle(s.getTitle());
            dto.setYear(s.getStartYear());
            dto.setType(SERIES);
            dto.setPosterUrl(s.getCoverUrl());
            dto.setImdbId(s.getImdbId());
            results.add(dto);
        });

        // 4) IMDB (only if its not in titles list)
        imdbRepo.searchByPrimarytitleContainingIgnoreCase(query).forEach(imdb -> {
            boolean exists = titleRepo.findByImdb_Tconst(imdb.getTconst()).isPresent();
            boolean allowed = !Arrays.asList(notAllowedForTypes).contains(imdb.getTitletype());
            if (!exists && allowed) {
                SearchTitleResultDto dto = new SearchTitleResultDto();
                dto.setSource("IMDB");
                dto.setTitle(imdb.getPrimarytitle());
                if(!ObjectUtils.isEmpty(imdb.getStartyear())) {
                	dto.setYear(Integer.valueOf(imdb.getStartyear()));
                }
                dto.setType(imdb.getTitletype().equals("tvSeries") ? SERIES : MOVIE);
                dto.setPosterUrl("/covers/placeholder.png"); 
                dto.setImdbId(imdb.getTconst());
                results.add(dto);
            }
        });

        return results;
    }
}
