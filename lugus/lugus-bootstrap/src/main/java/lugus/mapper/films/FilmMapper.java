package lugus.mapper.films;

import org.springframework.stereotype.Component;

import lugus.dto.core.FormatDTO;
import lugus.dto.films.CastDto;
import lugus.dto.films.ConditionDto;
import lugus.dto.films.DirectorDTO;
import lugus.dto.films.FilmDto;
import lugus.dto.films.GroupDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.model.films.Pelicula;
import lugus.model.groups.GroupFilms;
import lugus.model.values.Formato;
import lugus.model.values.Genero;

@Component
public class FilmMapper {

	public PeliculaCreateDto mapToDTO(Pelicula film) {
		PeliculaCreateDto dto = new PeliculaCreateDto();
		dto.setId(film.getId());
		dto.setTitulo(film.getTitulo());
		dto.setTituloGest(film.getTituloGest());
		dto.setAnyo(film.getAnyo());

		if (film.getFormato() != null) {
			dto.setFormatoCodigo(film.getFormato().getId());
		}

		if (film.getGenero() != null) {
			dto.setGeneroCodigo(film.getGenero().getCodigo());
		}

		if (film.getLocation() != null) {
			dto.setLocationCode(film.getLocation().getCodigo());
		}

		dto.setPack(film.isPack());
		dto.setSteelbook(film.isSteelbook());
		dto.setFunda(film.isFunda());
		dto.setComprado(film.isComprado());
		dto.setNotas(film.getNotas());

		return dto;
	}

	public FilmDto mapToFilmDTO(Pelicula film, String login) {
		FilmDto dto = mapToFilmDTO(film);
		if (login != null) {
			dto.setFavorite(film.isFavorite(login));
			dto.setMine(film.isMine(login));
		}

		return dto;
	}
	
	public FilmDto mapToFilmDTO(Pelicula film) {
		FilmDto dto = new FilmDto();
		dto.setId(film.getId());
		dto.setTitle(film.getTitulo());
		dto.setTitleMgmt(film.getTituloGest());
		dto.setYear(film.getAnyo());
		dto.setMgmtCode(film.getCodigo());

		createDirectors(dto, film);
		createCasting(dto, film);

		if (film.getEstado() != null) {
			dto.setCondition(new ConditionDto(film.getEstado().getId(), film.getEstado().getName()));
		}

		if (film.getFormato() != null) {
			dto.setFormat(new FormatDTO("" + film.getFormato().getId(), film.getFormato().name()));
		}

		if (film.getGenero() != null) {
			dto.setGenreCode(film.getGenero().getCodigo());
			dto.setGenreDesc(film.getGenero().getDisplayName());
		}

		if (film.getLocation() != null) {
			dto.setLocation(film.getLocation().getDescripcion());
		}

		dto.setPack(film.isPack());
		dto.setSteelbook(film.isSteelbook());
		dto.setSlipcover(film.isFunda());
		dto.setOwned(film.isComprado());
		dto.setNotes(film.getNotas());

		dto.setImdbId(film.getImdbId());
		dto.setRating(film.getRating());
		dto.setVotes(film.getVotes());

		dto.setSituation(film.getSituacion());
		dto.setCoverSrc(film.getCoverUrl());

		if (film.getGroups() != null && film.getGroups().isEmpty()) {
			dto.setGroup(getFirstGroup(film));
		}
		dto.setFaId(film.getFaId());
		dto.setTrailerUrl(film.getTrailerUrl());
		dto.setWatched(film.getLastSeen() != null);
		if (film.getImdbId() != null) {
			dto.setImdbUrl("https://www.imdb.com/es-es/title/" + film.getImdbId());
		}
		dto.setCountry(film.getCountry());
	
		dto.setSynopsis(film.getSynopsis());
		dto.setLastSeen(null);// TODO

		return dto;
	}

	private void createDirectors(FilmDto dto, Pelicula film) {
		film.getDirectores().stream().forEach(dir -> dto.addDirector(new DirectorDTO(dir.getPersona(), dir.getNombre())));

	}

	private void createCasting(FilmDto dto, Pelicula film) {
		film.getActores().stream()
				.forEach(act -> dto.addCast(new CastDto(act.getPersona(), act.getNombre(), act.getPersonaje())));

	}

	private GroupDto getFirstGroup(Pelicula film) {
		GroupDto group = new GroupDto();

		for (GroupFilms gf : film.getGroups()) {
			group.setId(gf.getGroup().getId());
			group.setName(gf.getGroup().getName());
			group.setFaId(gf.getGroup().getFilmaffinityId());
		}

		return group;
	}

	public Pelicula mapToFilm(FilmDto dto) {
		Pelicula pelicula = new Pelicula();
		pelicula.setTitulo(dto.getTitle());
		pelicula.setTituloGest(dto.getTitleMgmt());
		pelicula.setAnyo(dto.getYear());
		pelicula.setCodigo(dto.getMgmtCode());

		pelicula.setFormato(Formato.getById(Short.valueOf(dto.getFormat().getCodigo())));

		pelicula.setGenero(Genero.getById(dto.getGenreCode()));

		pelicula.setPack(dto.isPack());
		pelicula.setSteelbook(dto.isSteelbook());
		pelicula.setFunda(dto.isSlipcover());
		pelicula.setComprado(dto.isOwned());
		pelicula.setNotas(dto.getNotes());

		pelicula.setImdbId(dto.getImdbId());
		pelicula.setRating(dto.getRating());
		pelicula.setVotes(dto.getVotes());

		return pelicula;
	}
}
