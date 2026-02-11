package lugus.controller;

import lombok.RequiredArgsConstructor;
import lugus.service.films.PeliculaService;
import lugus.service.series.SeriesService;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final PeliculaService peliculaService;
	private final SeriesService serieService;
	
	@GetMapping("/home")
	public String home(Model model, Principal principal, HttpSession session) {
		
		model.addAttribute("pagePeliculas",  peliculaService.findForHome());

		model.addAttribute("pageSeries", serieService.findForHome());
		

		return "home";
	}
}