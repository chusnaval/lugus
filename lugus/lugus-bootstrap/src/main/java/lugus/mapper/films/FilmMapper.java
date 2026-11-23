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
		Film film = new Film();
		film.setTitle(dto.getTitle());
		film.setMgmtTitle(dto.getTitleMgmt());
		film.setYear(dto.getYear());

		for (EditionDto dtoEd : dto.getEditions()) {
			Edition edition = new Edition();
			edition.setLocation(new Location());
			edition.getLocation().setCode(dtoEd.getLocation());
			edition.setCode(dtoEd.getMgmtCode());
			edition.setFormat(Format.getById(Short.valueOf(dtoEd.getFormatCode())));
			edition.setSteelbook(dtoEd.isSteelbook());
			edition.setSlipCover(dtoEd.isSlipcover());
			edition.setPurchased(dtoEd.isOwned());
			edition.setNotes(dtoEd.getNotes());

			film.addEdition(edition);
		}

		film.setGenre(Genre.getById(dto.getGenreCode()));
		film.setImdbId(dto.getImdbId());
		film.setRating(dto.getRating());
		film.setVotes(dto.getVotes());
		film.setDuration(dto.getDuration());

		return film;
	}
}
