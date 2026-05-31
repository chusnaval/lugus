package lugus.api.titles;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.GroupTitleDto;
import lugus.dto.groups.SearchTitleResultDto;
import lugus.mapper.groups.GroupTitleMapper;
import lugus.model.titles.Title;
import lugus.service.titles.GroupTitleService;
import lugus.service.titles.TitleCreationService;

@RestController
@RequestMapping("/v1/api/groupsTitles")
@RequiredArgsConstructor
public class GroupTitleApiController {

	private final GroupTitleService service;
	private final TitleCreationService titleCreationService;


	// ELIMINAR TITULO DE GRUPO
	@DeleteMapping("/{groupId}/titles/{groupTitleId}")
	public void deleteTitle(@PathVariable int groupId, @PathVariable Long groupTitleId) {
		service.removeTitleFromGroup(groupId, groupTitleId);
	}

	// MOVER TITULO
	@PutMapping("/{groupId}/titles/{groupTitleId}/move")
	public void moveTitle(@PathVariable int groupId, @PathVariable int groupTitleId, @RequestParam String dir) {
		service.moveTitle(groupId, groupTitleId, dir);
	}
	
    @PostMapping("/{groupId}/titles")
    public GroupTitleDto addTitle(
            @PathVariable int groupId,
            @RequestBody SearchTitleResultDto dto
    ) {
        Title title = titleCreationService.getOrCreateTitle(dto);
        return GroupTitleMapper.toDto(service.addTitleToGroup(groupId, title.getId()));
    }
}
