package lugus.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.exception.PermisoException;
import lugus.model.groups.Group;
import lugus.model.groups.GroupFilms;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.films.Pelicula;
import lugus.service.groups.GroupFilmsService;
import lugus.service.groups.GroupsService;
import lugus.service.films.PeliculaService;
import lugus.service.imdb.ImdbTitleBasicsService;

@Controller
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

	private final GroupsService groupsService;
	
	private final GroupFilmsService groupFilmsService;

	private final PeliculaService peliculaService;

	private final ImdbTitleBasicsService imdbTitleBasicsService;

	@GetMapping("/{id}")
	public String detail(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {

		Optional<Group> group = groupsService.findById(id);
		model.addAttribute("group", group.get());
		
		List<GroupFilms> films = groupFilmsService.findByGroup(id);
		model.addAttribute("films", films);
		
		return "groups/detail";
	}

	@GetMapping("/list")
	public String list(Principal principal, HttpSession session, Model model, 
			@ModelAttribute FiltrosDto filtro) throws PermisoException {

		model.addAttribute("orden", filtro.getOrden().orElse("tituloGest"));
		model.addAttribute("direccion", filtro.getDireccion().orElse("ASC"));

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
	
	/**
	 * Borrdo de un grupo
	 */
	@PostMapping("/delete/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String delete(Principal principal, @PathVariable String id, HttpSession session, Model model)
			throws PermisoException {

		groupsService.delete(Integer.parseInt(id));
		
		return "redirect:/group/list";
	}

	/**
	 * Core logic to add movie to a group; returns a status string for the outcome.
	 */
	private String addMovieInternal(Integer groupId, String movieId, String tipo) throws PermisoException {
		Optional<Group> groupOpt = groupsService.findById(groupId);
		if (groupOpt.isEmpty()) {
			return "groupNotFound";
		}
		Group group = groupOpt.get();

		GroupFilms gf = new GroupFilms();
		gf.setGroup(group);
		
		if(tipo.equals("imdb")) {
			Optional<ImdbTitleBasics> itbOpt = imdbTitleBasicsService.findById(movieId);
			if (itbOpt.isPresent()) {
				gf.setItb(itbOpt.get());
			}
		} else if (tipo.equals("local")) {
			int pid = Integer.parseInt(movieId);
			Optional<Pelicula> p = peliculaService.findById(pid);
			if (p.isPresent()) {
				gf.setPelicula(p.get());
				Optional<ImdbTitleBasics> itbOpt = imdbTitleBasicsService.findById(p.get().getImdbId());
				if (itbOpt.isPresent()) {
					gf.setItb(itbOpt.get());
				}
			}
		}

		List<GroupFilms> existing = groupFilmsService.findByGroup(groupId);
		boolean duplicate = false;
		for (GroupFilms ex : existing) {
			if(tipo.equals("local")) {
				if (ex.getPelicula() != null && ex.getPelicula().getId() == Integer.parseInt(movieId)) {
					duplicate = true;
					break;
				}
				if (ex.getItb() != null && ex.getItb().getTconst().equals(movieId)) {
					duplicate = true;
					break;
				}	
			}
			if(tipo.equals("imdb")) {
				if (ex.getItb() != null && ex.getItb().getTconst().equals(movieId)) {
						duplicate = true;
						break;
					}
			}
		}

		if (duplicate) return "duplicate";

		int orden = groupFilmsService.nextOrderForGroup(groupId);
		gf.setOrden(orden);
		groupFilmsService.save(gf);
		return "added";
	}

	/**
	 * Add a movie to the group via normal form-post; keep redirect behavior for non-AJAX usage.
	 */
	@PostMapping("/{id}/addMovie")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String addMovieToGroup(Principal principal, @PathVariable Integer id, @RequestParam String movieId, @RequestParam String tipo) throws PermisoException {
		addMovieInternal(id, movieId, tipo);
		return "redirect:/group/" + id;
	}

	/**
	 * Add a movie via AJAX/fetch. Returns JSON with status/message so the client can show alerts.
	 */
	@PostMapping("/{id}/addMovieAjax")
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Map<String, String>> addMovieAjax(Principal principal, @PathVariable Integer id, @RequestParam String movieId, @RequestParam String tipo) throws PermisoException {
		String status = addMovieInternal(id, movieId, tipo);
		Map<String, String> body = new HashMap<>();
		if ("added".equals(status)) {
			body.put("status", "ok");
			body.put("message", "Película añadida al grupo");
			return ResponseEntity.ok(body);
		} else if ("duplicate".equals(status)) {
			body.put("status", "duplicate");
			body.put("message", "La película ya existe en el grupo");
			return ResponseEntity.ok(body);
		} else if ("groupNotFound".equals(status)) {
			body.put("status", "error");
			body.put("message", "Grupo no encontrado");
			return ResponseEntity.badRequest().body(body);
		} else {
			body.put("status", "error");
			body.put("message", "No se ha podido añadir la película");
			return ResponseEntity.badRequest().body(body);
		}
	}

	/**
	 * Remove a film from a group via AJAX. Expects parameter groupFilmId (int).
	 */
	@PostMapping("/{id}/removeFilmAjax")
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Map<String, String>> removeFilmAjax(Principal principal, @PathVariable Integer id, @RequestParam Integer groupFilmId) throws PermisoException {
		Map<String, String> body = new HashMap<>();
		Optional<GroupFilms> gfOpt = groupFilmsService.findById(groupFilmId);
		if (gfOpt.isEmpty()) {
			body.put("status", "error");
			body.put("message", "Entry not found");
			return ResponseEntity.badRequest().body(body);
		}
		GroupFilms gf = gfOpt.get();
		if (gf.getGroup() == null || gf.getGroup().getId() != id) {
			body.put("status", "error");
			body.put("message", "Group mismatch");
			return ResponseEntity.badRequest().body(body);
		}
		groupFilmsService.deleteById(groupFilmId);
		body.put("status", "ok");
		body.put("message", "Entrada eliminada");
		return ResponseEntity.ok(body);
	}

	/**
	 * Create a new group via AJAX. Expects 'name' and optional 'filmaffinityId'.
	 */
	@PostMapping("/createAjax")
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Map<String, Object>> createGroupAjax(Principal principal,
			@RequestParam String name,
			@RequestParam(required = false) String filmaffinityId) throws PermisoException {
		Map<String, Object> body = new HashMap<>();
		if (name == null || name.trim().isEmpty()) {
			body.put("status", "error");
			body.put("message", "El nombre es requerido");
			return ResponseEntity.badRequest().body(body);
		}
		Group g = new Group();
		g.setName(name.trim());
		if (filmaffinityId != null && !filmaffinityId.isBlank()) {
			g.setFilmaffinityId(Integer.valueOf(filmaffinityId.trim()));
		}
		Group saved = groupsService.saveGroup(g);
		body.put("status", "ok");
		body.put("id", saved.getId());
		body.put("name", saved.getName());
		return ResponseEntity.ok(body);
	}

	/**
	 * Update group's name and filmaffinityId via AJAX.
	 */
	@PostMapping("/{id}/updateAjax")
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Map<String, String>> updateGroupAjax(Principal principal, @PathVariable Integer id, @RequestParam String name, @RequestParam(required = false) String filmaffinityId) throws PermisoException {
		Map<String, String> body = new HashMap<>();
		Optional<Group> gOpt = groupsService.findById(id);
		if (gOpt.isEmpty()) {
			body.put("status", "error");
			body.put("message", "Grupo no encontrado");
			return ResponseEntity.badRequest().body(body);
		}
		Group g = gOpt.get();
		if (name == null || name.trim().isEmpty()) {
			body.put("status", "error");
			body.put("message", "El nombre es requerido");
			return ResponseEntity.badRequest().body(body);
		}
		g.setName(name.trim());
		if (filmaffinityId != null) {
			g.setFilmaffinityId(Integer.valueOf(filmaffinityId.trim()));
		}
		groupsService.saveGroup(g);
		body.put("status", "ok");
		body.put("message", "Grupo actualizado");
		return ResponseEntity.ok(body);
	}

}