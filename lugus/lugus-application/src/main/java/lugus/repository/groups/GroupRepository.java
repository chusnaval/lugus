package lugus.repository.groups;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import lugus.model.groups.Group;

public interface GroupRepository {

	List<Group> findAll();

	Optional<Group> findById(Integer id);

	Group save(Group group);

	void deleteById(Integer id);

	long count();

	Page<Group> findAll(Specification<Group> spec, Pageable pageable);

}
