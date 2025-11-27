package lugus.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.PermisoException;
import lugus.dto.FiltrosDto;
import lugus.dto.NewCaratulaDTO;
import lugus.dto.SerieCreateDto;
import lugus.model.Formato;
import lugus.model.Fuente;
import lugus.model.Genero;
import lugus.model.Localizacion;
import lugus.model.Serie;
import lugus.model.SerieFoto;
import lugus.model.Usuario;
import lugus.service.DwFotoService;
import lugus.service.DwFotoServiceI;
import lugus.service.FuenteService;
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

	private final FuenteService fuenteService;
	
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

	/*
	 * ------------------------------------------------- FORMULARIO DE CREACIÓN GET
	 * /series/nuevo -------------------------------------------------
	 */
	@GetMapping("/nuevo")
	public String createForm(Principal principal, Model model) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		List<Localizacion> localizaciones = locService.findAllOrderByDescripcion();
		model.addAttribute("localizaciones", localizaciones);

		List<Fuente> fuentes = fuenteService.findAll();
		model.addAttribute("fuentesList", fuentes);

		model.addAttribute("serie", new SerieCreateDto());
		return "series/new"; 
	}


	@PostMapping
	public String create(Principal principal, @Valid @ModelAttribute("serie") SerieCreateDto dto,
			BindingResult br, Model model, HttpSession session) throws PermisoException, IOException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		if (br.hasErrors()) {
			// Si hay errores de validación, volvemos al mismo formulario
			return "series/form";
		}
		Serie creada = service.crear(dto, session);
		// Redirigimos al detalle de la serie  recién creada
		return "redirect:/series/" + creada.getId();
	}
	
	@GetMapping("/{id}")
	public String detail(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {

		Serie p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Serie no encontrada"));

		model.addAttribute("serie", p);

		FiltrosDto filtro = (FiltrosDto) session.getAttribute("filtro");
		model.addAttribute("filtro", filtro);

		return "series/detail";
	}
	
	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> image(Principal principal, @PathVariable Integer id) throws PermisoException {

		Serie p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Serie no encontrada"));

		if (p.getSerieFotos() != null && !p.getSerieFotos().isEmpty()) {
			SerieFoto pf = p.getSerieFotos().iterator().next();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<>(pf.getFoto(), headers, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(new byte[0], HttpStatus.NOT_FOUND);
		}

	}

	@PostMapping("/volver")
	public String volver(HttpSession session, RedirectAttributes ra) {

		return "redirect:/series?recuperar=true";
	}

	@GetMapping("/editar/{id}")
	public String edit(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		Serie p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Serie no encontrada"));
		model.addAttribute("serie", p);

		List<Fuente> fuentes = fuenteService.findAll();
		model.addAttribute("fuentesList", fuentes);

		List<Localizacion> localizaciones = locService.findAll();
		model.addAttribute("localizaciones", localizaciones);

		model.addAttribute("caratula", new NewCaratulaDTO());

		SerieCreateDto nuevo = new SerieCreateDto();
		nuevo.setAnyoInicio(p.getAnyoInicio());
		nuevo.setAnyoFin(p.getAnyoFin());
		nuevo.setTitulo(p.getTitulo());
		nuevo.setTituloGest(p.getTituloGest());
		nuevo.setFormatoCodigo(p.getFormato().getId());
		nuevo.setGeneroCodigo(p.getGenero().getCodigo());
		nuevo.setComprado(p.isComprado());

		if (p.getLocalizacion() != null) {
			nuevo.setLocalizacionCodigo(p.getLocalizacion().getCodigo());
		}

		model.addAttribute("nuevo", nuevo);


		FiltrosDto filtro = (FiltrosDto) session.getAttribute("filtro");
		model.addAttribute("filtro", filtro);
		return "series/edit";
	}

	@PostMapping("/actualizar/{id}")
	public String actualizar(Principal principal, HttpSession session, @PathVariable Integer id, RedirectAttributes ra,
			@Valid @ModelAttribute SerieCreateDto nuevo) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		Optional<Serie> opt = service.findById(id);

		Serie existing = opt.get();

		if (existing == null) {
			new IllegalArgumentException("Serie no encontrada");
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
		existing.setAnyoInicio(nuevo.getAnyoInicio());
		existing.setAnyoFin(nuevo.getAnyoFin());
		existing.setGenero(genero);
		existing.setNotas(nuevo.getNotas());
		existing.setComprado(nuevo.isComprado());
		existing.calcularCodigo();
		service.save(existing);

		return "redirect:/series?recuperar=true";
	}
	
	@PostMapping("/{id}/caratula")
	public ResponseEntity<String> addCaratula(Principal principal, @PathVariable Integer id,
			@Valid @ModelAttribute("caratula") NewCaratulaDTO dto) throws IOException, PermisoException {

		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		final DwFotoServiceI dwFotoService = new DwFotoService();
		Optional<Fuente> fuenteObj = fuenteService.findById(dto.getFuente());
		SerieFoto pf = new SerieFoto();
		pf.setUrl(dto.getUrl());
		pf.setFuente(fuenteObj.get());
		pf.setFoto(dwFotoService.descargar(dto.getFuente(), dto.getUrl()));
		pf.setCaratula(true);

		Optional<Serie> serie = service.findById(id);

		if (serie.isPresent()) {
			serie.get().getSerieFotos().clear();
			service.save(serie.get());
			serie.get().addCaratula(pf);
			service.save(serie.get());
			return ResponseEntity.ok("Descargado");
		}

		return new ResponseEntity<String>("Id no encontrado", HttpStatus.NO_CONTENT);
	}

}
