package lugus.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.config.StorageProperties;
import lugus.dto.core.FiltrosDto;
import lugus.dto.films.PeliculaChildDto;
import lugus.dto.films.PeliculaCreateDto;
import lugus.dto.media.NewCaratulaDTO;
import lugus.exception.PermisoException;
import lugus.model.core.Source;
import lugus.model.core.Location;
import lugus.model.core.LocationType;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.model.groups.GroupFilms;
import lugus.model.imdb.ImdbTitleAkas;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.people.Actor;
import lugus.model.people.Director;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.service.core.SourceService;
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
import lugus.service.people.InsertPersonalDataService;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/peliculas")
@RequiredArgsConstructor
public class PeliculasController {

	private final PeliculaService service;

	private final LocationService locService;

	private final SourceService sourceService;

	private final DirectorService directorService;

	private final ActorService actorService;

	private final InsertPersonalDataService insertImdbService;

	private final LocationTypeService locationTypeService;

	private final StorageProperties storageProperties;

	private final ImdbTitleBasicsService imdbTitleBasicsService;

	private final ImdbTitleAkasService imdbTitleAkasService;

	private final GroupFilmsService groupFilmsService;
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
		model.addAttribute("filtro", filtro);
		session.setAttribute("filtro", filtro);

		// obtain the film by the filter
		Page<Pelicula> resultado = service.findAllBy(filtro);
		model.addAttribute("pagePeliculas", resultado);
		model.addAttribute("numResultado", "Resultados encontrados: " + resultado.getTotalElements());

		// select for filter
		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute("locations", locations);

		return "peliculas/list";
	}

	private FiltrosDto resetearFiltro() {
		FiltrosDto filtro = new FiltrosDto();
		filtro.setOrden(Optional.of("tituloGest"));
		filtro.setPack(false);
		return filtro;
	}

	private FiltrosDto recuperarFiltro(HttpSession session, FiltrosDto filtro) {
		FiltrosDto aux = filtro;
		if (session.getAttribute("filtro") != null) {
			int paginaSolicitada = 0;
			if (aux.getPagina().isPresent()) {
				paginaSolicitada = aux.getPagina().get();
			}
			aux = (FiltrosDto) session.getAttribute("filtro");
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
	public String createForm(Principal principal, Model model) throws PermisoException {

		List<Location> locations = locService.findAllOrderByDescripcion();
		model.addAttribute("locations", locations);

		List<Source> sources = sourceService.findAll();
		model.addAttribute("sourcesList", sources);

		model.addAttribute("pelicula", new PeliculaCreateDto());

		return "peliculas/new"; // → templates/peliculas/form.html
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/petition/{id}")
	public String createPeticion(Principal principal, Model model, @PathVariable String id) throws PermisoException {

		List<Source> sources = sourceService.findAll();
		model.addAttribute("sourcesList", sources);
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

		model.addAttribute("pelicula", dto);

		return "peliculas/petition";
	}

	/*
	 * ------------------------------------------------- GUARDAR NUEVA PELÍCULA POST
	 * /peliculas -------------------------------------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public String create(Principal principal, @Valid @ModelAttribute("pelicula") PeliculaCreateDto dto,
			BindingResult br, Model model, HttpSession session) throws PermisoException, IOException {

		if (br.hasErrors()) {
			List<Location> locations = locService.findAllOrderByDescripcion();
			model.addAttribute("locations", locations);

			List<Source> sources = sourceService.findAll();
			model.addAttribute("sourcesList", sources);
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
			throws PermisoException {

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

		model.addAttribute("pelicula", p);

		FiltrosDto filtro = (FiltrosDto) session.getAttribute("filtro");
		model.addAttribute("filtro", filtro);

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
	public ResponseEntity<byte[]> image(Principal principal, @PathVariable Integer id) throws PermisoException {

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

	@GetMapping("/trailer/{id}")
	public void trailer(Principal principal, @PathVariable Integer id, HttpServletRequest request,
			HttpServletResponse response) throws PermisoException, IOException {

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

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
	public String edit(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));
		model.addAttribute("pelicula", p);

		List<Source> sources = sourceService.findAll();
		model.addAttribute("sourcesList", sources);

		Optional<LocationType> locationType = locationTypeService.findById(p.getFormato().getIdParaUbicaciones());
		List<Location> locations = locService.findAllOrderByDescripcion(locationType.get());
		model.addAttribute("locations", locations);

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

		if (p.getOtros() != null) {
			nuevo.setVista(p.getOtros().getVista());
			nuevo.setLbRating(p.getOtros().getLbRating());
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

		FiltrosDto filtro = (FiltrosDto) session.getAttribute("filtro");
		model.addAttribute("filtro", filtro);
		return "peliculas/edit"; // → templates/peliculas/detail.html
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/actualizar/{id}")
	public String actualizar(Principal principal, HttpSession session, @PathVariable Integer id, RedirectAttributes ra,
			@Valid @ModelAttribute PeliculaCreateDto nuevo) throws PermisoException {

		Optional<Pelicula> opt = service.findById(id);

		Pelicula existing = opt.get();

		if (existing == null) {
			new IllegalArgumentException("Pelicula no encontrada");
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
		existing.setAnyo(nuevo.getAnyo());
		existing.setGenero(genero);
		existing.setPack(nuevo.isPack());
		existing.setNotas(nuevo.getNotas());
		existing.setSteelbook(nuevo.isSteelbook());
		existing.setFunda(nuevo.isFunda());
		existing.setComprado(nuevo.isComprado());
		existing.calcularCodigo();
		service.save(existing);

		if (nuevo.getImdbCodigo() != null && !nuevo.getImdbCodigo().isBlank()) {
			insertImdbService.insert(existing.getId(), nuevo.getImdbCodigo());
		}

		existing.getOtros().setLbRating(nuevo.getLbRating());
		existing.getOtros().setVista(nuevo.isVista());
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
			@Valid @ModelAttribute("caratula") NewCaratulaDTO dto) throws IOException, PermisoException {

		final DwFotoServiceI dwFotoService = new DwFotoService();
		Optional<Source> sourceObj = sourceService.findById(dto.getSource());
		PeliculaFoto pf = new PeliculaFoto();
		pf.setUrl(dto.getUrl());
		pf.setSource(sourceObj.get());
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

		return new ResponseEntity<String>("Id no encontrado", HttpStatus.NO_CONTENT);
	}

	/*
	 * ------------------------------------------------- AÑADIR UN HIJO AL PACK POST
	 * /peliculas/{padreId}/hijo -------------------------------------------------
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/{padreId}/hijo")
	public String addChild(Principal principal, @PathVariable Integer padreId, HttpSession session,
			@Valid @ModelAttribute("nuevoHijo") PeliculaChildDto dto, BindingResult br, Model model)
			throws PermisoException, IOException {


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
	public String delete(Principal principal, @PathVariable Integer id) throws PermisoException {

		service.delete(id);
		return "redirect:/peliculas";
	}

}