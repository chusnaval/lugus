package lugus.service.groups;

import java.util.List;
import java.util.Optional;

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

	public GroupFilms save(GroupFilms gf) {
		return groupFilmsRepository.save(gf);
	}

	public int nextOrderForGroup(Integer groupId) {
		List<GroupFilms> list = groupFilmsRepository.findAllByGroupIdOrderByOrden(groupId);
		if (list == null || list.isEmpty()) return 1;
		int max = list.stream().mapToInt(GroupFilms::getOrden).max().orElse(0);
		return max + 1;
	}

	public Optional<GroupFilms> findById(Integer id) {
		return groupFilmsRepository.findById(id);
	}

	public void deleteById(Integer id) {
		groupFilmsRepository.deleteById(id);
	}

}