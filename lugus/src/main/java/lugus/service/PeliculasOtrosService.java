package lugus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.PeliculasOtros;
import lugus.repository.PeliculasOtrosRepository;

@Service
@RequiredArgsConstructor
public class PeliculasOtrosService {

	private final PeliculasOtrosRepository peliculasOtrosRepository;

	public List<PeliculasOtros> findByImdbId(final String tconst) {
		return peliculasOtrosRepository.findByImdbId(tconst);
	}
}
