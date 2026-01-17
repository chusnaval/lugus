package lugus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import lugus.model.films.Pelicula;
import lugus.model.values.Formato;
import lugus.model.values.Genero;
import lugus.repository.films.PeliculaRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest   // usa H2 en memoria y configura solo JPA
class PeliculaRepositoryTest {

    @Autowired PeliculaRepository repo;

    @Test
    @DisplayName("Un hijo debe aparecer en la colección 'peliculasPack' del padre")
    void parent_child_relationship_is_persisted() {
        // --- Arrange ---------------------------------------------------------
        Pelicula padre = new Pelicula();
        padre.setTitulo("Colección Marvel");
        padre.setAnyo(2020);
        padre.setFormato(Formato.BLURAY);
        padre.setGenero(Genero.AVENTURA);
        padre.calcularCodigo();
        repo.save(padre);

        Pelicula child = new Pelicula();
        child.setTitulo("Iron Man");
        child.setAnyo(2008);
        child.setFormato(Formato.BLURAY);
        child.setGenero(Genero.AVENTURA);
        child.calcularCodigo();
        repo.save(child);
        
        padre.addHijo(child); 
        repo.save(padre);

        // --- Act -------------------------------------------------------------
        Pelicula fetchedParent = repo.findById(padre.getId()).orElseThrow();
        // Hibernate carga la colección lazy; forzamos fetch
        fetchedParent.getPeliculasPack().size(); // init

        // --- Assert ----------------------------------------------------------
        assertThat(fetchedParent.getPeliculasPack())
                .hasSize(1)
                .extracting(Pelicula::getTitulo)
                .containsExactly("Iron Man");

        // Además, el hijo debe tener su referencia al padre
        Pelicula fetchedChild = repo.findById(child.getId()).orElseThrow();
        assertThat(fetchedChild.getPadre()).isEqualTo(padre);
    }
}