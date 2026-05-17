package lugus.api.films;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.films.FilmDto;
import lugus.dto.films.FilmStatsDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.films.FilmMapper;
import lugus.model.films.Pelicula;
import lugus.service.films.PeliculaService;
import lugus.service.groups.GroupsService;

@RestController
@ResponseBody
@RequestMapping("/v1/api/films")
@RequiredArgsConstructor
public class FilmApiController {

	private final PeliculaService service;

	private final FilmMapper mapper;
	
	private final GroupsService	groupsService;

	@GetMapping("/{id}")
	FilmDto one(@PathVariable Integer id) throws LugusNotFoundException {
		Pelicula film = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		return mapper.mapToFilmDTO(film);
	}

	@GetMapping("/wanted")
	List<PeliculaCreateDto> wanted() throws LugusNotFoundException {
		Page<Pelicula> page = service.wanted();
		List<PeliculaCreateDto> result = new ArrayList<PeliculaCreateDto>();
		for (Pelicula p : page.getContent()) {
			result.add(mapper.mapToDTO(p));
		}
		return result;
	}

	@GetMapping("/random/wanted")
	PeliculaCreateDto randomw() throws LugusNotFoundException {
		Page<Pelicula> page = service.wanted();
		int number = (int) (Math.random() * page.getNumberOfElements()) + 1;
		return mapper.mapToDTO(page.getContent().get(number));
	}

	@GetMapping(value="/ultimas", produces = "application/json;charset=UTF-8")
	public List<FilmDto> ultimas() throws LugusNotFoundException {

		return service.findForHome().getContent().stream()
                .map(mapper::mapToFilmDTO)
                .toList();
	}
	
	@GetMapping(value="/stats", produces = "application/json;charset=UTF-8")
	public FilmStatsDto getStats() {
		FilmStatsDto stats = new FilmStatsDto();
		stats.setTotalFilms(service.contarTodas());
		stats.setRecentFilms(service.addedInLastDays(30));
		stats.setIncompleteGroups(groupsService.incompletedGroups());
		stats.setCompleteGroups((int) (groupsService.count() - groupsService.incompletedGroups()));
		return stats;
	}
	

}
