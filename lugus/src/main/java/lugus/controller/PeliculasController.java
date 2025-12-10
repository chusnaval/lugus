package lugus.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.PermisoException;
import lugus.config.StorageProperties;
import lugus.dto.FiltrosDto;
import lugus.dto.NewCaratulaDTO;
import lugus.dto.PeliculaChildDto;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Actor;
import lugus.model.Director;
import lugus.model.Formato;
import lugus.model.Fuente;
import lugus.model.Genero;
import lugus.model.ImdbTitleAkas;
import lugus.model.ImdbTitleBasics;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.model.PeliculaFoto;
import lugus.model.TiposUbicacion;
import lugus.model.Usuario;
import lugus.service.ActorService;
import lugus.service.DirectorService;
import lugus.service.DwFotoService;
import lugus.service.DwFotoServiceI;
import lugus.service.FuenteService;
import lugus.service.ImdbTitleAkasService;
import lugus.service.ImdbTitleBasicsService;
import lugus.service.InsertPersonalDataService;
import lugus.service.LocalizacionService;
import lugus.service.PeliculaService;
import lugus.service.TiposUbicacionService;
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

	private final LocalizacionService locService;

	private final FuenteService fuenteService;

	private final DirectorService directorService;

	private final ActorService actorService;

	private final UsuarioService usuarioService;

	private final InsertPersonalDataService insertImdbService;

	private final TiposUbicacionService tiposUbicacionService;

	private final StorageProperties storageProperties;

	private final ImdbTitleBasicsService imdbTitleBasicsService;

	private final ImdbTitleAkasService imdbTitleAkasService;
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
		List<Localizacion> localizaciones = locService.findAllOrderByDescripcion();
		model.addAttribute("localizaciones", localizaciones);

		// admin rigth
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());

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

		model.addAttribute("pelicula", new PeliculaCreateDto());

		model.addAttribute("admin", usuario.isAdmin());

		return "peliculas/new"; // → templates/peliculas/form.html
	}

	@GetMapping("/petition/{id}")
	public String createPeticion(Principal principal, Model model, @PathVariable String id) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		List<Fuente> fuentes = fuenteService.findAll();
		model.addAttribute("fuentesList", fuentes);
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

		model.addAttribute("admin", usuario.isAdmin());

		return "peliculas/petition"; 
	}

	/*
	 * ------------------------------------------------- GUARDAR NUEVA PELÍCULA POST
	 * /peliculas -------------------------------------------------
	 */
	@PostMapping
	public String create(Principal principal, @Valid @ModelAttribute("pelicula") PeliculaCreateDto dto,
			BindingResult br, Model model, HttpSession session) throws PermisoException, IOException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		if (br.hasErrors()) {
			List<Localizacion> localizaciones = locService.findAllOrderByDescripcion();
			model.addAttribute("localizaciones", localizaciones);

			List<Fuente> fuentes = fuenteService.findAll();
			model.addAttribute("fuentesList", fuentes);
			// Si hay errores de validación, volvemos al mismo formulario
			return "peliculas/new";
		}
		Pelicula creada = service.crear(dto, session);

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

		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		model.addAttribute("admin", usuario.isAdmin());

		File file = new File(storageProperties.getNfsRoot(), +id + "_trailer.mkv");
		if (!file.exists()) {
			file = new File(storageProperties.getNfsRoot(), +id + "_trailer.mp4");
		}
		Path videoPath = Paths.get(file.getPath()).toAbsolutePath().normalize();

		// --- 3️⃣ ¿Existe el archivo? -----------------------------------------------
		boolean exists = Files.exists(videoPath) && Files.isRegularFile(videoPath);
		model.addAttribute("hasTrailer", exists);

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

	@GetMapping("/editar/{id}")
	public String edit(Principal principal, @PathVariable Integer id, HttpSession session, Model model)
			throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		Pelicula p = service.findById(id).orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));
		model.addAttribute("pelicula", p);

		List<Fuente> fuentes = fuenteService.findAll();
		model.addAttribute("fuentesList", fuentes);

		Optional<TiposUbicacion> tipoUbicacion = tiposUbicacionService.findById(p.getFormato().getIdParaUbicaciones());
		List<Localizacion> localizaciones = locService.findAllOrderByDescripcion(tipoUbicacion.get());
		model.addAttribute("localizaciones", localizaciones);

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

		if (p.getLocalizacion() != null) {
			nuevo.setLocalizacionCodigo(p.getLocalizacion().getCodigo());
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

	@PostMapping("/actualizar/{id}")
	public String actualizar(Principal principal, HttpSession session, @PathVariable Integer id, RedirectAttributes ra,
			@Valid @ModelAttribute PeliculaCreateDto nuevo) throws PermisoException {
		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
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
		existing.setComprado(nuevo.isComprado());
		existing.calcularCodigo();
		service.save(existing);

		if (nuevo.getImdbCodigo() != null && !nuevo.getImdbCodigo().isBlank()) {
			insertImdbService.insert(existing.getId(), nuevo.getImdbCodigo());
		}

		return "redirect:/peliculas?recuperar=true";
	}

	@PostMapping("/volver")
	public String volver(HttpSession session, RedirectAttributes ra) {

		return "redirect:/peliculas?recuperar=true";
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
		PeliculaFoto pf = new PeliculaFoto();
		pf.setUrl(dto.getUrl());
		pf.setFuente(fuenteObj.get());
		pf.setFoto(dwFotoService.descargar(dto.getFuente(), dto.getUrl()));
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
	@PostMapping("/{padreId}/hijo")
	public String addChild(Principal principal, @PathVariable Integer padreId, HttpSession session,
			@Valid @ModelAttribute("nuevoHijo") PeliculaChildDto dto, BindingResult br, Model model)
			throws PermisoException, IOException {

		Usuario usuario = usuarioService.findByLogin(principal.getName()).get();
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		if (br.hasErrors()) {
			// Si hay errores, volvemos al detalle mostrando los mensajes
			return detail(principal, padreId, session, model);
		}
		service.addChild(padreId, dto, session);
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
		if (!usuario.isAdmin()) {
			throw new PermisoException("No tiene permisos");
		}

		service.delete(id);
		return "redirect:/peliculas";
	}

}