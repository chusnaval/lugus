package lugus.api.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import lugus.dto.core.FiltrosDto;
import lugus.dto.groups.GroupDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.groups.GroupMapper;
import lugus.model.groups.Group;
import lugus.model.films.Pelicula;
import lugus.service.films.PeliculaService;
import lugus.service.groups.GroupsService;
import lugus.service.imdb.ImdbBasicsService;
import lugus.model.imdb.ImdbBasics;
@RestController
@RequestMapping("/v1/api/groups")
@RequiredArgsConstructor
public class GroupApiController {

	private final GroupsService service;
	
	private final GroupMapper mapper;

	private final PeliculaService peliculaService;

	private final ImdbBasicsService imdbBasicsService;
	
	@GetMapping
	List<GroupDTO> all() {
		List<Group> sources = service.findAll();
		return sources.stream().map(mapper::mapToDTO).collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	GroupDTO one(@PathVariable Integer id) throws LugusNotFoundException {
		Group group = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		return mapper.mapToDTO(group);
	}

	@GetMapping("/page")
	public Page<GroupDTO> getSagas(
	        @RequestParam Integer page,
	        @RequestParam Integer size,
	        @RequestParam(required = false) String title
	     
	) {
		FiltrosDto filtro = new FiltrosDto();
		filtro.setPagina(Optional.of(page));
		filtro.setPageSize(size);
		if(title!=null) {
			filtro.setTitulo(title);
		}
	    return service.findAllBy(filtro).map(mapper::mapToDTO);
	}
	
	@PostMapping
	GroupDTO newGroup(@RequestBody GroupDTO dto) {
		Group Group = service.saveGroup(mapper.mapToEntity(dto));
		return mapper.mapToDTO(Group);
	}
	
	@PutMapping("/{id}")
	GroupDTO replace(@RequestBody GroupDTO newSource, @PathVariable Integer id) {
		Optional<Group> Group = service.findById(id);
		if (Group.isPresent()) {
			Group obj = Group.get();
			obj.setName(newSource.getName());
			obj.setFilmaffinityId(newSource.getFilmaffinityId());
			obj = service.saveGroup(obj);

			return mapper.mapToDTO(obj);
		} else {
			throw new LugusNotFoundException(id);
		}
	}

	@DeleteMapping("/{id}")
	void delete(@PathVariable Integer id) {
		service.delete(id);
	}

	
	/**
	 * Search movies by title: returns registered movies first, then IMDB suggestions.
	 */
	@GetMapping("/{id}/searchMovies")
	public List<Object> searchMovies(@PathVariable Integer id, @RequestParam String title) {
		List<Object> result = new ArrayList<>();
		if (title == null || title.isBlank()) return result;
		// search registered Pelicula (use repository's findByTitulo via service if available)
		try {
			// using service to search by title via repository method (will need a small wrapper)
			List<Pelicula> pelis = peliculaService.findByTitulo(title);
			if (pelis != null) {
				for (Pelicula p : pelis) {
					// return minimal object
					result.add(new SimpleFilm(p.getId(), p.getImdbId()!=null? p.getImdbId():"", p.getTitulo(), p.getAnyo()));
				}
			}
		} catch (Exception e) {
			// ignore
		}

		// IMDB suggestions
		List<ImdbBasics> im = imdbBasicsService.findByTitleAndRegion(title.toLowerCase(),"ES");
		if (im != null) {
			for (ImdbBasics d : im) {
				// debemos evitar sugerencias ya registradas como Pelicula
				// primero comprobamos si el título de IMDB ya existe en el result
				// por si acas el título de IMDB no coincide exactamente con el título de Pelicula, comprobamos si el título de IMDB ya existe como Pelicula
				String imdbTitle = d.getTitleid();
				if(imdbTitle == null || imdbTitle.isBlank() || !peliculaService.findByImdbId(imdbTitle).isEmpty()) {
					continue; // skip this suggestion
				}
				if(result.stream().filter(r -> r instanceof SimpleFilm)
						.anyMatch(r -> ((SimpleFilm) r).tconst.equals(imdbTitle))) {
					continue; // skip this suggestion
				}
					
				result.add(new SimpleImdb(d.getTitleid(), d.getTitle(), d.getStartyear()));
				
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
			this.id = id; this.tconst = tconst; this.titulo = titulo; this.anyo = anyo;
		}
	}

	public static class SimpleImdb {
		public String tconst;
		public String title;
		public String anyo;
		public String tipo = "imdb";
		public SimpleImdb(String tconst, String title, String anyo) { this.tconst = tconst; this.title = title;  this.anyo = anyo;}
	}

}