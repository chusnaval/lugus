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

import lugus.export.SeriesExportService;
import lugus.export.SeriesWantedExportService;
import lugus.model.series.SerWanted;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.series.SerWantedService;
import lugus.service.series.SeriesService;

class SeriesWantedExportControllerTest {

	private static final String FORMAT = "format";
    private MockMvc mockMvc;
	private SerWantedService serWantedService;

	@BeforeEach
	void setUp() {
		SeriesService seriesService = Mockito.mock(SeriesService.class);
		LocationService locationService = Mockito.mock(LocationService.class);
		SourceService sourceService = Mockito.mock(SourceService.class);
		serWantedService = Mockito.mock(SerWantedService.class);
		SeriesExportService exportService = Mockito.mock(SeriesExportService.class);
		SeriesWantedExportService exportWantedService = new SeriesWantedExportService();

		SeriesController controller = new SeriesController(seriesService, locationService, sourceService, serWantedService,
				exportService, exportWantedService);

		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void shouldExportMarkdown() throws Exception {
		mockWanted();

		mockMvc.perform(get("/series/wanted/export").param(FORMAT, "md"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=series_buscadas.md"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("# Series buscadas")));
	}

	@Test
	void shouldExportOds() throws Exception {
		mockWanted();

		mockMvc.perform(get("/series/wanted/export").param(FORMAT, "ods"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=series_buscadas.ods"));
	}

	@Test
	void shouldExportPdf() throws Exception {
		mockWanted();

		mockMvc.perform(get("/series/wanted/export").param(FORMAT, "pdf"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "attachment; filename=series_buscadas.pdf"));
	}

	@Test
	void shouldReturnBadRequestForUnknownFormat() throws Exception {
		mockWanted();

		mockMvc.perform(get("/series/wanted/export").param(FORMAT, "csv"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Formato no soportado"));
	}

	private void mockWanted() {
		when(serWantedService.findAllOrdered()).thenReturn(List.of(
				SerWanted.builder().titulo("The Wire").anyoInicio(2002).seasonDesc("T1").build(),
				SerWanted.builder().titulo("Lost").anyoInicio(2004).seasonDesc("T2").build()));
	}
}