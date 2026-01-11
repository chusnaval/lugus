package lugus.controller;

import lombok.RequiredArgsConstructor;
import lugus.model.user.Usuario;
import lugus.service.PeliculaService;
import lugus.service.SerieService;
import lugus.service.UsuarioService;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final PeliculaService peliculaService;
	private final SerieService serieService;
	private final UsuarioService usuarioService;
	
	@GetMapping("/home")
	public String home(Model model, Principal principal, HttpSession session) {
		
		model.addAttribute("pagePeliculas",  peliculaService.findForHome());

		model.addAttribute("pageSeries", serieService.findForHome());
		
		// admin
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());

		return "home";
	}
}