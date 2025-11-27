package lugus.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/guardarUsuario")
	public String guardarUsuario(Principal principal, HttpSession session) {
		session.removeAttribute("filtro");
		session.setAttribute("usuarioConectado", principal.getName());
		return "redirect:/home";
	}

}
