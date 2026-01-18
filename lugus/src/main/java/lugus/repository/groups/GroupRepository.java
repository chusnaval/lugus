package lugus.repository.groups;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.groups.Group;

public interface GroupRepository extends JpaRepository<Group, Integer> {

	Page<Group> findAll(Specification<Group> spec, Pageable pageable);

}
