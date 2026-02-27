package lugus.api.user;

import lugus.dto.user.FavoritosUsuarioDto;
import lugus.mapper.user.FavoritosUsuarioMapper;
import lugus.model.films.Pelicula;
import lugus.model.user.FavoritosUsuario;
import lugus.model.user.Usuario;
import lugus.model.series.Serie;
import lugus.service.films.PeliculaService;
import lugus.service.series.SeriesService;
import lugus.service.user.FavoritosUsuarioService;
import lugus.service.user.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/favoritos")
@RequiredArgsConstructor
public class FavoritosUsuarioApiController {
    private final FavoritosUsuarioService favoritosService;
    private final PeliculaService peliculaService;
    private final UsuarioService usuarioService;
    private final SeriesService seriesService;
    private final FavoritosUsuarioMapper mapper;

    @PostMapping("/pelicula/{peliculaId}")
    public ResponseEntity<FavoritosUsuarioDto> addFavoritoPelicula(@PathVariable Integer peliculaId, Principal principal) {
        String login = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByLogin(login);
        Optional<Pelicula> peliculaOpt = peliculaService.findById(peliculaId);
        if (usuarioOpt.isEmpty() || peliculaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = usuarioOpt.get();
        Pelicula pelicula = peliculaOpt.get();
        FavoritosUsuario existente = favoritosService.findByUsuarioAndPelicula(usuario, pelicula);
        if (existente != null) {
            return ResponseEntity.ok(mapper.toDto(existente));
        }
        FavoritosUsuario favorito = FavoritosUsuario.builder()
                .usuario(usuario)
                .pelicula(pelicula)
                .fechaAgregado(java.time.Instant.now())
                .build();
        FavoritosUsuario saved = favoritosService.save(favorito);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @DeleteMapping("/pelicula/{peliculaId}")
    public ResponseEntity<Void> removeFavoritoPelicula(@PathVariable Integer peliculaId, Principal principal) {
        String login = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByLogin(login);
        Optional<Pelicula> peliculaOpt = peliculaService.findById(peliculaId);
        if (usuarioOpt.isEmpty() || peliculaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = usuarioOpt.get();
        Pelicula pelicula = peliculaOpt.get();
        FavoritosUsuario favorito = favoritosService.findByUsuarioAndPelicula(usuario, pelicula);
        if (favorito != null) {
            favoritosService.delete(favorito);
        }
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints para series ---
    @PostMapping("/serie/{serieId}")
    public ResponseEntity<FavoritosUsuarioDto> addFavoritoSerie(@PathVariable Integer serieId, Principal principal) {
        String login = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByLogin(login);
        Optional<Serie> serieOpt = seriesService.findById(serieId);
        if (usuarioOpt.isEmpty() || serieOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = usuarioOpt.get();
        Serie serie = serieOpt.get();
        FavoritosUsuario existente = favoritosService.findByUsuarioAndSerie(usuario, serie);
        if (existente != null) {
            return ResponseEntity.ok(mapper.toDto(existente));
        }
        FavoritosUsuario favorito = FavoritosUsuario.builder()
                .usuario(usuario)
                .serie(serie)
                .fechaAgregado(java.time.Instant.now())
                .build();
        FavoritosUsuario saved = favoritosService.save(favorito);
        return ResponseEntity.ok(mapper.toDto(saved));
    }

    @DeleteMapping("/serie/{serieId}")
    public ResponseEntity<Void> removeFavoritoSerie(@PathVariable Integer serieId, Principal principal) {
        String login = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioService.findByLogin(login);
        Optional<Serie> serieOpt = seriesService.findById(serieId);
        if (usuarioOpt.isEmpty() || serieOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = usuarioOpt.get();
        Serie serie = serieOpt.get();
        FavoritosUsuario favorito = favoritosService.findByUsuarioAndSerie(usuario, serie);
        if (favorito != null) {
            favoritosService.delete(favorito);
        }
        return ResponseEntity.noContent().build();
    }
}
