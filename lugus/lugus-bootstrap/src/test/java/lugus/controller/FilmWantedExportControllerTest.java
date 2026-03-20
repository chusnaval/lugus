package lugus.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import lugus.config.StorageProperties;
import lugus.export.FilmWantedExportService;
import lugus.model.films.FilmWanted;
import lugus.service.core.EstadoService;
import lugus.service.core.LocationService;
import lugus.service.core.LocationTypeService;
import lugus.service.core.SourceService;
import lugus.service.films.PeliculaService;
import lugus.service.groups.GroupFilmsService;
import lugus.service.imdb.ImdbTitleAkasService;
import lugus.service.imdb.ImdbTitleBasicsService;
import lugus.service.people.ActorService;
import lugus.service.people.DirectorService;
import lugus.service.people.FilmWantedService;
import lugus.service.people.InsertPersonalDataService;
import lugus.service.user.FavoritosUsuarioService;
import lugus.service.user.UsuarioService;

class FilmWantedExportControllerTest {

	private static final String FORMAT = "format";
	private MockMvc mockMvc;
	private FilmWantedService filmWantedService;

	@BeforeEach
	void setUp() {
		PeliculaService peliculaService = Mockito.mock(PeliculaService.class);
		LocationService locationService = Mockito.mock(LocationService.class);
		SourceService sourceService = Mockito.mock(SourceService.class);
		filmWantedService = Mockito.mock(FilmWantedService.class);
		FilmWantedExportService exportService = new FilmWantedExportService();
		DirectorService directorService = Mockito.mock(DirectorService.class);
		ActorService actorService = Mockito.mock(ActorService.class);
		InsertPersonalDataService insertPersonalDataService = Mockito.mock(InsertPersonalDataService.class);
		LocationTypeService locationTypeService = Mockito.mock(LocationTypeService.class);
		StorageProperties storageProperties = Mockito.mock(StorageProperties.class);
		ImdbTitleBasicsService imdbTitleBasicsService = Mockito.mock(ImdbTitleBasicsService.class);
		ImdbTitleAkasService imdbTitleAkasService = Mockito.mock(ImdbTitleAkasService.class);
		GroupFilmsService groupFilmsService = Mockito.mock(GroupFilmsService.class);
		FavoritosUsuarioService favoritosUsuarioService = Mockito.mock(FavoritosUsuarioService.class);
		UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
		EstadoService estadoService = Mockito.mock(EstadoService.class);

		PeliculasController controller = new PeliculasController(
				peliculaService,
				locationService,
				estadoService,
				sourceService,
				filmWantedService,
				exportService,
				directorService,
				actorService,
				insertPersonalDataService,
				locationTypeService,
				storageProperties,
				imdbTitleBasicsService,
				imdbTitleAkasService,
				groupFilmsService,
				favoritosUsuarioService,
				usuarioService);

		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void shouldExportMarkdown() throws Exception {
		mockWanted();

		mockMvc.perform(get("/peliculas/wanted/export").param(FORMAT, "md"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=peliculas_buscadas.md"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("# Películas buscadas")));
	}

	@Test
	void shouldExportOds() throws Exception {
		mockWanted();

		mockMvc.perform(get("/peliculas/wanted/export").param(FORMAT, "ods"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=peliculas_buscadas.ods"));
	}

	@Test
	void shouldExportPdf() throws Exception {
		mockWanted();

		mockMvc.perform(get("/peliculas/wanted/export").param(FORMAT, "pdf"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=peliculas_buscadas.pdf"));
	}

	@Test
	void shouldReturnBadRequestForUnknownFormat() throws Exception {
		mockWanted();

		mockMvc.perform(get("/peliculas/wanted/export").param(FORMAT, "csv"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Formato no soportado"));
	}

	private void mockWanted() {
		when(filmWantedService.findAllOrdered()).thenReturn(List.of(
				FilmWanted.builder().titulo("The Matrix").anyo(1999).formato("DVD").build(),
				FilmWanted.builder().titulo("Arrival").anyo(2016).formato("BluRay").build()));
	}
}
