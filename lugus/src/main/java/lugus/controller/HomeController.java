package lugus.controller;

import lombok.RequiredArgsConstructor;
import lugus.service.PeliculaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PeliculaService peliculaService;

    @GetMapping("/")
    public String home(Model model) {
        // opcional: pasar una estadística al índice
        long total = peliculaService.contarTodas();  
        model.addAttribute("totalPeliculas", total);
        return "index";   // → src/main/resources/templates/index.html
    }
}