package lugus;

import com.fasterxml.jackson.databind.ObjectMapper;

import lugus.dto.PeliculaCreateDto;
import lugus.model.Formato;
import lugus.model.Genero;
import lugus.model.Pelicula;
import lugus.repository.PeliculaRepository;

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

@SpringBootTest // levanta todo el contexto (incluye H2 en memoria)
@AutoConfigureMockMvc
class PeliculaControllerIT {

	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	PeliculaRepository repo;

	@BeforeEach
	void clean() {
		repo.deleteAll(); // garantizamos aislamiento entre tests

	}

	@Test
	void create_and_fetch_pelicula() throws Exception {
		// --- Arrange ---------------------------------------------------------
		PeliculaCreateDto dto = new PeliculaCreateDto();
		dto.setTitulo("Matrix");
		dto.setAnyo(1999);
		dto.setGeneroCodigo(Genero.CIENCIA_FICCION.getCodigo());
		dto.setFormatoCodigo(Formato.BLURAY.getId());
		
		
		// --- Act -------------------------------------------------------------
		// POST /api/peliculas
		mvc.perform(
				post("/api/peliculas").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.titulo").value("Matrix"));

		// --- Assert ----------------------------------------------------------
		// GET /api/peliculas
		mvc.perform(get("/api/peliculas")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].titulo").value("Matrix"));
	}

	@Test
	void add_child_to_parent_pack() throws Exception {
		// Creamos primero el padre
		Pelicula padre = new Pelicula();
		padre.setId(1);
		padre.setTitulo("Saga Star Wars");
		padre.setFormato(Formato.BLURAY);
		padre.setGenero(Genero.AVENTURA);
		padre.calcularCodigo();
		repo.save(padre);

		// Payload del hijo
		PeliculaCreateDto hijo = new PeliculaCreateDto();
		hijo.setTitulo("Una nueva esperanza");
		hijo.setFormatoCodigo(Formato.BLURAY.getId());
		hijo.setGeneroCodigo(Genero.AVENTURA.getCodigo());
		hijo.setAnyo(1977);
		
		
		// POST /api/peliculas/{padreId}/children
		mvc.perform(put("/api/peliculas/{padreId}/children", padre.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(hijo))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.padre.id").value(padre.getId()));

		// Verificamos que la relación quedó guardada
		mvc.perform(get("/api/peliculas/{id}", padre.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.peliculasPack", hasSize(1)))
				.andExpect(jsonPath("$.peliculasPack[0].titulo").value("Una nueva esperanza"));
	}
}