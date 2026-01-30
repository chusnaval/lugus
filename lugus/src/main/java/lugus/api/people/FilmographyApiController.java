package lugus.api.people;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.model.films.Pelicula;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.model.people.Filmography;
import lugus.model.people.Persona;
import lugus.service.films.PeliculaService;
import lugus.service.films.PeliculasOtrosService;
import lugus.service.imdb.ImdbTitleAkasService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.service.imdb.ImdbTitlePrincipalsService;
import lugus.service.people.PersonaService;

@RestController
@RequestMapping("/v1/api/filmography")
@RequiredArgsConstructor
public class FilmographyApiController {

	private final PersonaService personaService;
	
	private final ImdbTitlePrincipalsService imdbTitlePrincipalsService;
	
	private final ImdbTitleBasicsService imdbTitleBasicsService;
	
	private final PeliculasOtrosService peliculasOtrosService;

	private final ImdbTitleAkasService imdbTitleAkasService;

	private final PeliculaService peliculaService;

	@GetMapping("/{id}")
	public List<Filmography> findFilmography( @PathVariable Integer id){
		Persona person = personaService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));
		
		return  buildFilmsList(person);
	}
	
	private List<Filmography> buildFilmsList(Persona person) {

		List<ImdbTitlePrincipals> aux = imdbTitlePrincipalsService.findAllByIdNconst(person.getNconst());
		List<Filmography> films = aux.stream().filter(itp -> {
			Optional<ImdbTitleBasics> itb = imdbTitleBasicsService.findById(itp.getId().getTconst());
			return itb.isPresent() && itb.get().getStartyear() != null && itb.get().isAMovie();
		}).collect(Collectors.groupingBy(itp -> itp.getId().getTconst())).values().stream().map(list -> {
			ImdbTitlePrincipals first = list.get(0);
			ImdbTitleBasics itb = imdbTitleBasicsService.findById(first.getId().getTconst()).get();
			Filmography film = buildNewFilmography(person, first, Optional.of(itb));
			list.stream().skip(1).forEach(itp -> film.appendCategory(itp.getId().getCategory()));

			return film;
		}).collect(Collectors.toList());

		Collections.sort(films, (o1, o2) -> o1.getStartyear().compareTo(o2.getStartyear()));
		return films;
	}

	private Filmography buildNewFilmography(Persona person, ImdbTitlePrincipals itp, Optional<ImdbTitleBasics> itb) {
		
		Optional<ImdbTitleAkas> ita = imdbTitleAkasService.findByTitleId(itp.getId().getTconst());
		boolean isFilmRegister = peliculasOtrosService.isFilmRegistered(itp.getId().getTconst());
		
		Filmography film = Filmography.builder().id(person.getId()).nconst(person.getNconst())
				.category(itp.getId().getCategory()).tconst(itp.getId().getTconst())
				.startyear(Integer.parseInt(itb.get().getStartyear())).title(getTitle(ita, itb)).build();

		if (isFilmRegister) {
			film.setPeliculaId(peliculasOtrosService.getIdFilm(itp.getId().getTconst()));
			Optional<Pelicula> pel = peliculaService.findById(film.getPeliculaId());
			film.setComprado(pel.isPresent() && pel.get().isComprado());
			film.setBuscado(!film.isComprado());
		}


		return film;
	}

	private String getTitle(Optional<ImdbTitleAkas> ita, Optional<ImdbTitleBasics> itb) {
		if (ita.isPresent()) {
			return ita.get().getTitle();
		}
		if (itb.isPresent()) {
			return itb.get().getOriginaltitle();
		}
		return "";
	}
}
