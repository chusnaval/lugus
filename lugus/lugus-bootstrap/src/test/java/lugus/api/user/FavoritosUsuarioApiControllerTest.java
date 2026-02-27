package lugus.api.user;

import lugus.dto.user.FavoritosUsuarioDto;
import lugus.mapper.user.FavoritosUsuarioMapper;
import lugus.service.films.PeliculaService;
import lugus.service.series.SeriesService;
import lugus.service.user.FavoritosUsuarioService;
import lugus.service.user.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FavoritosUsuarioApiControllerTest {

    @Mock
    private FavoritosUsuarioService favoritosService;
    @Mock
    private PeliculaService peliculaService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private FavoritosUsuarioMapper mapper;
    @Mock
    private SeriesService seriesService;
    @Mock
    private Principal principal;

    @InjectMocks
    private FavoritosUsuarioApiController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(principal.getName()).thenReturn("testuser");
    }

    @Test
    void addFavoritoPelicula_usuarioNoExiste() {
        when(usuarioService.findByLogin(anyString())).thenReturn(null);
        ResponseEntity<FavoritosUsuarioDto> response = controller.addFavoritoPelicula(1, principal);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void removeFavoritoPelicula_usuarioNoExiste() {
        when(usuarioService.findByLogin(anyString())).thenReturn(null);
        ResponseEntity<Void> response = controller.removeFavoritoPelicula(1, principal);
        assertEquals(404, response.getStatusCode().value());
    }

    // Puedes agregar más tests para los casos de éxito y para series
}