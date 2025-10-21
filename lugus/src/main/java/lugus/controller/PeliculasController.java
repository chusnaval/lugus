package lugus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Pelicula;
import lugus.service.PeliculaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/peliculas")
@RequiredArgsConstructor
public class PeliculasController {

    private final PeliculaService service;

    /* -------------------------------------------------
       LISTADO DE PELÍCULAS
       GET /peliculas
       ------------------------------------------------- */
    @GetMapping
    public String list(Model model) {
        List<Pelicula> todas = service.findAll();
        model.addAttribute("peliculas", todas);
        return "peliculas/list";               // → src/main/resources/templates/peliculas/list.html
    }

    /* -------------------------------------------------
       FORMULARIO DE CREACIÓN
       GET /peliculas/nuevo
       ------------------------------------------------- */
    @GetMapping("/nuevo")
    public String createForm(Model model) {
        model.addAttribute("pelicula", new PeliculaCreateDto());
        return "peliculas/form";               // → templates/peliculas/form.html
    }

    /* -------------------------------------------------
       GUARDAR NUEVA PELÍCULA
       POST /peliculas
       ------------------------------------------------- */
    @PostMapping
    public String create(@Valid @ModelAttribute("pelicula") PeliculaCreateDto dto,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            // Si hay errores de validación, volvemos al mismo formulario
            return "peliculas/form";
        }
        Pelicula creada = service.crear(dto);
        // Redirigimos al detalle de la película recién creada
        return "redirect:/peliculas/" + creada.getId();
    }

    /* -------------------------------------------------
       DETALLE DE UNA PELÍCULA (incluye su pack)
       GET /peliculas/{id}
       ------------------------------------------------- */
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Pelicula p = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));
        model.addAttribute("pelicula", p);
        // DTO vacío para el formulario “añadir hijo al pack”
        model.addAttribute("nuevoHijo", new PeliculaCreateDto());
        return "peliculas/detail";             // → templates/peliculas/detail.html
    }

    /* -------------------------------------------------
       AÑADIR UN HIJO AL PACK
       POST /peliculas/{padreId}/hijo
       ------------------------------------------------- */
    @PostMapping("/{padreId}/hijo")
    public String addChild(@PathVariable Integer padreId,
                           @Valid @ModelAttribute("nuevoHijo") PeliculaCreateDto dto,
                           BindingResult br,
                           Model model) {
        if (br.hasErrors()) {
            // Si hay errores, volvemos al detalle mostrando los mensajes
            return detail(padreId, model);
        }
        service.addChild(padreId, dto);
        // Después de añadir el hijo, recargamos el detalle del padre
        return "redirect:/peliculas/" + padreId;
    }

    /* -------------------------------------------------
       ELIMINAR UNA PELÍCULA (opcional)
       DELETE /peliculas/{id}
       ------------------------------------------------- */
    @PostMapping("/{id}/eliminar")   // usando POST para evitar problemas con browsers
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/peliculas";
    }
}