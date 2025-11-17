package lugus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.Director;
import lugus.repository.DirectorRepository;

@Service
@RequiredArgsConstructor
public class DirectorService {

	@SuppressWarnings("unused")
	private final  DirectorRepository directorRepository;

	public List<Director> findByPeliculaId(int id) {
		return directorRepository.findByPeliculaId(id);
	}
}
