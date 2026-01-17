package lugus.service.people;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.people.Director;
import lugus.repository.people.DirectorRepository;

@Service
@RequiredArgsConstructor
public class DirectorService {

	private final  DirectorRepository directorRepository;

	public List<Director> findByPeliculaId(int id) {
		return directorRepository.findByPeliculaId(id);
	}
}
