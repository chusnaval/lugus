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
import lugus.model.films.Edicion;
import lugus.model.films.Pelicula;
import lugus.model.groups.GroupFilms;
import lugus.model.values.Formato;
import lugus.model.values.Genero;

@Component
public class FilmMapper {
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	public FilmDto mapToFilmDTO(Pelicula film, String login) {
		FilmDto dto = mapToFilmDTO(film);
		if (login != null) {
			dto.setFavorite(film.isFavorite(login));
			dto.setMine(film.isMine(login));
			dto.setLastSeen(film.getFechaVista(login)!=null?film.getFechaVista(login).format(formatter):null);
			String aux = film.getUsuarioRating(login);
			if(!ObjectUtils.isEmpty(aux)) {
				dto.setLbRating(Double.valueOf(aux));
			}else
				dto.setLbRating(null);
			dto.setWatched(dto.getLastSeen()!=null);
		}

		return dto;
	}

	public FilmDto mapToFilmDTO(Pelicula film) {
		FilmDto dto = new FilmDto();
		dto.setId(film.getId());
		dto.setTitle(film.getTitulo());
		dto.setTitleMgmt(film.getTituloGest());
		dto.setYear(film.getAnyo());
		
		for(Edicion edicion : film.getEditions()) {
			dto.addEdition(EdicionMapper.toDto(edicion));
		}
		
		createDirectors(dto, film);
		createCasting(dto, film);
		
		
		if (film.getGenero() != null) {
			dto.setGenreCode(film.getGenero().getCodigo());
			dto.setGenreDesc(film.getGenero().getDisplayName());
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

	private void createDirectors(FilmDto dto, Pelicula film) {
		film.getDirectores().stream()
				.forEach(dir -> dto.addDirector(new DirectorDTO(dir.getPersona(), dir.getNombre())));

	}

	private void createCasting(FilmDto dto, Pelicula film) {
		film.getActores().stream()
				.forEach(act -> dto.addCast(new CastDto(act.getPersona(), act.getOrden(), act.getNombre(), act.getPersonaje())));

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
		
		for(EditionDto dtoEd : dto.getEditions()) {
			Edicion edicion = new Edicion();
			edicion.setLocation(new Location());
			edicion.getLocation().setCodigo(dtoEd.getLocation());
			edicion.setCodigo(dtoEd.getMgmtCode());
			edicion.setFormato(Formato.getById(Short.valueOf(dtoEd.getFormat().getCodigo())));
			edicion.setSteelbook(dtoEd.isSteelbook());
			edicion.setFunda(dtoEd.isSlipcover());
			edicion.setComprado(dtoEd.isOwned());
			edicion.setNotas(dtoEd.getNotes());
			
			pelicula.addEdicion(edicion);
		}

		pelicula.setGenero(Genero.getById(dto.getGenreCode()));
		pelicula.setImdbId(dto.getImdbId());
		pelicula.setRating(dto.getRating());
		pelicula.setVotes(dto.getVotes());
		pelicula.setDuration(dto.getDuration());

		
		return pelicula;
	}
}
