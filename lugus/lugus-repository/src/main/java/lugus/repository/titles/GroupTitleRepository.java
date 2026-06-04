package lugus.repository.titles;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lugus.model.groups.GroupTitle;
@Repository
public interface GroupTitleRepository {

	List<GroupTitle> findByGroupIdOrderByOrdenAsc(int groupId);

	GroupTitle save(GroupTitle gt);

	Optional<GroupTitle> findByIdAndGroupId(Long groupTitleId, int groupId);

	void delete(GroupTitle gt);


	List<GroupTitle> findByTitle_Id(Long titleId);
}
