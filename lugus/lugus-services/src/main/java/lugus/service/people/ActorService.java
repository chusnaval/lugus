package lugus.service.people;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.people.Actor;
import lugus.repository.people.ActorRepository;

@Service
@RequiredArgsConstructor
public class ActorService {

	private final  ActorRepository actorRepository;

	public List<Actor> findByPeliculaIdOrderByOrdenAsc(int id) {
		return actorRepository.findByPeliculaIdOrderByOrdenAsc(id);
	}
}
