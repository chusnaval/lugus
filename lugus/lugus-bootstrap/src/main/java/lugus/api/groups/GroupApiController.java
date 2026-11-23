package lugus.api.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FilterDto;
import lugus.dto.groups.GroupDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.groups.GroupMapper;
import lugus.model.groups.Group;
import lugus.model.films.Film;
import lugus.service.films.FilmService;
import lugus.service.groups.GroupsService;
import lugus.service.imdb.ImdbBasicsService;
import lugus.model.imdb.ImdbBasics;

@RestController
@RequestMapping("/v1/api/groups")
@RequiredArgsConstructor
public class GroupApiController {

	private final GroupsService service;

	private final GroupMapper mapper;

	private final FilmService filmService;

	private final ImdbBasicsService imdbBasicsService;

	@GetMapping
	List<GroupDTO> all() {
		List<Group> sources = service.findAll();
		return sources.stream().map(mapper::mapToDTO).toList();
	}

	@GetMapping("/{id}")
	GroupDTO one(@PathVariable final Integer id) throws LugusNotFoundException {
		Group group = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		return mapper.mapToDTO(group);
	}

	@GetMapping("/page")
	public Page<GroupDTO> getSagas(@RequestParam final Integer page, @RequestParam final Integer size,
			@RequestParam(required = false) final Boolean owned, @RequestParam(required = false) final String title,
			@RequestParam(required = false) final String casting,
			@RequestParam(required = false) final Integer fromYear,
			@RequestParam(required = false) final Integer toYear, @RequestParam(required = false) final String format,
			@RequestParam(required = false) final String genre, @RequestParam(required = false) final Boolean pack,
			@RequestParam(required = false) final String sort,
			@RequestParam(required = false) final String sortDirection) {
		FilterDto filter = FilterDto.buildFilter(page, size, owned, title, casting, fromYear,
			 toYear, format, genre, pack, null, sort,
				sortDirection);
		return service.findAllBy(filter).map(mapper::mapToDTO);
	}

	

	@PostMapping
	GroupDTO newGroup(@RequestBody final GroupDTO dto) {
		Group group = service.saveGroup(mapper.mapToEntity(dto));
		return mapper.mapToDTO(group);
	}

	@PutMapping("/{id}")
	GroupDTO replace(@RequestBody final GroupDTO newSource, @PathVariable final Integer id) {
		Optional<Group> group = service.findById(id);
		if (group.isPresent()) {
			Group obj = group.get();
			obj.setName(newSource.getName());
			obj.setFilmaffinityId(newSource.getFilmaffinityId());
			obj = service.saveGroup(obj);

			return mapper.mapToDTO(obj);
		} else {
			throw new LugusNotFoundException(id);
		}
	}

	@DeleteMapping("/{id}")
	void delete(@PathVariable final Integer id) {
		service.delete(id);
	}

	/**
	 * Search movies by title: returns registered movies first, then IMDB
	 * suggestions.
	 */
	@GetMapping("/{id}/searchMovies")
	public List<Object> searchMovies(@PathVariable final Integer id, @RequestParam final String title) {
		List<Object> result = new ArrayList<>();
		if (title == null || title.isBlank())
			return result;
		// search registered Film (use repository's findByTitle via service if
		// available)
		try {
			// using service to search by title via repository method (will need a small
			// wrapper)
			List<Film> pelis = filmService.findByTitle(title);
			if (pelis != null) {
				for (Film p : pelis) {
					// return minimal object
					result.add(new SimpleFilm(p.getId(), p.getImdbId() != null ? p.getImdbId() : "", p.getTitle(),
							p.getYear()));
				}
			}
		} catch (Exception e) {
			// ignore
		}

		// IMDB suggestions
		List<ImdbBasics> im = imdbBasicsService.findByTitleAndRegion(title.toLowerCase(), "ES");
		if (im != null) {
			for (ImdbBasics d : im) {
				// We must avoid suggestions already registered as "Movie"
				// First, we check if the IMDb title already exists in the results
				// In case the IMDb title doesn't exactly match the Movie title, we check if the
				// IMDb title already exists as "Movie"
				String imdbTitle = d.getTitleid();
				boolean alreadyRegistered = imdbTitle != null && !imdbTitle.isBlank()
						&& !filmService.isFilmRegistered(imdbTitle);
				boolean alreadyInResults = imdbTitle != null
						&& result.stream().filter(SimpleFilm.class::isInstance)
								.anyMatch(r -> ((SimpleFilm) r).tconst.equals(imdbTitle));

				if (!alreadyRegistered && !alreadyInResults) {
					result.add(new SimpleImdb(d.getTitleid(), d.getTitle(), d.getStartyear()));
				}
			}
		}

		return result;
	}

	// simple DTO classes used only here
	public static class SimpleFilm {
		public Integer id;
		public String tconst;
		public String titulo;
		public Integer anyo;
		public String tipo = "local";

		public SimpleFilm(Integer id, String tconst, String titulo, Integer anyo) {
			this.id = id;
			this.tconst = tconst;
			this.titulo = titulo;
			this.anyo = anyo;
		}
	}

	public static class SimpleImdb {
		public String tconst;
		public String title;
		public String anyo;
		public String tipo = "imdb";

		public SimpleImdb(String tconst, String title, String anyo) {
			this.tconst = tconst;
			this.title = title;
			this.anyo = anyo;
		}
	}

}