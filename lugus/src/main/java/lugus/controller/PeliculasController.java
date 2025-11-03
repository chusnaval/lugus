package lugus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.model.PeliculaFoto;
import lugus.service.LocalizacionService;
import lugus.service.PeliculaService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.awt.Image;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/peliculas")
@RequiredArgsConstructor
public class PeliculasController {

	private final PeliculaService service;

	private final LocalizacionService locService;

	/*
	 * ------------------------------------------------- LISTADO DE PELÍCULAS GET
	 * /peliculas -------------------------------------------------
	 */

	@GetMapping
	public String listPaginado(Model model, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) Optional<String> orden,
			@RequestParam(required = false) Optional<String> direccion,
			@RequestParam(required = false) Optional<Integer> pagina) {
		Page<Pelicula> resultado = service.findAllByTitulo(keyword, pagina.orElse(0), orden.orElse("titulo"),
				direccion.orElse("ASC"));
		model.addAttribute("pagePeliculas", resultado);
		String campoOrden = "titulo";
		String campoDireccion = "ASC";
		if (resultado.getSort().get().findFirst().isPresent()) {
			campoOrden = resultado.getSort().get().findFirst().get().getProperty();
			campoDireccion = resultado.getSort().get().findFirst().get().getDirection().name();
		}
		List<Localizacion> localizaciones = locService.findAllOrderByDescripcion();
		model.addAttribute("localizaciones", localizaciones);
		model.addAttribute("campoOrden", campoOrden);
		model.addAttribute("direccionOrden", campoDireccion);

		return "peliculas/list"; // → src/main/resources/templates/peliculas/list.html
	}

	/*
	 * ------------------------------------------------- FORMULARIO DE CREACIÓN GET
	 * /peliculas/nuevo -------------------------------------------------
	 */
	@GetMapping("/nuevo")
	public String createForm(Model model) {
		model.addAttribute("pelicula", new PeliculaCreateDto());
		return "peliculas/form"; // → templates/peliculas/form.html
	}

	/*
	 * ------------------------------------------------- GUARDAR NUEVA PELÍCULA POST
	 * /peliculas -------------------------------------------------
	 */
	@PostMapping
	public String create(@Valid @ModelAttribute("pelicula") PeliculaCreateDto dto, BindingResult br, Model model) {
		if (br.hasErrors()) {
			// Si hay errores de validación, volvemos al mismo formulario
			return "peliculas/form";
		}
		Pelicula creada = service.crear(dto);
		// Redirigimos al detalle de la película recién creada
		return "redirect:/peliculas/" + creada.getId();
	}

	/*
	 * ------------------------------------------------- DETALLE DE UNA PELÍCULA
	 * (incluye su pack) GET /peliculas/{id}
	 * -------------------------------------------------
	 */
	@GetMapping("/{id}")
	public String detail(@PathVariable Integer id, Model model) {
		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

		model.addAttribute("pelicula", p);
		return "peliculas/detail"; // → templates/peliculas/detail.html
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> image(@PathVariable Integer id) {
		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

		if (p.getPeliculaFotos() != null && !p.getPeliculaFotos().isEmpty()) {
			PeliculaFoto pf = p.getPeliculaFotos().iterator().next();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<>(pf.getFoto(), headers, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(new byte[0], HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/{id}/editar")
	public String edit(@PathVariable Integer id, Model model) {
		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));
		model.addAttribute("pelicula", p);
		// DTO vacío para el formulario “añadir hijo al pack”
		model.addAttribute("nuevoHijo", new PeliculaCreateDto());
		return "peliculas/edit"; // → templates/peliculas/detail.html
	}

	/*
	 * ------------------------------------------------- AÑADIR UN HIJO AL PACK POST
	 * /peliculas/{padreId}/hijo -------------------------------------------------
	 */
	@PostMapping("/{padreId}/hijo")
	public String addChild(@PathVariable Integer padreId, @Valid @ModelAttribute("nuevoHijo") PeliculaCreateDto dto,
			BindingResult br, Model model) {
		if (br.hasErrors()) {
			// Si hay errores, volvemos al detalle mostrando los mensajes
			return detail(padreId, model);
		}
		service.addChild(padreId, dto);
		// Después de añadir el hijo, recargamos el detalle del padre
		return "redirect:/peliculas/" + padreId;
	}

	/*
	 * ------------------------------------------------- ELIMINAR UNA PELÍCULA
	 * (opcional) DELETE /peliculas/{id}
	 * -------------------------------------------------
	 */
	@PostMapping("/{id}/eliminar") // usando POST para evitar problemas con browsers
	public String delete(@PathVariable Integer id) {
		service.delete(id);
		return "redirect:/peliculas";
	}
}