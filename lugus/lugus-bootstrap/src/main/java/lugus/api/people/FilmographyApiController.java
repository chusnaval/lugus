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
import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.model.series.Serie;
import lugus.model.values.Format;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.model.people.Filmography;
import lugus.model.people.People;
import lugus.service.films.PeliculaService;

import lugus.service.imdb.ImdbTitleAkasService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.service.imdb.ImdbTitlePrincipalsService;
import lugus.service.people.PersonaService;
import lugus.service.series.SeriesService;

@RestController
@RequestMapping("/v1/api/filmography")
@RequiredArgsConstructor
public class FilmographyApiController {

	private final PersonaService personaService;

	private final ImdbTitlePrincipalsService imdbTitlePrincipalsService;

	private final ImdbTitleBasicsService imdbTitleBasicsService;

	private final ImdbTitleAkasService imdbTitleAkasService;

	private final PeliculaService peliculaService;

	private final SeriesService seriesService;

	@GetMapping("/{id}")
	public List<Filmography> findFilmography(@PathVariable Integer id) {
		People person = personaService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Persona no encontrada"));

		return buildFilmsList(person);
	}

	private List<Filmography> buildFilmsList(People person) {

		List<ImdbTitlePrincipals> aux = imdbTitlePrincipalsService.findAllByIdNconst(person.getNconst());

		List<Filmography> films = aux.stream().filter(itp -> {
			Optional<ImdbTitleBasics> itb = imdbTitleBasicsService.findById(itp.getId().getTconst());
			return itb.isPresent() && itb.get().getStartyear() != null && !itb.get().isASerieEpisode();

		}).collect(Collectors.groupingBy(itp -> itp.getId().getTconst())).values().stream().map(list -> {
			ImdbTitlePrincipals first = list.get(0);
			ImdbTitleBasics itb = imdbTitleBasicsService.findById(first.getId().getTconst()).get();
			Filmography film = buildNewFilmography(person, first, Optional.of(itb));
			if (film == null) {
				return null;
			}
			list.stream().skip(1).forEach(itp -> film.appendCategory(itp.getId().getCategory()));

			return film;
		}).filter(f -> f != null).collect(Collectors.toList());

		Collections.sort(films, (o1, o2) -> o1.getStartyear().compareTo(o2.getStartyear()));
		return films;
	}

	private Filmography buildNewFilmography(People person, ImdbTitlePrincipals itp, Optional<ImdbTitleBasics> itb) {

		Optional<ImdbTitleAkas> ita = imdbTitleAkasService.findByTitleId(itp.getId().getTconst());

		if (itb.isEmpty()) {
			throw new IllegalArgumentException("No se puede construir Filmography: itb vacío");
		}
		if (itb.isEmpty() || itb.get().getStartyear() == null) {
			throw new IllegalArgumentException("No se puede construir Filmography: itb vacío o startyear nulo");
		}
		boolean isMovie = itb.get().isAMovie();
		if (isMovie) {
			return buildFilmographyForMovie(person, itp, itb, ita);
		} else if (itb.get().isASerie()) {
			return buildFilmographyForSerie(person, itp, itb, ita);
		}

		return null;

	}

	private Filmography buildFilmographyForSerie(People person, ImdbTitlePrincipals itp, Optional<ImdbTitleBasics> itb,
			Optional<ImdbTitleAkas> ita) {
		boolean isSerieRegister = seriesService.isFilmRegistered(itp.getId().getTconst());

		Filmography film = Filmography.builder().id(person.getId()).nconst(person.getNconst())
				.category(itp.getId().getCategory()).tconst(itp.getId().getTconst()).type(itb.get().getTitletype())
				.startyear(Integer.parseInt(itb.get().getStartyear())).title(getTitle(ita, itb)).build();

		if (isSerieRegister) {
			Optional<Serie> serie = seriesService.findByImdbId(itp.getId().getTconst());
			if (!serie.isPresent()) {
				film.setPeliculaId(serie.get().getId());

				if (serie.get().isPurchased()) {
					film.setFormatoId(obtainBestFormatoId(serie.get()));
				}
				film.setComprado(serie.get().isPurchased());
				film.setBuscado(!serie.get().isPurchased());
			} else {
				film.setBuscado(true);
				film.setComprado(false);
			}
		}

		return film;
	}

	private Short obtainBestFormatoId(Serie serie) {
		Short bestFormatoId = null;
		// best format would be the one with the highest priority, so we need to define
		// a priority order for the formats
		// in the future, we could have a configuration for this, but for now, we will
		// hardcode it
		List<Format> orderedFormats = Format.prioritizedFormats();

		for (Format formato : orderedFormats) {
			if (serie.getFormato().getId() == formato.getId()) {
				bestFormatoId = formato.getId();
				break;
			}
		}
		return bestFormatoId;
	}

	private Filmography buildFilmographyForMovie(People person, ImdbTitlePrincipals itp, Optional<ImdbTitleBasics> itb,
			Optional<ImdbTitleAkas> ita) {
		boolean isFilmRegister = peliculaService.isFilmRegistered(itp.getId().getTconst());

		Filmography film = Filmography.builder().id(person.getId()).nconst(person.getNconst())
				.category(itp.getId().getCategory()).tconst(itp.getId().getTconst()).type(itb.get().getTitletype())
				.startyear(Integer.parseInt(itb.get().getStartyear())).title(getTitle(ita, itb)).build();
		if (isFilmRegister) {
			Optional<Film> filmOptional = peliculaService.findByImdbId(itp.getId().getTconst());
			if (filmOptional.isPresent()) {
				// necesitamos poner peliculaId en filmography para que se sepa que pelicula de
				// una forma u otra
				// la tenemos registrada, asi que si hay varias peliculas con ese tconst, se
				// pone el id de la edicion con mayor fecha de modificacion, asi que se asume
				// que es la edicion mas actualizada
				// pero tsmodif puede ser nulo, asi que se asume que si es nulo, es la edicion
				// mas antigua, y si no es nulo, se compara con las fechas de modificacion de
				// las otras ediciones para determinar cual es la mas actualizada
				Film pelicula = filmOptional.get();
				film.setPeliculaId(pelicula.getId());
				// queremos marcar como comprado o buscado el registro de filmografia, asi que
				// si cualquiera de los registros de pelicula con ese tconst esta comprado, se
				// marca como comprado, sino se marca como buscado
				boolean comprado = pelicula.isComprado();
				if (comprado) {
					film.setFormatoId(obtainBestFormatoId(pelicula));
				}
				film.setComprado(comprado);
				film.setBuscado(!comprado);
			} else {
				film.setBuscado(true);
				film.setComprado(false);
			}

		}

		return film;
	}

	private Short obtainBestFormatoId(Film pelicula) {
		Short bestFormatoId = null;
		// best format would be the one with the highest priority, so we need to define
		// a priority order for the formats
		// in the future, we could have a configuration for this, but for now, we will
		// hardcode it
		List<Format> orderedFormats = Format.prioritizedFormats();

		for (Format formato : orderedFormats) {

			for (Edition edition : pelicula.getEditions()) {
				if (edition.getFormat().getId() == formato.getId()) {
					bestFormatoId = formato.getId();
					break;
				}
			}

			if (bestFormatoId != null) {
				break;
			}
		}
		return bestFormatoId;
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
