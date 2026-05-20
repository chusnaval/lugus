package lugus.infrastructure.repository.titles;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.groups.GroupTitle;
import lugus.repository.titles.GroupTitleRepository;

public interface JpaGroupTitleRepository  extends GroupTitleRepository, JpaRepository<GroupTitle, Integer> {

}
