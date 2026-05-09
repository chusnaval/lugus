package lugus.api.groups;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.GroupDTO;
import lugus.dto.groups.GroupDetailedDTO;
import lugus.dto.groups.GroupFilmsDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.groups.GroupFilmsMapper;
import lugus.mapper.groups.GroupMapper;
import lugus.model.groups.Group;
import lugus.model.groups.GroupFilms;
import lugus.model.imdb.ImdbDirectorFilm;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.films.Pelicula;
import lugus.service.films.PeliculaService;
import lugus.service.groups.GroupFilmsService;
import lugus.service.groups.GroupsService;
import lugus.service.imdb.ImdbBasicsService;
import lugus.service.imdb.ImdbDirectorFilmService;
import lugus.service.imdb.ImdbTitleAkasService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.model.imdb.ImdbBasics;
@RestController
@RequestMapping("/v1/api/groups")
@RequiredArgsConstructor
public class GroupApiController {

	private final GroupsService service;
	
	private final GroupFilmsService fService;
	
	private final GroupMapper mapper;
	
	private final GroupFilmsMapper fmapper;

	private final PeliculaService peliculaService;

	private final ImdbBasicsService imdbBasicsService;
	
	@GetMapping
	List<GroupDTO> all() {
		List<Group> sources = service.findAll();
		return sources.stream().map(mapper::mapToDTO).collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	GroupDetailedDTO one(@PathVariable Integer id) throws LugusNotFoundException {
		Group group = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		GroupDetailedDTO gdto = new GroupDetailedDTO();
		gdto.setId(group.getId());
		gdto.setName(group.getName());
		
		List<GroupFilms> gfs = fService.findByGroup(id);
		List<GroupFilmsDTO> films = new ArrayList<>();
		for(GroupFilms gf : gfs) {
			films.add(fmapper.mapToDTO(gf));
		}
		
		gdto.setFilms(films);
		return gdto;
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
				if(d.getTitleid() != null && result.stream().filter(r -> r instanceof SimpleFilm)
						.anyMatch(r -> ((SimpleFilm) r).tconst.equals(d.getTitleid()))) continue;
				// por si acas el título de IMDB no coincide exactamente con el título de Pelicula, comprobamos si el título de IMDB ya existe como Pelicula
				if(d.getTitleid() != null && peliculaService.findByImdbId(d.getTitleid()) != null) continue;
				
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