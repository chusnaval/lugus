package lugus.infrastructure.repository.groups;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.groups.Group;
import lugus.repository.groups.GroupRepository;

public interface JpaGroupRepository extends GroupRepository, JpaRepository<Group, Integer>, JpaSpecificationExecutor<Group> {

}