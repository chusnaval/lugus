package lugus.api.films;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.films.PeliculaCreateDto;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.films.FilmMapper;
import lugus.model.films.Pelicula;
import lugus.service.films.PeliculaService;

@RestController
@RequestMapping("/v1/api/films")
@RequiredArgsConstructor
public class FilmApiController {

	private final PeliculaService service;

	private final FilmMapper mapper;

	@GetMapping("/{id}")
	PeliculaCreateDto one(@PathVariable Integer id) throws LugusNotFoundException {
		Pelicula film = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		return mapper.mapToDTO(film);
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
}
