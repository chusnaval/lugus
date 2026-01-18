package lugus.service.groups;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.groups.GroupFilms;
import lugus.repository.groups.GroupFilmsRepository;

@Service
@RequiredArgsConstructor
public class 
GroupFilmsService {
	
	private final GroupFilmsRepository groupFilmsRepository;
	
	public List<GroupFilms> findByGroup(Integer id) {
		return groupFilmsRepository.findAllByGroupIdOrderByOrden(id);
	}
	
	public List<GroupFilms> findByPelicula(Integer id) {
		return groupFilmsRepository.findAllByPeliculaIdOrderByGroupName(id);
	}

}
