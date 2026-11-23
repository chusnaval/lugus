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
import lugus.model.series.Series;
import lugus.model.values.Format;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.model.people.Filmography;
import lugus.model.people.People;
import lugus.service.films.FilmService;

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

	private final FilmService peliculaService;

	private final SeriesService seriesService;

	@GetMapping("/{id}")
	public List<Filmography> findFilmography(@PathVariable final Integer id) {
		People person = personaService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("People not found"));

		return buildFilmsList(person);
	}

	private List<Filmography> buildFilmsList(final People person) {

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
		}).filter(f -> f != null).toList();

		Collections.sort(films, (o1, o2) -> o1.getStartyear().compareTo(o2.getStartyear()));
		return films;
	}

	private Filmography buildNewFilmography(final People person, final ImdbTitlePrincipals itp, 
		final Optional<ImdbTitleBasics> itb) {

		Optional<ImdbTitleAkas> ita = imdbTitleAkasService.findByTitleId(itp.getId().getTconst());

		if (itb.isEmpty()) {
			throw new IllegalArgumentException("Cant build filmography: empty itb");
		}
		if (itb.isEmpty() || itb.get().getStartyear() == null) {
			throw new IllegalArgumentException("Cant build filmography: empty itb or null startyear");
		}
		boolean isMovie = itb.get().isAMovie();
		if (isMovie) {
			return buildFilmographyForFilm(person, itp, itb, ita);
		} else if (itb.get().isASerie()) {
			return buildFilmographyForSeries(person, itp, itb, ita);
		}

		return null;

	}

	private Filmography buildFilmographyForSeries(final People person,
			final ImdbTitlePrincipals itp, final Optional<ImdbTitleBasics> itb,
			final Optional<ImdbTitleAkas> ita) {
		boolean isSerieRegister = seriesService.isFilmRegistered(itp.getId().getTconst());

		Filmography film = Filmography.builder().id(person.getId()).nconst(person.getNconst())
				.category(itp.getId().getCategory()).tconst(itp.getId().getTconst()).type(itb.get().getTitletype())
				.startyear(Integer.parseInt(itb.get().getStartyear())).title(getTitle(ita, itb)).build();

		if (isSerieRegister) {
			Optional<Series> serie = seriesService.findByImdbId(itp.getId().getTconst());
			if (!serie.isPresent()) {
				film.setPeliculaId(serie.get().getId());

				if (serie.get().isPurchased()) {
					film.setFormatoId(obtainBestFormatId(serie.get()));
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

	private Short obtainBestFormatId(final Series series) {
		Short bestFormatoId = null;
		// best format would be the one with the highest priority, so we need to define
		// a priority order for the formats
		// in the future, we could have a configuration for this, but for now, we will
		// hardcode it
		List<Format> orderedFormats = Format.prioritizedFormats();

		for (Format formato : orderedFormats) {
			if (series.getFormat().getId() == formato.getId()) {
				bestFormatoId = formato.getId();
				break;
			}
		}
		return bestFormatoId;
	}

	private Filmography buildFilmographyForFilm(final People person,
			final ImdbTitlePrincipals itp, final Optional<ImdbTitleBasics> itb,
			final Optional<ImdbTitleAkas> ita) {
		boolean isFilmRegister = peliculaService.isFilmRegistered(itp.getId().getTconst());

		Filmography filmography = Filmography.builder().id(person.getId()).nconst(person.getNconst())
				.category(itp.getId().getCategory()).tconst(itp.getId().getTconst()).type(itb.get().getTitletype())
				.startyear(Integer.parseInt(itb.get().getStartyear())).title(getTitle(ita, itb)).build();
		if (isFilmRegister) {
			Optional<Film> filmOptional = peliculaService.findByImdbId(itp.getId().getTconst());
			if (filmOptional.isPresent()) {
				// We need to put movieId in filmography so we know which movie
				// we have registered in one way or another
				// so if there are several movies with that tconst, we
				// use the ID of the edition with the latest modification date, so it's assumed
				// to be the most up-to-date edition
				// but tsmodif can be null, so if it's null, it's assumed to be the
				// oldest edition, and if it's not null, we compare it with the modification
				// dates of
				// the other editions to determine which is the most up-to-date
				Film film = filmOptional.get();
				filmography.setPeliculaId(film.getId());
				// We want to mark the filmography record as purchased or searched, so
				// if any of the film records with that tconst are purchased, they are
				// marked as purchased, otherwise they are marked as searched
				boolean purchased = film.isPurchased();
				if (purchased) {
					filmography.setFormatoId(obtainBestFormatoId(film));
				}
				filmography.setComprado(purchased);
				filmography.setBuscado(!purchased);
			} else {
				filmography.setBuscado(true);
				filmography.setComprado(false);
			}

		}

		return filmography;
	}

	private Short obtainBestFormatoId(final Film film) {
		Short bestFormatoId = null;
		// best format would be the one with the highest priority, so we need to define
		// a priority order for the formats
		// in the future, we could have a configuration for this, but for now, we will
		// hardcode it
		List<Format> orderedFormats = Format.prioritizedFormats();

		for (Format formato : orderedFormats) {

			for (Edition edition : film.getEditions()) {
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
