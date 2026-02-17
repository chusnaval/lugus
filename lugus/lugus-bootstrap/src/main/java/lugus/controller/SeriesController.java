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
import org.springframework.security.access.prepost.PreAuthorize;
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
import lugus.dto.core.FiltrosDto;
import lugus.dto.media.NewCaratulaDTO;
import lugus.dto.series.SerieCreateDto;
import lugus.exception.PermisoException;
import lugus.model.core.Source;
import lugus.model.core.Location;
import lugus.model.series.Serie;
import lugus.model.series.SerieFoto;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.service.core.SourceService;
import lugus.service.core.LocationService;
import lugus.service.films.DwFotoService;
import lugus.service.films.DwFotoServiceI;
import lugus.service.series.SeriesService;

@Controller
@RequestMapping("/series")
@RequiredArgsConstructor
public class SeriesController {

	private final SeriesService service;

	private final LocationService locService;

	private final SourceService sourceService;
	
	@GetMapping
	public String listPaginado(Model model, Principal principal, HttpSession session,
			@RequestParam(required = false) Boolean resetFilter, @RequestParam(required = false) Boolean recuperar,
			@ModelAttribute FiltrosDto filtro) {

		// si hay filtro anterior y no queremos reiniciarlo
		if ((resetFilter != null && resetFilter)) {
			filtro = new FiltrosDto();
			filtro.setOrden(Optional.of("tituloGest"));
			filtro.setPack(false);

		} else if ((recuperar != null && recuperar)) {

			if (session.getAttribute("filtro") != null) {
				filtro = (FiltrosDto) session.getAttribute("filtro");
			}
		}

		model.addAttribute("orden", filtro.getOrden().orElse("tituloGest"));
		model.addAttribute("direccion", filtro.getDireccion().orElse("ASC"));
		// set filter to view
		model.addAttribute("filtro", filtro);
		session.setAttribute("filtro", filtro);

		// obtain the film by the filter
		Page<Serie> resultado = service.findAllBy(filtro);
		model.addAttribute("pageSeries", resultado);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		// select for filter
		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute("locations", locations);

		return "series/list";
	}

	/*
	 * ------------------------------------------------- FORMULARIO DE CREACIÓN GET
	 * /series/nuevo -------------------------------------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/nuevo")
	public String createForm(Principal principal, Model model) throws PermisoException {

		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute("locations", locations);

		List<Source> sources = sourceService.findAll();
		model.addAttribute("sourcesList", sources);

		model.addAttribute("serie", new SerieCreateDto());
		
		return "series/new"; 
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public String create(Principal principal, @Valid @ModelAttribute("serie") SerieCreateDto dto,
			BindingResult br, Model model, HttpSession session) throws PermisoException, IOException {

		if (br.hasErrors()) {
			// Si hay errores de validación, volvemos al mismo formulario
			return "series/form";
		}
		Serie creada = service.crear(dto);
		// Redirigimos al detalle de la serie  recién creada
		return "redirect:/series/" + creada.getId();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
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

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/editar/{id}")
	public String edit(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {

		Serie p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Serie no encontrada"));
		model.addAttribute("serie", p);

		List<Source> sources = sourceService.findAll();
		model.addAttribute("sourcesList", sources);

		List<Location> locations = locService.findAll();
		model.addAttribute("locations", locations);

		model.addAttribute("caratula", new NewCaratulaDTO());

		SerieCreateDto nuevo = new SerieCreateDto();
		nuevo.setAnyoInicio(p.getAnyoInicio());
		nuevo.setAnyoFin(p.getAnyoFin());
		nuevo.setTitulo(p.getTitulo());
		nuevo.setTituloGest(p.getTituloGest());
		nuevo.setFormatoCodigo(p.getFormato().getId());
		nuevo.setGeneroCodigo(p.getGenero().getCodigo());
		nuevo.setComprado(p.isComprado());
		nuevo.setCompleta(p.isCompleta());

		if (p.getLocation() != null) {
			nuevo.setLocationCode(p.getLocation().getCodigo());
		}

		model.addAttribute("nuevo", nuevo);


		FiltrosDto filtro = (FiltrosDto) session.getAttribute("filtro");
		model.addAttribute("filtro", filtro);
		return "series/edit";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/actualizar/{id}")
	public String actualizar(Principal principal, HttpSession session, @PathVariable Integer id, RedirectAttributes ra,
			@Valid @ModelAttribute SerieCreateDto nuevo) throws PermisoException {

		Optional<Serie> opt = service.findById(id);

		Serie existing = opt.get();

		if (existing == null) {
			new IllegalArgumentException("Serie no encontrada");
		}

		Formato formato = Formato.getById(nuevo.getFormatoCodigo());
		Genero genero = Genero.getById(nuevo.getGeneroCodigo());

		Location loc = null;
		if (nuevo.getLocationCode() != null && !nuevo.getLocationCode().isBlank()) {
			loc = locService.findById(nuevo.getLocationCode())
					.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
		}

		existing.setTitulo(nuevo.getTitulo());
		existing.setTituloGest(nuevo.getTituloGest());
		existing.setFormato(formato);
		existing.setLocation(loc);
		existing.setAnyoInicio(nuevo.getAnyoInicio());
		existing.setAnyoFin(nuevo.getAnyoFin());
		existing.setGenero(genero);
		existing.setNotas(nuevo.getNotas());
		existing.setComprado(nuevo.isComprado());
		existing.setCompleta(nuevo.isCompleta());
		existing.calcularCodigo();
		service.save(existing);

		return "redirect:/series?recuperar=true";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/{id}/caratula")
	public ResponseEntity<String> addCaratula(Principal principal, @PathVariable Integer id,
			@Valid @ModelAttribute("caratula") NewCaratulaDTO dto) throws IOException, PermisoException {


		final DwFotoServiceI dwFotoService = new DwFotoService();
		Optional<Source> sourceObj = sourceService.findById(dto.getSource());
		SerieFoto pf = new SerieFoto();
		pf.setUrl(dto.getUrl());
		pf.setSource(sourceObj.get());
		pf.setFoto(dwFotoService.descargar(dto.getSource(), dto.getUrl()));
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
