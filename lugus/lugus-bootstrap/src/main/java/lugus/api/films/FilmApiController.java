package lugus.api.films;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.films.FilmDto;
import lugus.dto.films.FilmStatsDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.films.FilmMapper;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.films.DwFotoService;
import lugus.service.films.DwFotoServiceI;
import lugus.service.films.PeliculaService;
import lugus.service.groups.GroupsService;
import lugus.service.people.InsertPersonalDataService;

@RestController
@ResponseBody
@RequestMapping("/v1/api/films")
@RequiredArgsConstructor
public class FilmApiController {

	private final PeliculaService service;

	private final FilmMapper mapper;

	private final GroupsService groupsService;
	private final LocationService locService;
	private final SourceService sourceService;
	private final InsertPersonalDataService insertImdbService;
	
	@GetMapping("/{id}")
	FilmDto one(@PathVariable Integer id) throws LugusNotFoundException {
		Pelicula film = service.findById(id).orElse(null);
		if (film != null)
			return mapper.mapToFilmDTO(film);
		return null;
	}
	
	@PostMapping("new")
	ResponseEntity<Object> save(@RequestBody FilmDto dto, Authentication auth) throws LugusNotFoundException, IOException, URISyntaxException {
		Pelicula film = mapper.mapToFilm(dto);
		Location loc = findLocation(dto);
		film.setLocation(loc);
		film.calcularCodigo();
		film.setTsAlta(Instant.now());
		film.setUsrAlta(auth.getName());
		Pelicula saved = service.save(film);
		if (dto.getCoverSrc() != null && !dto.getCoverSrc().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			final int sourceId = calcularSource(dto.getCoverSrc());
			Optional<Source> sourceObj = sourceService.findById(sourceId);
			PeliculaFoto pf = new PeliculaFoto();
			pf.setUrl(dto.getCoverSrc());
			if (sourceObj.isPresent()) {
				pf.setSource(sourceObj.get());
			}
			pf.setFoto(dwFotoService.descargar(sourceId, dto.getCoverSrc()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			saved = service.save(saved);
		}

		if (dto.getImdbId() != null && !dto.getImdbId().isBlank()) {
			insertImdbService.insert(saved.getId(), dto.getImdbId());
		}

		
		return ResponseEntity.ok().build();
	}
	
	private int calcularSource(String coverSrc) {
		int source = 0;
		if(coverSrc!=null && !coverSrc.isBlank()) {
			
		}
		return source;
	}

	private Location findLocation(FilmDto dto) {
		Location loc = null;
		if (dto.getLocation() != null && !dto.getLocation().isBlank()) {
			loc = locService.findById(dto.getLocation())
					.orElseThrow(() -> new LugusNotFoundException(dto.getLocation()));
		}
		return loc;
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

	@GetMapping(value = "/ultimas", produces = "application/json;charset=UTF-8")
	public List<FilmDto> ultimas() throws LugusNotFoundException {

		return service.findForHome().getContent().stream().map(mapper::mapToFilmDTO).toList();
	}

	@GetMapping(value = "/stats", produces = "application/json;charset=UTF-8")
	public FilmStatsDto getStats() {
		FilmStatsDto stats = new FilmStatsDto();
		stats.setTotalFilms(service.contarTodas());
		stats.setRecentFilms(service.addedInLastDays(30));
		stats.setIncompleteGroups(groupsService.incompletedGroups());
		stats.setCompleteGroups((int) (groupsService.count() - groupsService.incompletedGroups()));
		return stats;
	}

}
