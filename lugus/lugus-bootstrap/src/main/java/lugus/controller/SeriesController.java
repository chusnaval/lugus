package lugus.controller;

import java.io.IOException;
import java.net.URISyntaxException;
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
import lugus.export.SeriesWantedExportService;
import lugus.dto.core.FiltrosDto;
import lugus.dto.media.NewCaratulaDTO;
import lugus.dto.series.SerieCreateDto;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.series.SerWanted;
import lugus.model.series.Serie;
import lugus.model.series.SerieFoto;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.films.DwFotoService;
import lugus.service.films.DwFotoServiceI;
import lugus.service.series.SerWantedService;
import lugus.service.series.SeriesService;

@Controller
@RequestMapping("/series")
@RequiredArgsConstructor
public class SeriesController {

	private static final String SERIE = "serie";

	private static final String FILTRO = "filtro";

	private static final String LOCATIONS = "locations";

	private static final String SERIE_NO_ENCONTRADA = "Serie no encontrada";

	private final SeriesService service;

	private final LocationService locService;

	private final SourceService sourceService;
	private final SerWantedService serWantedService;
	private final SeriesWantedExportService seriesWantedExportService;

	@GetMapping
	public String listPaginado(Model model, Principal principal, HttpSession session,
			@RequestParam(required = false) Boolean resetFilter, @RequestParam(required = false) Boolean recuperar,
			@ModelAttribute FiltrosDto filtro) {

		// si hay filtro anterior y no queremos reiniciarlo
		if ((resetFilter != null && resetFilter)) {
			filtro = new FiltrosDto();
			filtro.setOrden(Optional.of("tituloGest"));
			filtro.setPack(false);

		} else if ((recuperar != null && recuperar) && session.getAttribute(FILTRO) != null) {
				filtro = (FiltrosDto) session.getAttribute(FILTRO);
		}

		model.addAttribute("orden", filtro.getOrden().orElse("tituloGest"));
		model.addAttribute("direccion", filtro.getDireccion().orElse("ASC"));
		// set filter to view
		model.addAttribute(FILTRO, filtro);
		session.setAttribute(FILTRO, filtro);

		// obtain the film by the filter
		Page<Serie> resultado = service.findAllBy(filtro);
		model.addAttribute("pageSeries", resultado);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		// select for filter
		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute(LOCATIONS, locations);

		return "series/list";
	}

	@GetMapping("/wanted")
	public String wanted(Model model) {
		model.addAttribute("wantedList", serWantedService.findAllOrdered());
		return "series/wanted";
	}

	@GetMapping("/wanted/export")
	public ResponseEntity<?> exportWanted(@RequestParam String format) {
		List<SerWanted> list = serWantedService.findAllOrdered();

		if ("md".equalsIgnoreCase(format)) {
			String body = seriesWantedExportService.toMarkdown(list);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=series_buscadas.md")
					.contentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"))
					.body(body);
		}

		if ("ods".equalsIgnoreCase(format)) {
			try {
				byte[] body = seriesWantedExportService.toOds(list);
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=series_buscadas.ods")
						.contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet"))
						.body(body);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error generando ODS");
			}
		}

		if ("pdf".equalsIgnoreCase(format)) {
			try {
				byte[] body = seriesWantedExportService.toPdf(list);
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=series_buscadas.pdf")
						.contentType(MediaType.parseMediaType("application/pdf"))
						.body(body);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error generando PDF");
			}
		}

		return ResponseEntity.badRequest().body("Formato no soportado");
	}

	/*
	 * ------------------------------------------------- FORMULARIO DE CREACIÓN GET
	 * /series/nuevo -------------------------------------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/nuevo")
	public String createForm(Principal principal, Model model) {
		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute(LOCATIONS, locations);

		List<Source> sources = sourceService.findAll();
		model.addAttribute("sourcesList", sources);

		model.addAttribute(SERIE, new SerieCreateDto());
		
		return "series/new"; 
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public String create(Principal principal, @Valid @ModelAttribute(SERIE) SerieCreateDto dto,
			BindingResult br, Model model, HttpSession session) throws IOException, URISyntaxException {

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
	public String detail(Principal principal, @PathVariable Integer id, HttpSession session, Model model) {

		Serie p = service.findById(id).orElseThrow(() -> new IllegalArgumentException(SERIE_NO_ENCONTRADA));

		model.addAttribute(SERIE, p);

		FiltrosDto filtro = (FiltrosDto) session.getAttribute(FILTRO);
		model.addAttribute(FILTRO, filtro);

		return "series/detail";
	}
	
	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> image(Principal principal, @PathVariable Integer id) {

		Serie p = service.findById(id).orElseThrow(() -> new IllegalArgumentException(SERIE_NO_ENCONTRADA));

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
	public String edit(Principal principal, @PathVariable Integer id, HttpSession session, Model model) {
		Serie p = service.findById(id).orElseThrow(() -> new IllegalArgumentException(SERIE_NO_ENCONTRADA));
		model.addAttribute(SERIE, p);

		List<Source> sources = sourceService.findAll();
		model.addAttribute("sourcesList", sources);

		List<Location> locations = locService.findAll();
		model.addAttribute(LOCATIONS, locations);

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


		FiltrosDto filtro = (FiltrosDto) session.getAttribute(FILTRO);
		model.addAttribute(FILTRO, filtro);
		return "series/edit";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/actualizar/{id}")
	public String actualizar(Principal principal, HttpSession session, @PathVariable Integer id, RedirectAttributes ra,
			@Valid @ModelAttribute SerieCreateDto nuevo)  {

		Optional<Serie> opt = service.findById(id);
		
		if (opt.isEmpty()) {
			throw new IllegalArgumentException(SERIE_NO_ENCONTRADA);
		}
		
		Formato formato = Formato.getById(nuevo.getFormatoCodigo());
		Genero genero = Genero.getById(nuevo.getGeneroCodigo());
		
		Location loc = null;
		if (nuevo.getLocationCode() != null && !nuevo.getLocationCode().isBlank()) {
			loc = locService.findById(nuevo.getLocationCode())
			.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
		}
		
		Serie existing = opt.get();
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
			@Valid @ModelAttribute("caratula") NewCaratulaDTO dto) throws IOException, URISyntaxException {


		final DwFotoServiceI dwFotoService = new DwFotoService();
		Optional<Source> sourceObj = sourceService.findById(dto.getSource());
		SerieFoto pf = new SerieFoto();
		if(sourceObj.isPresent()) {
			pf.setSource(sourceObj.get());
		}
		pf.setUrl(dto.getUrl());
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

		return new ResponseEntity<>("Id no encontrado", HttpStatus.NO_CONTENT);
	}

}
