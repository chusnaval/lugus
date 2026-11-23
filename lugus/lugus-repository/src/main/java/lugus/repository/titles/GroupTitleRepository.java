package lugus.repository.titles;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lugus.model.groups.GroupTitle;
@Repository
public interface GroupTitleRepository {

	List<GroupTitle> findByGroupIdOrderByOrdenAsc(final int groupId);

	GroupTitle save(GroupTitle gt);

	Optional<GroupTitle> findByIdAndGroupId(final Long groupTitleId, final int groupId);

	void delete(final GroupTitle gt);


	List<GroupTitle> findByTitle_Id(final Long titleId);
}
