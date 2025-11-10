package lugus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.PermisoException;
import lugus.dto.FiltrosDto;
import lugus.dto.NewCaratulaDTO;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Formato;
import lugus.model.Fuente;
import lugus.model.Genero;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.model.PeliculaFoto;
import lugus.model.Usuario;
import lugus.service.DwFotoServiceI;
import lugus.service.FuenteService;
import lugus.service.LocalizacionService;
import lugus.service.PeliculaService;
import lugus.service.UsuarioService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/peliculas")
@RequiredArgsConstructor
public class PeliculasController {

	private final PeliculaService service;

	private final LocalizacionService locService;

	private final FuenteService fuenteService;
	
	private final UsuarioService usuarioService;

	/*
	 * ------------------------------------------------- LISTADO DE PELÍCULAS GET
	 * /peliculas -------------------------------------------------
	 */

	@GetMapping
	public String listPaginado(Model model, Principal principal,  @RequestParam(required = false) Optional<String> orden,
			@RequestParam(required = false) Optional<String> direccion,
			@RequestParam(required = false) Optional<Integer> pagina, @ModelAttribute FiltrosDto filtro) {

		Page<Pelicula> resultado = service.findAllBy(filtro, pagina.orElse(0), orden.orElse("tituloGest"),
				direccion.orElse("ASC"));
		model.addAttribute("pagePeliculas", resultado);

		String campoOrden = "tituloGest";
		model.addAttribute("campoOrden", campoOrden);

		String campoDireccion = "ASC";
		if (resultado.getSort().get().findFirst().isPresent()) {
			campoOrden = resultado.getSort().get().findFirst().get().getProperty();
			campoDireccion = resultado.getSort().get().findFirst().get().getDirection().name();
		}
		model.addAttribute("direccionOrden", campoDireccion);

		filtro.setPagina(pagina.orElse(0));
		filtro.setOrden(campoOrden);
		filtro.setDireccion(campoDireccion);
		model.addAttribute("filtro", filtro);
		
		model.addAttribute("numeroPagina", pagina.orElse(0));

		List<Localizacion> localizaciones = locService.findAllOrderByDescripcion();
		model.addAttribute("localizaciones", localizaciones);
		
		
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		
		model.addAttribute("admin", usuario.isAdmin());

		return "peliculas/list"; // → src/main/resources/templates/peliculas/list.html
	}

	/*
	 * ------------------------------------------------- FORMULARIO DE CREACIÓN GET
	 * /peliculas/nuevo -------------------------------------------------
	 */
	@GetMapping("/nuevo")
	public String createForm(Principal principal, Model model) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		model.addAttribute("pelicula", new PeliculaCreateDto());
		return "peliculas/form"; // → templates/peliculas/form.html
	}

	/*
	 * ------------------------------------------------- GUARDAR NUEVA PELÍCULA POST
	 * /peliculas -------------------------------------------------
	 */
	@PostMapping
	public String create(Principal principal, @Valid @ModelAttribute("pelicula") PeliculaCreateDto dto, BindingResult br, Model model) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		
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
	public String detail(Principal principal, @PathVariable Integer id, Model model) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		
		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

		model.addAttribute("pelicula", p);
		return "peliculas/detail"; // → templates/peliculas/detail.html
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> image(Principal principal, @PathVariable Integer id) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		
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
	public String edit(Principal principal, @PathVariable Integer id, Model model) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		
		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));
		model.addAttribute("pelicula", p);

		List<Fuente> fuentes = fuenteService.findAll();
		model.addAttribute("fuentesList", fuentes);

		List<Localizacion> localizaciones = locService.findAll();
		model.addAttribute("localizaciones", localizaciones);

		model.addAttribute("caratula", new NewCaratulaDTO());
		// DTO vacío para el formulario “añadir hijo al pack”
		model.addAttribute("nuevoHijo", new PeliculaCreateDto());

		PeliculaCreateDto nuevo = new PeliculaCreateDto();
		nuevo.setAnyo(p.getAnyo());
		nuevo.setTitulo(p.getTitulo());
		nuevo.setTituloGest(p.getTituloGest());
		nuevo.setFormatoCodigo(p.getFormato().getId());
		nuevo.setGeneroCodigo(p.getGenero().getCodigo());
		nuevo.setFunda(p.isFunda());
		nuevo.setComprado(p.isComprado());
		nuevo.setSteelbook(p.isSteelbook());
		nuevo.setPack(p.isPack());

		if (p.getLocalizacion() != null) {
			nuevo.setLocalizacionCodigo(p.getLocalizacion().getCodigo());
		}

		model.addAttribute("nuevo", nuevo);

		return "peliculas/edit"; // → templates/peliculas/detail.html
	}

	@PostMapping("/{id}/actualizar")
	public String actualizar(Principal principal, @PathVariable Integer id, @Valid @ModelAttribute PeliculaCreateDto nuevo) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		
		Optional<Pelicula> opt = service.findById(id);

		Pelicula existing = opt.get();

		if (existing == null) {
			new IllegalArgumentException("Pelicula no encontrada");
		}

		Formato formato = Formato.getById(nuevo.getFormatoCodigo());
		Genero genero = Genero.getById(nuevo.getGeneroCodigo());

		Localizacion loc = null;
		if (nuevo.getLocalizacionCodigo() != null && !nuevo.getLocalizacionCodigo().isBlank()) {
			loc = locService.findById(nuevo.getLocalizacionCodigo())
					.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
		}

		existing.setTitulo(nuevo.getTitulo());
		existing.setTituloGest(nuevo.getTituloGest());
		existing.setFormato(formato);
		existing.setLocalizacion(loc);
		existing.setAnyo(nuevo.getAnyo());
		existing.setGenero(genero);
		existing.setPack(nuevo.isPack());
		existing.setNotas(nuevo.getNotas());
		existing.setSteelbook(nuevo.isSteelbook());
		existing.setFunda(nuevo.isFunda());
		existing.calcularCodigo();
		service.save(existing);

		return "redirect:/peliculas/" + id + "/editar";
	}

	@PostMapping("/{id}/caratula")
	public ResponseEntity<String> addCaratula(Principal principal, @PathVariable Integer id,
			@Valid @ModelAttribute("caratula") NewCaratulaDTO dto) throws IOException, PermisoException {
		
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		final DwFotoServiceI dwFotoService = new DwFotoService();
		Optional<Fuente> fuenteObj = fuenteService.findById(dto.getFuente());
		PeliculaFoto pf = new PeliculaFoto();
		pf.setUrl(dto.getUrl());
		pf.setFuente(fuenteObj.get());
		pf.setFoto(dwFotoService.descargar(dto.getFuente(), dto.getUrl()));
		pf.setCaratula(true);

		Optional<Pelicula> pelicula = service.findById(id);

		if (pelicula.isPresent()) {
			pelicula.get().addCaratula(pf);
			service.save(pelicula.get());
			return ResponseEntity.ok("Descargado");
		}

		return new ResponseEntity<String>("Id no encontrado", HttpStatus.NO_CONTENT);
	}

	/*
	 * ------------------------------------------------- AÑADIR UN HIJO AL PACK POST
	 * /peliculas/{padreId}/hijo -------------------------------------------------
	 */
	@PostMapping("/{padreId}/hijo")
	public String addChild(Principal principal, @PathVariable Integer padreId, @Valid @ModelAttribute("nuevoHijo") PeliculaCreateDto dto,
			BindingResult br, Model model) throws PermisoException {
		
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		
		if (br.hasErrors()) {
			// Si hay errores, volvemos al detalle mostrando los mensajes
			return detail(principal, padreId, model);
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
	public String delete(Principal principal, @PathVariable Integer id) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if(!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}
		
		service.delete(id);
		return "redirect:/peliculas";
	}
}