package lugus.mapper.films;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import lugus.dto.films.CastDto;
import lugus.dto.films.DirectorDTO;
import lugus.dto.films.EditionDto;
import lugus.dto.films.FilmDto;
import lugus.dto.films.GroupDto;
import lugus.model.core.Location;
import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.model.groups.GroupFilms;
import lugus.model.values.Format;
import lugus.model.values.Genre;

@Component
public class FilmMapper {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	public FilmDto mapToFilmDTO(Film film, String login) {
		FilmDto dto = mapToFilmDTO(film);
		if (login != null) {
			dto.setFavorite(film.isFavorite(login));
			dto.setMine(film.isMine(login));
			dto.setLastSeen(film.getLastSeenDate(login) != null ? film.getLastSeenDate(login).format(formatter) : null);
			String aux = film.getUserRating(login);
			if (!ObjectUtils.isEmpty(aux)) {
				dto.setLbRating(Double.valueOf(aux));
			} else
				dto.setLbRating(null);
			dto.setWatched(dto.getLastSeen() != null);
		}

		return dto;
	}

	public FilmDto mapToFilmDTO(Film film) {
		FilmDto dto = new FilmDto();
		dto.setId(film.getId());
		dto.setTitle(film.getTitle());
		dto.setTitleMgmt(film.getMgmtTitle());
		dto.setYear(film.getYear());

		for (Edition edicion : film.getEditions()) {
			dto.addEdition(EditionMapper.toDto(edicion));
		}

		createDirectors(dto, film);
		createCasting(dto, film);

		if (film.getGenre() != null) {
			dto.setGenreCode(film.getGenre().getCode());
			dto.setGenreDesc(film.getGenre().getDisplayName());
		}

		dto.setImdbId(film.getImdbId());
		dto.setRating(film.getRating());
		dto.setVotes(film.getVotes());

		dto.setCoverSrc(film.getCoverUrl());

		if (film.getGroups() != null && film.getGroups().isEmpty()) {
			dto.setGroup(getFirstGroup(film));
		}
		dto.setTrailerUrl(film.getTrailerUrl());

		if (film.getImdbId() != null) {
			dto.setImdbUrl("https://www.imdb.com/es-es/title/" + film.getImdbId());
		}
		dto.setCountry(film.getCountry());

		dto.setSynopsis(film.getSynopsis());
		dto.setDuration(film.getDuration());

		return dto;
	}

	private void createDirectors(FilmDto dto, Film film) {
		film.getDirectors()
				.forEach(dir -> dto.addDirector(new DirectorDTO(dir.getPeople(), dir.getName())));

	}

	private void createCasting(FilmDto dto, Film film) {
		film.getActors()
				.forEach(act -> dto
						.addCast(new CastDto(act.getPeople(), act.getOrder(), act.getName(), act.getCharacter())));

	}

	private GroupDto getFirstGroup(Film film) {
		GroupDto group = new GroupDto();

		for (GroupFilms gf : film.getGroups()) {
			group.setId(gf.getGroup().getId());
			group.setName(gf.getGroup().getName());
			group.setFaId(gf.getGroup().getFilmaffinityId());
		}

		return group;
	}

	public Film mapToFilm(FilmDto dto) {
		Film pelicula = new Film();
		pelicula.setTitle(dto.getTitle());
		pelicula.setMgmtTitle(dto.getTitleMgmt());
		pelicula.setYear(dto.getYear());

		for (EditionDto dtoEd : dto.getEditions()) {
			Edition edicion = new Edition();
			edicion.setLocation(new Location());
			edicion.getLocation().setCode(dtoEd.getLocation());
			edicion.setCode(dtoEd.getMgmtCode());
			edicion.setFormat(Format.getById(Short.valueOf(dtoEd.getFormat().getCodigo())));
			edicion.setSteelbook(dtoEd.isSteelbook());
			edicion.setSlipCover(dtoEd.isSlipcover());
			edicion.setPurchased(dtoEd.isOwned());
			edicion.setNotes(dtoEd.getNotes());

			pelicula.addEdition(edicion);
		}

		pelicula.setGenre(Genre.getById(dto.getGenreCode()));
		pelicula.setImdbId(dto.getImdbId());
		pelicula.setRating(dto.getRating());
		pelicula.setVotes(dto.getVotes());
		pelicula.setDuration(dto.getDuration());

		return pelicula;
	}
}
