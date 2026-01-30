package lugus.api.films;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lugus.dto.groups.GroupDTO;
import lugus.dto.groups.GroupDetailedDTO;
import lugus.dto.groups.GroupFilmsDTO;
import lugus.exception.LugusNotFoundException;
import lugus.mapper.groups.GroupFilmsMapper;
import lugus.mapper.groups.GroupMapper;
import lugus.model.groups.Group;
import lugus.model.groups.GroupFilms;
import lugus.service.groups.GroupFilmsService;
import lugus.service.groups.GroupsService;

@RestController
@RequestMapping("/v1/api/groups")
@RequiredArgsConstructor
public class GroupApiController {

	private final GroupsService service;
	
	private final GroupFilmsService fService;
	
	private final GroupMapper mapper;
	
	private final GroupFilmsMapper fmapper;
	
	@GetMapping
	List<GroupDTO> all() {
		List<Group> sources = service.findAll();
		return sources.stream().map(mapper::mapToDTO).collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	GroupDetailedDTO one(@PathVariable Integer id) throws LugusNotFoundException {
		Group group = service.findById(id).orElseThrow(() -> new LugusNotFoundException(id));
		GroupDetailedDTO gdto = new GroupDetailedDTO();
		gdto.setId(group.getId());
		gdto.setName(group.getName());
		
		List<GroupFilms> gfs = fService.findByGroup(id);
		List<GroupFilmsDTO> films = new ArrayList<>();
		for(GroupFilms gf : gfs) {
			films.add(fmapper.mapToDTO(gf));
		}
		
		gdto.setFilms(films);
		return gdto;
	}
}
