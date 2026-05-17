package lugus.mapper.films;

import org.springframework.stereotype.Component;

import lugus.dto.films.FilmDto;
import lugus.dto.films.GroupDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.model.films.Pelicula;
import lugus.model.groups.GroupFilms;

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

	public FilmDto mapToFilmDTO(Pelicula film) {
		FilmDto dto = new FilmDto();
		dto.setId(film.getId());
		dto.setTitle(film.getTitulo());
		dto.setTitleMgmt(film.getTituloGest());
		dto.setYear(film.getAnyo());
		dto.setMgmtCode(film.getCodigo());

		if (film.getFormato() != null) {
			dto.setFormat(film.getFormato().name());
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

		dto.setWatched(false); // TODO
		dto.setSynopsis("");// TODO
		dto.setImdbUrl(null);// TODO
		dto.setFaUrl(null);// TODO
		dto.setLastSeen(null);// TODO
		dto.setCountry(null);// TODO

		return dto;
	}

	private GroupDto getFirstGroup(Pelicula film) {
		GroupDto group = new GroupDto();
		
		for(GroupFilms gf : film.getGroups()) {
			group.setId(gf.getGroup().getId());
			group.setName(gf.getGroup().getName());
			group.setFaId(gf.getGroup().getFilmaffinityId());
		}
		
		return group;
	}
}
