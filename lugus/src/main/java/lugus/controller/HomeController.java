package lugus.controller;

import lombok.RequiredArgsConstructor;
import lugus.model.Pelicula;
import lugus.model.Usuario;
import lugus.service.PeliculaService;
import lugus.service.UsuarioService;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final PeliculaService peliculaService;
	private final UsuarioService usuarioService;
	
	@GetMapping("/home")
	public String home(Model model, Principal principal, HttpSession session) {
		
		Page<Pelicula> resultado = peliculaService.findForHome();
		model.addAttribute("pagePeliculas", resultado);

		// admin rigth
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());

		return "home";
	}
}