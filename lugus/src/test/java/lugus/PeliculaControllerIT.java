package lugus;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest   // levanta todo el contexto (incluye H2 en memoria)
@AutoConfigureMockMvc
class PeliculaControllerIT {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired PeliculaRepository repo;

    @BeforeEach
    void clean() {
        repo.deleteAll(); // garantizamos aislamiento entre tests
    }

    @Test
    void create_and_fetch_pelicula() throws Exception {
        // --- Arrange ---------------------------------------------------------
        Pelicula dto = new Pelicula();
        dto.setTitulo("Matrix");
        dto.setAnyo(1999);
        // Otros campos pueden quedar null para este test rápido

        // --- Act -------------------------------------------------------------
        // POST /api/peliculas
        mvc.perform(post("/api/peliculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Matrix"));

        // --- Assert ----------------------------------------------------------
        // GET /api/peliculas
        mvc.perform(get("/api/peliculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo").value("Matrix"));
    }

    @Test
    void add_child_to_parent_pack() throws Exception {
        // Creamos primero el padre
        Pelicula padre = new Pelicula();
        padre.setTitulo("Saga Star Wars");
        padre.setAnyo(1977);
        padre = repo.save(padre);

        // Payload del hijo
        Pelicula hijo = new Pelicula();
        hijo.setTitulo("Una nueva esperanza");
        hijo.setAnyo(1977);

        // POST /api/peliculas/{padreId}/children
        mvc.perform(put("/api/peliculas/{padreId}/children", padre.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(hijo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.padre.id").value(padre.getId()));

        // Verificamos que la relación quedó guardada
        mvc.perform(get("/api/peliculas/{id}", padre.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peliculasPack", hasSize(1)))
                .andExpect(jsonPath("$.peliculasPack[0].titulo").value("Una nueva esperanza"));
    }
}