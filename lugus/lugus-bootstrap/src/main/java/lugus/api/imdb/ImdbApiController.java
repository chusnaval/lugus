package lugus.api.imdb;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FormatDTO;
import lugus.dto.films.EditionDto;
import lugus.dto.films.FilmDto;
import lugus.exception.LugusNotFoundException;
import lugus.model.films.Film;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.values.Format;
import lugus.model.values.Genre;
import lugus.service.films.EdicionService;
import lugus.service.imdb.ImdbTitleAkasService;
import lugus.service.imdb.ImdbTitleBasicsService;

@RestController
@RequestMapping("/v1/api/imdb")
@RequiredArgsConstructor
public class ImdbApiController {


	private final ImdbTitleBasicsService itbService;

	private final ImdbTitleAkasService itaService;
	
	private final EdicionService edicionService;

	@GetMapping("/{id}")
	FilmDto one(@PathVariable String id) throws LugusNotFoundException {
		Optional<ImdbTitleBasics> optItb = itbService.findById(id);
		if (optItb.isPresent()) {
			ImdbTitleBasics film = optItb.get();
			FilmDto dto = new FilmDto();

			Optional<ImdbTitleAkas> akas = itaService.findByTitleId(id);
			if (akas.isPresent()) {
				dto.setTitle(akas.get().getTitle());
				dto.setTitleMgmt(akas.get().getTitle());
			} else {
				dto.setTitle(film.getOriginaltitle());
				dto.setTitleMgmt(film.getOriginaltitle());
			}
			try {
				dto.setYear(Integer.parseInt(film.getStartyear()));
			}catch(Exception e) {
				// we don't stop in case of a problem with start year info
			}
			
			EditionDto edto = new EditionDto();
			edto.setFormat(new FormatDTO(""+Format.DVD.getId(), Format.DVD.name()));
			
			if(film.getGenres().length>0) {
				String firstGenre = translate(film.getGenres()[0]);
				String genres = String.join(",", film.getGenres());
				dto.setGenreCode(firstGenre);
				dto.setGenreDesc(Genre.getById(firstGenre).getDisplayName());
				edto.setNotes(genres);
				edto.setMgmtCode(edicionService.calculateCompleteCode(-1, createFakeFilm(dto)));
			}
			dto.addEdition(edto);
			

			dto.setImdbId(id);

			return dto;

		}
		return null;
	}

	private Film createFakeFilm(FilmDto dto) {
		Film film = new Film();
		film.setMgmtTitle(dto.getTitleMgmt());
		film.setYear(dto.getYear());
		film.setGenre(Genre.getById(dto.getGenreCode()));
		
		return film;
	}

	private String translate(String firstGenre) {
		String result = null;
		switch(firstGenre){
		case "Action":
			result = Genre.ACCION.getCode();
			break;
		case "Adventure":
			result = Genre.AVENTURA.getCode();
			break;
		case "Animation":
			result = Genre.ANIMACION.getCode();
			break;
		case "Biography", "Drama", "History":
			result = Genre.DRAMA.getCode();
			break;
		case "Comedy":
			result = Genre.COMEDIA.getCode();
			break;
		case "Crime", "Film-Noir":
			result = Genre.CRIMEN.getCode();
			break;
		case "Documentary":
			result = Genre.DOCUMENTAL.getCode();
			break;
		case "Family":
			result = Genre.INFANTIL.getCode();
			break;
		case "Fantasy":
			result = Genre.FANTASIA.getCode();
			break;
		case "Horror":
			result = Genre.TERROR.getCode();
			break;
		case "Music", "Musical":
			result = Genre.MUSICAL.getCode();
			break;
		case "Mystery":
			result = Genre.MISTERIO.getCode();
			break;
		case "Romance":
			result = Genre.ROMANTICA.getCode();
			break;
		case "Sci-Fi":
			result = Genre.CIENCIA_FICCION.getCode();
			break;
		case "Sport":
			result = Genre.DEPORTES.getCode();
			break;
		case "Thriller":
			result = Genre.THRILLER.getCode();
			break;
		case "War":
			result = Genre.BELICO.getCode();
			break;
		case "Western":
			result = Genre.WESTERN.getCode();
			break;
		default:
			result = null;
		}
		
		return result;
	}

	
	
}
