package lugus.mapper.groups;

import lugus.dto.groups.GroupTitleDto;
import lugus.mapper.titles.TitleMapper;
import lugus.model.groups.GroupTitle;

public class GroupTitleMapper {

    public static GroupTitleDto toDto(GroupTitle gt) {
        GroupTitleDto dto = new GroupTitleDto();
        dto.setId(gt.getId());
        dto.setOrden(gt.getOrden());
        dto.setTitle(TitleMapper.toDto(gt.getTitle()));
        return dto;
    }
}
