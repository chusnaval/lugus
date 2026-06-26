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
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.service.imdb.ImdbTitleAkasService;
import lugus.service.imdb.ImdbTitleBasicsService;

@RestController
@ResponseBody
@RequestMapping("/v1/api/imdb")
@RequiredArgsConstructor
public class ImdbApiController {


	private final ImdbTitleBasicsService itbService;

	private final ImdbTitleAkasService itaService;

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
			edto.setFormat(new FormatDTO(""+Formato.DVD.getId(), Formato.DVD.name()));
			
			if(film.getGenres().length>0) {
				String firstGenre = translate(film.getGenres()[0]);
				String genres = String.join(",", film.getGenres());
				dto.setGenreCode(firstGenre);
				dto.setGenreDesc(Genero.getById(firstGenre).getDisplayName());
				edto.setNotes(genres);
				edto.setMgmtCode(calcularCodigo(dto));
			}
			dto.addEdition(edto);
			

			dto.setImdbId(id);

			return dto;

		}
		return null;
	}

	private String translate(String firstGenre) {
		String result = Genero.ACCION.getCodigo();
		switch(firstGenre){
		case "Action":
			result = Genero.ACCION.getCodigo();
			break;
		case "Adventure":
			result = Genero.AVENTURA.getCodigo();
			break;
		case "Animation":
			result = Genero.ANIMACION.getCodigo();
			break;
		case "Biography":
		case "Drama":
		case "History":
			result = Genero.DRAMA.getCodigo();
			break;
		case "Comedy":
			result = Genero.COMEDIA.getCodigo();
			break;
		case "Crime":
		case "Film-Noir":
			result = Genero.CRIMEN.getCodigo();
			break;
		case "Documentary":
			result = Genero.DOCUMENTAL.getCodigo();
			break;
		case "Family":
			result = Genero.INFANTIL.getCodigo();
			break;
		case "Fantasy":
			result = Genero.FANTASIA.getCodigo();
			break;
		case "Horror":
			result = Genero.TERROR.getCodigo();
			break;
		case "Music":
		case "Musical":
			result = Genero.MUSICAL.getCodigo();
			break;
		case "Mystery":
			result = Genero.MISTERIO.getCodigo();
			break;
		case "Romance":
			result = Genero.ROMANTICA.getCodigo();
			break;
		case "Sci-Fi":
			result = Genero.CIENCIA_FICCION.getCodigo();
			break;
		case "Sport":
			result = Genero.DEPORTES.getCodigo();
			break;
		case "Thriller":
			result = Genero.THRILLER.getCodigo();
			break;
		case "War":
			result = Genero.BELICO.getCodigo();
			break;
		case "Western":
			result = Genero.WESTERN.getCodigo();
			break;
		}
		return result;
	}

	
	//TODO duplicated
	public String calcularCodigo(FilmDto dto) {
		// Eliminar artículos del título
		String procesado = dto.getTitle().replaceAll("(?i)\\b(un|the|a|an|el|la|los|las| )\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefijo = procesado.length() >= 3 ? procesado.substring(0, 3).toUpperCase() : procesado.toUpperCase();

		// Obtener la etiqueta del género
		String parteCodigo = dto.getGenreCode();

		return (parteCodigo + "-" + prefijo + "-" + dto.getYear());

	}
}
