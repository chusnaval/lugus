package lugus.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.config.StorageProperties;
import lugus.export.FilmExportService;
import lugus.export.FilmWantedExportService;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.PeliculaChildDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.dto.films.PeliculaFavoritaDto;
import lugus.dto.media.NewCaratulaDTO;
import lugus.model.core.Source;
import lugus.model.core.Estado;
import lugus.model.core.Location;
import lugus.model.core.LocationType;
import lugus.model.films.FilmWanted;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.model.groups.GroupFilms;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.people.Actor;
import lugus.model.people.Director;
import lugus.model.user.FavoritosUsuario;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.service.core.SourceService;
import lugus.service.core.EstadoService;
import lugus.service.core.LocationService;
import lugus.service.core.LocationTypeService;
import lugus.service.films.DwFotoService;
import lugus.service.films.DwFotoServiceI;
import lugus.service.films.PeliculaService;
import lugus.service.groups.GroupFilmsService;
import lugus.service.imdb.ImdbTitleAkasService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.service.people.ActorService;
import lugus.service.people.DirectorService;
import lugus.service.people.FilmWantedService;
import lugus.service.people.InsertPersonalDataService;
import lugus.service.user.FavoritosUsuarioService;
import lugus.service.user.UsuarioService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/peliculas")
@RequiredArgsConstructor
public class PeliculasController {

	private static final String SOURCES_LIST = "sourcesList";

	private static final String PELICULA = "pelicula";

	private static final String LOCATIONS_STRING = "locations";

	private static final String FILTRO_STRING = "filtro";

	private static final String PELICULA_NO_ENCONTRADA = "Película no encontrada";

	private final PeliculaService service;

	private final LocationService locService;
	
	private final EstadoService estadoService;

	private final SourceService sourceService;

	private final FilmWantedService filmWantedService;

	private final FilmWantedExportService filmWantedExportService;
	
	private final FilmExportService filmExportService;

	private final DirectorService directorService;

	private final ActorService actorService;

	private final InsertPersonalDataService insertImdbService;

	private final LocationTypeService locationTypeService;

	private final StorageProperties storageProperties;

	private final ImdbTitleBasicsService imdbTitleBasicsService;

	private final ImdbTitleAkasService imdbTitleAkasService;

	private final GroupFilmsService groupFilmsService;

	private final FavoritosUsuarioService favoritosUsuarioService;

	private final UsuarioService usuarioService;

	/*
	 * ------------------------------------------------- LISTADO DE PELÍCULAS GET
	 * /peliculas -------------------------------------------------
	 */

	@GetMapping
	public String listPaginado(Model model, Principal principal, HttpSession session,
			@RequestParam(required = false) Boolean resetFilter, @RequestParam(required = false) Boolean recuperar,
			@ModelAttribute FiltrosDto filtro) {

		// si hay filtro anterior y no queremos reiniciarlo
		if ((resetFilter != null && resetFilter)) {
			filtro = resetearFiltro();

		} else if ((recuperar != null && recuperar)) {

			filtro = recuperarFiltro(session, filtro);
		}

		model.addAttribute("orden", filtro.getOrden().orElse("tituloGest"));
		model.addAttribute("direccion", filtro.getDireccion().orElse("ASC"));
		// set filter to view
		model.addAttribute(FILTRO_STRING, filtro);
		session.setAttribute(FILTRO_STRING, filtro);

		// obtain the film by the filter
		Page<Pelicula> resultado = service.findAllBy(filtro);
		final Set<Integer> favoritasIds;
		if (principal != null) {
			String login = principal.getName();
			var usuarioOpt = usuarioService.findByLogin(login);
			if (usuarioOpt.isPresent()) {
				List<FavoritosUsuario> favoritas = favoritosUsuarioService.findByUsuario(usuarioOpt.get());
				favoritasIds = favoritas.stream().map(f -> f.getPelicula().getId()).collect(Collectors.toSet());
			} else {
				favoritasIds = Set.of();
			}
		} else {
			favoritasIds = Set.of();
		}
		Page<PeliculaFavoritaDto> resultadoDto = resultado.map(p -> PeliculaFavoritaDto.builder()
			.id(p.getId())
			.titulo(p.getTitulo())
			.anyo(p.getAnyo())
			.formatoCodigo(p.getCodigo())
			.formato(p.getFormato().name())
			.generoCodigo(p.getGenero().getCodigo())
			.favorita(favoritasIds.contains(p.getId()))
			.tieneCaratula(p.tieneCaratula())
			.notas(p.getNotas())
			.ratingFormatted(p.getRatingFormatted())
			.location(p.getLocation() != null ? p.getLocation().getDescripcion() : null)
			.comprado(p.isComprado())
			.funda(p.isFunda())
			.build()
		);
		model.addAttribute("pagePeliculas", resultadoDto);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		// select for filter
		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute(LOCATIONS_STRING, locations);

		return "peliculas/list";
	}

	@GetMapping("/wanted")
	public String wanted(Model model) {
		model.addAttribute("wantedList", filmWantedService.findAllOrdered());
		return "peliculas/wanted";
	}

	@GetMapping("/report")
	public String report(Model model) {
		
		List<Pelicula> films = service.findAllOrdered();
		films.forEach(f -> f.calcularSituacion());
		
		model.addAttribute("reportList", films);
		return "peliculas/report";
	}
	
	@GetMapping("/wanted/export")
	public ResponseEntity<?> exportWanted(@RequestParam String format) {
		List<FilmWanted> list = filmWantedService.findAllOrdered();

		if ("md".equalsIgnoreCase(format)) {
			String body = filmWantedExportService.toMarkdown(list);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas_buscadas.md")
					.contentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"))
					.body(body);
		}

		if ("ods".equalsIgnoreCase(format)) {
			try {
				byte[] body = filmWantedExportService.toOds(list);
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas_buscadas.ods")
						.contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet"))
						.body(body);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error generando ODS");
			}
		}

		if ("pdf".equalsIgnoreCase(format)) {
			try {
				byte[] body = filmWantedExportService.toPdf(list);
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas_buscadas.pdf")
						.contentType(MediaType.parseMediaType("application/pdf; charset=UTF-8"))
						.body(body);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error generando PDF");
			}
		}

		return ResponseEntity.badRequest().body("Formato no soportado");
	}
	
	@GetMapping("/report/export")
	public ResponseEntity<?> reportWanted(@RequestParam String format) {
		List<Pelicula> list = service.findAllOrdered();

		if ("md".equalsIgnoreCase(format)) {
			String body = filmExportService.toMarkdown(list);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.md")
					.contentType(MediaType.parseMediaType("text/markdown; charset=UTF-8"))
					.body(body);
		}

		if ("ods".equalsIgnoreCase(format)) {
			try {
				byte[] body = filmExportService.toOds(list);
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.ods")
						.contentType(MediaType.parseMediaType("application/vnd.oasis.opendocument.spreadsheet"))
						.body(body);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error generando ODS");
			}
		}

		if ("pdf".equalsIgnoreCase(format)) {
			try {
				byte[] body = filmExportService.toPdf(list);
				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=peliculas.pdf")
						.contentType(MediaType.parseMediaType("application/pdf; charset=UTF-8"))
						.body(body);
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error generando PDF");
			}
		}

		return ResponseEntity.badRequest().body("Formato no soportado");
	}

	private FiltrosDto resetearFiltro() {
		FiltrosDto filtro = new FiltrosDto();
		filtro.setOrden(Optional.of("tituloGest"));
		filtro.setPack(false);
		return filtro;
	}

	private FiltrosDto recuperarFiltro(HttpSession session, FiltrosDto filtro) {
		FiltrosDto aux = filtro;
		if (session.getAttribute(FILTRO_STRING) != null) {
			int paginaSolicitada = 0;
			if (aux.getPagina().isPresent()) {
				paginaSolicitada = aux.getPagina().get();
			}
			aux = (FiltrosDto) session.getAttribute(FILTRO_STRING);
			if (aux.getPagina().isPresent() && aux.getPagina().get() != paginaSolicitada) {
				aux.setPagina(Optional.of(paginaSolicitada));
			}
		}
		return aux;
	}

	/*
	 * ------------------------------------------------- FORMULARIO DE CREACIÓN GET
	 * /peliculas/nuevo -------------------------------------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/nuevo")
	public String createForm(Principal principal, Model model) {

		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute(LOCATIONS_STRING, locations);

		List<Source> sources = sourceService.findAll();
		model.addAttribute(SOURCES_LIST, sources);

		model.addAttribute(PELICULA, new PeliculaCreateDto());

		return "peliculas/new"; // → templates/peliculas/form.html
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/petition/{id}")
	public String createPeticion(Principal principal, Model model, @PathVariable String id)  {

		List<Source> sources = sourceService.findAll();
		model.addAttribute(SOURCES_LIST, sources);
		PeliculaCreateDto dto = new PeliculaCreateDto();
		dto.setImdbCodigo(id);

		Optional<ImdbTitleBasics> itb = imdbTitleBasicsService.findById(id);
		Optional<ImdbTitleAkas> ita = imdbTitleAkasService.findByTitleId(id);
		if (ita.isPresent()) {
			dto.setTitulo(ita.get().getTitle());
			dto.setTituloGest(ita.get().getTitle());
		} else {
			if (itb.isPresent()) {
				dto.setTitulo(itb.get().getOriginaltitle());
			}
		}

		if (itb.isPresent() && itb.get().getStartyear() != null && !itb.get().getStartyear().isBlank()) {
			dto.setAnyo(Integer.parseInt(itb.get().getStartyear()));
			dto.setNotas(String.join(",", itb.get().getGenres()));
		}

		model.addAttribute(PELICULA, dto);

		return "peliculas/petition";
	}

	/*
	 * ------------------------------------------------- GUARDAR NUEVA PELÍCULA POST
	 * /peliculas -------------------------------------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public String create(Principal principal, @Valid @ModelAttribute(PELICULA) PeliculaCreateDto dto,
			BindingResult br, Model model, HttpSession session)
			throws IOException, URISyntaxException {

		if (br.hasErrors()) {
			List<Location> locations = locService.findAllOrderByDescripcion();
			model.addAttribute(LOCATIONS_STRING, locations);

			List<Source> sources = sourceService.findAll();
			model.addAttribute(SOURCES_LIST, sources);
			// Si hay errores de validación, volvemos al mismo formulario
			return "peliculas/new";
		}
		Pelicula creada = service.crear(dto);

		if (dto.getImdbCodigo() != null && !dto.getImdbCodigo().isBlank()) {
			insertImdbService.insert(creada.getId(), dto.getImdbCodigo());
		}

		// Redirigimos al detalle de la película recién creada
		return "redirect:/peliculas/" + creada.getId();
	}

	/*
	 * ------------------------------------------------- DETALLE DE UNA PELÍCULA
	 * (incluye su pack) GET /peliculas/{id}
	 * -------------------------------------------------
	 */
	@GetMapping("/{id}")
	public String detail(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			{

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException(PELICULA_NO_ENCONTRADA));

		model.addAttribute(PELICULA, p);

		FiltrosDto filtro = (FiltrosDto) session.getAttribute(FILTRO_STRING);
		model.addAttribute(FILTRO_STRING, filtro);

		List<Director> directores = directorService.findByPeliculaId(p.getId());
		model.addAttribute("directores", directores);

		List<Actor> actores = actorService.findByPeliculaIdOrderByOrdenAsc(p.getId());
		model.addAttribute("actores", actores);

		File file = new File(storageProperties.getNfsRoot(), +id + "_trailer.mkv");
		if (!file.exists()) {
			file = new File(storageProperties.getNfsRoot(), +id + "_trailer.mp4");
		}
		Path videoPath = Paths.get(file.getPath()).toAbsolutePath().normalize();

		// --- 3️⃣ ¿Existe el archivo? -----------------------------------------------
		boolean exists = Files.exists(videoPath) && Files.isRegularFile(videoPath);
		model.addAttribute("hasTrailer", exists);

		List<GroupFilms> grupos = groupFilmsService.findByPelicula(id);
		model.addAttribute("grupos", grupos);

		return "peliculas/detail";
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> image(Principal principal, @PathVariable Integer id) {

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException(PELICULA_NO_ENCONTRADA));

		if (p.getPeliculaFotos() != null && !p.getPeliculaFotos().isEmpty()) {
			PeliculaFoto pf = p.getPeliculaFotos().iterator().next();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			return new ResponseEntity<>(pf.getFoto(), headers, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(new byte[0], HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/trailer/{id}")
	public void trailer(Principal principal, @PathVariable Integer id, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException(PELICULA_NO_ENCONTRADA));

		File file = new File(storageProperties.getNfsRoot(), +p.getId() + "_trailer.mkv");
		if (!file.exists()) {
			file = new File(storageProperties.getNfsRoot(), +p.getId() + "_trailer.mp4");
		}
		Path videoPath = Paths.get(file.getPath()).toAbsolutePath().normalize();

		String rangeHeader = request.getHeader(HttpHeaders.RANGE);
		long fileLength = Files.size(videoPath);
		long start = 0;
		long end = fileLength - 1;

		if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
			// Ejemplo: bytes=1024-2047
			String[] ranges = rangeHeader.substring(6).split("-", 2);
			try {
				start = Long.parseLong(ranges[0].trim());
				if (ranges.length > 1 && !ranges[1].isEmpty()) {
					end = Long.parseLong(ranges[1].trim());
				}
			} catch (NumberFormatException ignored) {
				// Si el rango está mal formado, ignoramos y enviamos todo
				start = 0;
				end = fileLength - 1;
			}
		}

		long contentLength = end - start + 1;

		try (InputStream input = Files.newInputStream(videoPath);
				BufferedInputStream bis = new BufferedInputStream(input);
				OutputStream out = response.getOutputStream()) {

			// Salta hasta el byte de inicio
			bis.skip(start);

			byte[] buffer = new byte[8192];
			long bytesToRead = contentLength;
			int len;
			while (bytesToRead > 0 && (len = bis.read(buffer, 0, (int) Math.min(buffer.length, bytesToRead))) != -1) {
				out.write(buffer, 0, len);
				bytesToRead -= len;
			}
			out.flush();
		}

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/editar/{id}")
	public String edit(Principal principal, @PathVariable Integer id, HttpSession session, Model model) {

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException(PELICULA_NO_ENCONTRADA));
		model.addAttribute(PELICULA, p);

		List<Source> sources = sourceService.findAll();
		model.addAttribute(SOURCES_LIST, sources);

		Optional<LocationType> locationType = locationTypeService.findById(p.getFormato().getIdParaUbicaciones());
		List<Location> locations = locService.findAllOrderByDescripcion(locationType.get());
		model.addAttribute(LOCATIONS_STRING, locations);

		model.addAttribute("caratula", new NewCaratulaDTO());
		// DTO vacío para el formulario “añadir hijo al pack”
		model.addAttribute("nuevoHijo", new PeliculaChildDto());

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
		nuevo.setNotas(p.getNotas());
		nuevo.setImdbCodigo(p.getImdbId());
		nuevo.setEstadoCodigo(p.getEstado() != null ? p.getEstado().getId() : null);

		if(p.getPeliculasUsuario() != null) {
			p.getPeliculasUsuario().stream()
				.filter(pu -> pu.getUsuario().getLogin().equals(principal.getName()))
				.findFirst()
				.ifPresent(pu -> {
					nuevo.setVista(pu.isVista());
					nuevo.setLbRating(pu.getLbRating());
				});
		} else {
			nuevo.setVista(false);
		}

		if (p.getLocation() != null) {
			nuevo.setLocationCode(p.getLocation().getCodigo());
		} else {
			nuevo.setLocationCode("");
		}

		model.addAttribute("nuevo", nuevo);

		List<Director> directores = directorService.findByPeliculaId(p.getId());
		model.addAttribute("directores", directores);

		List<Actor> actores = actorService.findByPeliculaIdOrderByOrdenAsc(p.getId());
		model.addAttribute("actores", actores);

		FiltrosDto filtro = (FiltrosDto) session.getAttribute(FILTRO_STRING);
		model.addAttribute(FILTRO_STRING, filtro);
		return "peliculas/edit"; // → templates/peliculas/detail.html
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/actualizar/{id}")
	public String actualizar(Principal principal, HttpSession session, @PathVariable Integer id, RedirectAttributes ra,
			@Valid @ModelAttribute PeliculaCreateDto nuevo) {

		Optional<Pelicula> opt = service.findById(id);

		Pelicula existing = opt.get();

		if (existing == null) {
			throw new IllegalArgumentException(PELICULA_NO_ENCONTRADA);
		}

		Formato formato = Formato.getById(nuevo.getFormatoCodigo());
		Genero genero = Genero.getById(nuevo.getGeneroCodigo());

		Location loc = null;
		if (nuevo.getLocationCode() != null && !nuevo.getLocationCode().isBlank()) {
			loc = locService.findById(nuevo.getLocationCode())
					.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
		}
		Optional<Estado> estado = null;
		if(nuevo.getEstadoCodigo() != null) {
			estado = estadoService.findEstadoById(nuevo.getEstadoCodigo());
			if(estado.isPresent()) {
				existing.setEstado(estado.get());
			}
		}
		

		existing.setTitulo(nuevo.getTitulo());
		existing.setTituloGest(nuevo.getTituloGest());
		existing.setFormato(formato);
		existing.setLocation(loc);
		existing.setAnyo(nuevo.getAnyo());
		existing.setGenero(genero);
		existing.setPack(nuevo.isPack());
		existing.setNotas(nuevo.getNotas());
		existing.setSteelbook(nuevo.isSteelbook());
		existing.setFunda(nuevo.isFunda());
		existing.setComprado(nuevo.isComprado());
		existing.setImdbId(nuevo.getImdbCodigo());
		existing.calcularCodigo();
		service.save(existing);

		if (nuevo.getImdbCodigo() != null && !nuevo.getImdbCodigo().isBlank()) {
			insertImdbService.insert(existing.getId(), nuevo.getImdbCodigo());
		}

		existing.getPeliculasUsuario().forEach(pu -> {
			if (pu.getUsuario().getLogin().equals(principal.getName())) {
				pu.setVista(nuevo.isVista());
				pu.setLbRating(nuevo.getLbRating());
			}
		});
		service.save(existing);

		return "redirect:/peliculas?recuperar=true";
	}

	@PostMapping("/volver")
	public String volver(HttpSession session, RedirectAttributes ra) {

		return "redirect:/peliculas?recuperar=true";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/{id}/caratula")
	public ResponseEntity<String> addCaratula(Principal principal, @PathVariable Integer id,
			@Valid @ModelAttribute("caratula") NewCaratulaDTO dto) throws IOException, URISyntaxException {

		final DwFotoServiceI dwFotoService = new DwFotoService();
		Optional<Source> sourceObj = sourceService.findById(dto.getSource());
		PeliculaFoto pf = new PeliculaFoto();
		pf.setUrl(dto.getUrl());
		if(sourceObj.isPresent()) {
			pf.setSource(sourceObj.get());
		}
		pf.setFoto(dwFotoService.descargar(dto.getSource(), dto.getUrl()));
		pf.setCaratula(true);

		Optional<Pelicula> pelicula = service.findById(id);

		if (pelicula.isPresent()) {
			pelicula.get().getPeliculaFotos().clear();
			service.save(pelicula.get());
			pelicula.get().addCaratula(pf);
			service.save(pelicula.get());
			return ResponseEntity.ok("Descargado");
		}

		return new ResponseEntity<>("Id no encontrado", HttpStatus.NO_CONTENT);
	}

	/*
	 * ------------------------------------------------- AÑADIR UN HIJO AL PACK POST
	 * /peliculas/{padreId}/hijo -------------------------------------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/{padreId}/hijo")
	public String addChild(Principal principal, @PathVariable Integer padreId, HttpSession session,
			@Valid @ModelAttribute("nuevoHijo") PeliculaChildDto dto, BindingResult br, Model model) {


		if (br.hasErrors()) {
			// Si hay errores, volvemos al detalle mostrando los mensajes
			return detail(principal, padreId, session, model);
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/{id}/eliminar") // usando POST para evitar problemas con browsers
	public String delete(Principal principal, @PathVariable Integer id) {

		service.delete(id);
		return "redirect:/peliculas";
	}

}