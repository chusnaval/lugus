package lugus.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.PermisoException;
import lugus.dto.FiltrosDto;
import lugus.model.groups.Group;
import lugus.model.groups.GroupFilms;
import lugus.model.user.Usuario;
import lugus.service.groups.GroupFilmsService;
import lugus.service.groups.GroupsService;
import lugus.service.user.UsuarioService;

@Controller
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

	private final UsuarioService usuarioService;

	private final GroupsService groupsService;
	
	private final GroupFilmsService groupFilmsService;

	@GetMapping("/{id}")
	public String detail(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {

		Optional<Group> group = groupsService.findById(id);
		model.addAttribute("group", group.get());
		
		List<GroupFilms> films = groupFilmsService.findByGroup(id);
		model.addAttribute("films", films);
		
		// admin rigth
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());
		
		return "groups/detail";
	}

	@GetMapping("/list")
	public String list(Principal principal, HttpSession session, Model model, 
			@ModelAttribute FiltrosDto filtro) throws PermisoException {

		model.addAttribute("orden", filtro.getOrden().orElse("tituloGest"));
		model.addAttribute("direccion", filtro.getDireccion().orElse("ASC"));

		// admin rigth
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());

		// set filter to view
		model.addAttribute("filtro", filtro);
		session.setAttribute("filtro", filtro);

		// obtain the film by the filter
		Page<Group> resultado = groupsService.findAllBy(filtro);
		for(Group group: resultado) {
			List<GroupFilms> films = groupFilmsService.findByGroup(group.getId());
			group.setHasFilms(films.size()>0);
		}
		
		model.addAttribute("pageGroup", resultado);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		return "groups/list";
	}

}
