package lugus;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import lugus.dto.PeliculaCreateDto;
import lugus.model.Formato;
import lugus.model.Genero;
import lugus.model.Pelicula;
import lugus.repository.PeliculaRepository;
import lugus.service.LocalizacionService;
import lugus.service.PeliculaService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PeliculaServiceTest {

    private PeliculaRepository repo;
	private LocalizacionService locService;
    private PeliculaService service;

    @BeforeEach
    void setUp() {
        repo = mock(PeliculaRepository.class);
        service = new PeliculaService(repo, locService);
    }

    @Test
    void save_shouldLinkParentAndChild_whenParentIsSet() {
        // --- Arrange ---------------------------------------------------------
        Pelicula padre = new Pelicula();
        padre.setId(1);
        padre.setTitulo("Trilogía");
        // Simulamos que el padre ya está persistido
        when(repo.save(any(Pelicula.class))).thenAnswer(i -> i.getArgument(0));

        Pelicula child = new Pelicula();
        child.setTitulo("Parte 1");
        child.setPadre(padre);
        child.setFormato(Formato.BLURAY);
        child.setGenero(Genero.ACCION);
        child.setAnyo(1990);
        child.calcularCodigo();
        padre.addHijo(child);
        
        // --- Act -------------------------------------------------------------
        Pelicula saved = service.save(child);

        // --- Assert ----------------------------------------------------------
        assertThat(saved.getPadre()).isSameAs(padre);
        assertThat(padre.getPeliculasPack()).containsExactlyInAnyOrder(saved);

        // Verificamos que el repositorio haya sido llamado una sola vez
        verify(repo, times(1)).save(child);
    }

    @Test
    void addChild_shouldPersistBothSides() {
        // --- Arrange ---------------------------------------------------------
        Pelicula padre = new Pelicula();
        padre.setId(10);
        padre.setTitulo("Colección");
        padre.setFormato(Formato.BLURAY);
        padre.setGenero(Genero.ACCION);
        padre.setAnyo(1970);
        
        PeliculaCreateDto child = new PeliculaCreateDto();
        child.setTitulo("Capítulo 2");
        child.setFormatoCodigo(Formato.BLURAY.getId());
        child.setGeneroCodigo(Genero.ACCION.getCodigo());
        child.setAnyo(1970);

        when(repo.findById(10)).thenReturn(Optional.of(padre));
        when(repo.save(any(Pelicula.class))).thenAnswer(i -> i.getArgument(0));

        // --- Act -------------------------------------------------------------
        Pelicula result = service.addChild(10, child);

        // --- Assert ----------------------------------------------------------
        assertThat(result.getPadre()).isSameAs(padre);
        assertThat(padre.getPeliculasPack()).containsExactlyInAnyOrder(result);

        // Capturamos el argumento pasado a save para inspeccionar la entidad
        ArgumentCaptor<Pelicula> captor = ArgumentCaptor.forClass(Pelicula.class);
        verify(repo, times(2)).save(captor.capture());
        Pelicula saved = captor.getValue();
        assertThat(saved).isSameAs(padre); // guardamos el padre (cascada guarda al hijo)
    }
}