package lugus.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.dto.core.LocationDTO;
import lugus.exception.LugusNotFoundException;
import lugus.exception.PermisoException;
import lugus.mapper.core.LocationMapper;
import lugus.model.core.Location;
import lugus.model.user.Usuario;
import lugus.service.core.LocationService;
import lugus.service.core.LocationTypeService;
import lugus.service.user.UsuarioService;

@Controller
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

	private final LocationService service;

	private final UsuarioService usuarioService;

	private final LocationTypeService locTypeService;

	private final LocationMapper mapper;

	@GetMapping("/new/")
	public String newLocation(Model model) {
		model.addAttribute("location", new LocationDTO());
		model.addAttribute("locTypes", locTypeService.findAll());
		return "management/locationNew";
	}

	@PostMapping("/saveNew")
	public ModelAndView saveNew(@ModelAttribute LocationDTO dto) throws IOException {
		Location location = mapper.mapToEntity(dto);
		service.save(location);
		return new ModelAndView("redirect:/locations/list");
	}

	@GetMapping("/list")
	public String list(Principal principal, HttpSession session, Model model, @ModelAttribute FiltrosDto filtro)
			throws PermisoException {

		model.addAttribute("orden", filtro.getOrden().orElse("descripcion"));
		model.addAttribute("direccion", filtro.getDireccion().orElse("ASC"));

		// admin rigth
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());

		// set filter to view
		model.addAttribute("filtro", filtro);
		session.setAttribute("filtro", filtro);

		// obtain the film by the filter
		Page<Location> resultado = service.findAllBy(filtro);

		model.addAttribute("locGroup", resultado);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		return "management/locations";
	}

	@GetMapping("/deleteLoc/{id}")
	public ModelAndView delete(Principal principal, @PathVariable String id, Model model) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tienes permiso para hacer esto");
		}

		Optional<Location> loc = service.findById(id);
		if (loc.isEmpty()) {
			throw new LugusNotFoundException(id);
		}

		if (!loc.get().getPeliculas().isEmpty()) {
			throw new PermisoException("No se puede borrar una ubicación con peliculas asignadas");
		}

		service.deleteById(id);
		model.addAttribute("admin", usuario.isAdmin());
		return new ModelAndView("redirect:/locations/list");
	}

	@GetMapping("/editLoc/{id}")
	public String edit(Principal principal, @PathVariable String id, Model model) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tienes permiso para hacer esto");
		}

		Optional<Location> loc = service.findById(id);
		if (loc.isEmpty()) {
			throw new LugusNotFoundException(id);
		}
		model.addAttribute("location", loc.get());

		List<Location> locations = service.findAllExcept(id);
		List<LocationDTO> dtos = new ArrayList<>();
		for (Location aux : locations) {
			dtos.add(mapper.mapToDTO(aux));
		}
		model.addAttribute("otherLocs", dtos);

		model.addAttribute("admin", usuario.isAdmin());
		return "management/editLocations";
	}
}
