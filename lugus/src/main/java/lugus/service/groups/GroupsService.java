package lugus.service.groups;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.dto.FiltrosDto;
import lugus.model.groups.Group;
import lugus.repository.groups.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupsService {

	private static final int NUM_ELEMENTS_PER_PAGE = 50;
	
	private final GroupRepository groupRepository;
	
	public Optional<Group> findById(Integer id) {
		return groupRepository.findById(id);
	}
	
	public List<Group> findAll(){
		return groupRepository.findAll();
	}

	public Page<Group> findAllBy(FiltrosDto filter) {
		
		Sort sort = Sort.by(Direction.ASC, "name");

		Pageable pageable = PageRequest.of(filter.getPagina().get(), NUM_ELEMENTS_PER_PAGE, sort);

		Specification<Group> spec = Specification.where(null);

		return groupRepository.findAll(spec, pageable);
	}
}
