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
import lugus.dto.core.FiltrosDto;
import lugus.model.groups.Group;
import lugus.repository.groups.GroupRepository;



@Service
@RequiredArgsConstructor
public class GroupsService {

	private final GroupRepository groupRepository;
	
	public Optional<Group> findById(Integer id) {
		return groupRepository.findById(id);
	}
	
	public List<Group> findAll(){
		return groupRepository.findAll();
	}

	public Group saveGroup(Group group) {
		return groupRepository.save(group);
	}


	public Page<Group> findAllBy(FiltrosDto filter) {
		Optional<String> direccion = filter.getDireccion();
		if(direccion.isEmpty()) {
			direccion = Optional.of("asc");
		}
		Sort sort = Sort.by(Direction.fromString(direccion.get()), filter.getOrden().orElse("name"));

		Pageable pageable = PageRequest.of(filter.getPagina().get(), filter.getPageSize(), sort);

		
		Specification<Group> spec = Specification.where(null);
		spec = spec.and(porTitulo(filter.getTitulo()));
		
		return groupRepository.findAll(spec, pageable);
	}

	public void delete(Integer id) {
		groupRepository.deleteById(id);
	}

	public int incompletedGroups() {
		return groupRepository.countIncompleteGroups();
	}

	public long count() {
		return groupRepository.count();
	}

	public Page<Group> findAll(
			Pageable pageable) {
		Sort sort = Sort.by(Direction.ASC, "name");
		
		Pageable pageable2 = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Specification<Group> spec = Specification.where(null);

		return groupRepository.findAll(spec, pageable2);
	}
	
	public static Specification<Group> porTitulo(String titulo) {
		return (root, query, cb) -> (titulo == null || titulo.isBlank()) ? null
				: cb.like(cb.lower(root.get("name")), "%" + titulo.trim().toLowerCase() + "%");
	}

}