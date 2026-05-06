package lugus.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.PeliculaFavoritaDto;
import lugus.model.films.Pelicula;
import lugus.service.films.PeliculaService;

@Controller
@RequestMapping("/covers")
@RequiredArgsConstructor
public class CoverController {
	private static final String FILTRO_STRING = "filtro";
	private final PeliculaService service;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/list")
	public String listPaginado(Model model, Principal principal, HttpSession session) {

		FiltrosDto filtro = FiltrosDto.reset("tituloGest");
		filtro.setTieneCaratula(false);

		model.addAttribute("orden", filtro.getOrden().orElse("tituloGest"));
		model.addAttribute("direccion", filtro.getDireccion().orElse("ASC"));
		// set filter to view
		model.addAttribute(FILTRO_STRING, filtro);
		session.setAttribute(FILTRO_STRING, filtro);
		Page<Pelicula> resultado = service.findAllBy(filtro);
		Page<PeliculaFavoritaDto> resultadoDto = resultado.map(p -> PeliculaFavoritaDto.builder().id(p.getId())
				.titulo(p.getTitulo()).anyo(p.getAnyo()).formatoCodigo(p.getCodigo()).formato(p.getFormato().name())
				.generoCodigo(p.getGenero().getCodigo()).tieneCaratula(p.tieneCaratula()).notas(p.getNotas())
				.ratingFormatted(p.getRatingFormatted())
				.location(p.getLocation() != null ? p.getLocation().getDescripcion() : null).comprado(p.isComprado())
				.funda(p.isFunda()).build());
		model.addAttribute("pagePeliculas", resultadoDto);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		return "management/covers";
	}

}
