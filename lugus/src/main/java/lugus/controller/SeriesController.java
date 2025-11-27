package lugus.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.dto.FiltrosDto;
import lugus.model.Localizacion;
import lugus.model.Serie;
import lugus.model.Usuario;
import lugus.service.LocalizacionService;
import lugus.service.SerieService;
import lugus.service.UsuarioService;

@Controller
@RequestMapping("/series")
@RequiredArgsConstructor
public class SeriesController {

	private final SerieService service;
	
	private final LocalizacionService locService;
	
	private final UsuarioService usuarioService;
	@GetMapping
	public String listPaginado(Model model, Principal principal, HttpSession session,
			@RequestParam(required = false) Boolean resetFilter, @RequestParam(required = false) Boolean recuperar,
			@ModelAttribute FiltrosDto filtro) {

		// si hay filtro anterior y no queremos reiniciarlo
		if ((resetFilter != null && resetFilter)) {
			filtro = new FiltrosDto();
			filtro.setOrden(Optional.of("tituloGest"));
			filtro.setPack(false);
			filtro.setComprado(true);

		} else if ((recuperar != null && recuperar)) {

			if (session.getAttribute("filtro") != null) {
				filtro = (FiltrosDto) session.getAttribute("filtro");
			}
		}

		// set filter to view
		model.addAttribute("filtro", filtro);
		session.setAttribute("filtro", filtro);

		// obtain the film by the filter
		Page<Serie> resultado = service.findAllBy(filtro);
		model.addAttribute("pageSeries", resultado);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		// select for filter
		List<Localizacion> localizaciones = locService.findAllOrderByDescripcion();
		model.addAttribute("localizaciones", localizaciones);

		// admin rigth
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());

		return "series/list";
	}
	
	@PostMapping("/volver")
	public String volver(HttpSession session, RedirectAttributes ra) {

		return "redirect:/series?recuperar=true";
	}

}
