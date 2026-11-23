package lugus.api.imdb;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
@ResponseBody
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
		film.setMgmntTitle(dto.getTitleMgmt());
		film.setYear(dto.getYear());
		film.setGenre(Genre.getById(dto.getGenreCode()));
		
		return film;
	}

	private String translate(String firstGenre) {
		String result = null;
		switch(firstGenre){
		case "Action":
			result = Genre.ACCION.getCodigo();
			break;
		case "Adventure":
			result = Genre.AVENTURA.getCodigo();
			break;
		case "Animation":
			result = Genre.ANIMACION.getCodigo();
			break;
		case "Biography", "Drama", "History":
			result = Genre.DRAMA.getCodigo();
			break;
		case "Comedy":
			result = Genre.COMEDIA.getCodigo();
			break;
		case "Crime", "Film-Noir":
			result = Genre.CRIMEN.getCodigo();
			break;
		case "Documentary":
			result = Genre.DOCUMENTAL.getCodigo();
			break;
		case "Family":
			result = Genre.INFANTIL.getCodigo();
			break;
		case "Fantasy":
			result = Genre.FANTASIA.getCodigo();
			break;
		case "Horror":
			result = Genre.TERROR.getCodigo();
			break;
		case "Music", "Musical":
			result = Genre.MUSICAL.getCodigo();
			break;
		case "Mystery":
			result = Genre.MISTERIO.getCodigo();
			break;
		case "Romance":
			result = Genre.ROMANTICA.getCodigo();
			break;
		case "Sci-Fi":
			result = Genre.CIENCIA_FICCION.getCodigo();
			break;
		case "Sport":
			result = Genre.DEPORTES.getCodigo();
			break;
		case "Thriller":
			result = Genre.THRILLER.getCodigo();
			break;
		case "War":
			result = Genre.BELICO.getCodigo();
			break;
		case "Western":
			result = Genre.WESTERN.getCodigo();
			break;
		default:
			result = null;
		}
		
		return result;
	}

	
	
}
