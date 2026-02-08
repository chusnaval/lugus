package lugus.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.exception.PermisoException;
import lugus.model.core.Location;
import lugus.model.user.Usuario;
import lugus.service.core.LocationService;
import lugus.service.user.UsuarioService;

@Controller
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

	private final LocationService service;
	
	private final UsuarioService usuarioService;
	
	@GetMapping("/list")
	public String list(Principal principal, HttpSession session, Model model, 
			@ModelAttribute FiltrosDto filtro) throws PermisoException {

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
}
