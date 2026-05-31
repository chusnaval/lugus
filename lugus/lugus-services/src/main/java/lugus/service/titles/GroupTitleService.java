package lugus.service.titles;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.groups.Group;
import lugus.model.groups.GroupTitle;
import lugus.model.titles.Title;
import lugus.repository.groups.GroupRepository;
import lugus.repository.titles.GroupTitleRepository;

@Service
@RequiredArgsConstructor
public class GroupTitleService {

	private final GroupRepository groupRepo;

	private final GroupTitleRepository groupTitleRepo;

	private final TitlesService titlesService;

	public GroupTitle addTitleToGroup(int groupId, Long titleId) {
		Group group = groupRepo.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));

		Title title = titlesService.findById(titleId).orElseThrow(() -> new RuntimeException("Title not found"));

		int nextOrder = groupTitleRepo.findByGroupIdOrderByOrdenAsc(groupId).size() + 1;

		GroupTitle gt = new GroupTitle();
		gt.setGroup(group);
		gt.setTitle(title);
		gt.setOrden(nextOrder);

		return groupTitleRepo.save(gt);
	}

	public void removeTitleFromGroup(int groupId, Long groupTitleId) {
		GroupTitle gt = groupTitleRepo.findByIdAndGroupId(groupTitleId, groupId)
				.orElseThrow(() -> new RuntimeException("GroupTitle not found"));

		groupTitleRepo.delete(gt);

		// Reordenar después de borrar
		List<GroupTitle> list = groupTitleRepo.findByGroupIdOrderByOrdenAsc(groupId);
		int order = 1;
		for (GroupTitle g : list) {
			g.setOrden(order++);
			groupTitleRepo.save(g);
		}
	}

	public void moveTitle(int groupId, int groupTitleId, String direction) {
		List<GroupTitle> list = groupTitleRepo.findByGroupIdOrderByOrdenAsc(groupId);

		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == groupTitleId) {
				index = i;
				break;
			}
		}

		if (index == -1)
			return;

		if (direction.equals("up") && index > 0) {
			Collections.swap(list, index, index - 1);
		}

		if (direction.equals("down") && index < list.size() - 1) {
			Collections.swap(list, index, index + 1);
		}

		// Guardar nuevo orden
		int order = 1;
		for (GroupTitle g : list) {
			g.setOrden(order++);
			groupTitleRepo.save(g);
		}

		
	}
}
