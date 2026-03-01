package lugus.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lugus.model.films.FilmWanted;

class FilmWantedExportServiceTest {

	private FilmWantedExportService service;

	@BeforeEach
	void setUp() {
		service = new FilmWantedExportService();
	}

	@Test
	@DisplayName("Genera Markdown con cabecera y filas")
	void shouldGenerateMarkdown() {
		List<FilmWanted> list = sample();

		String md = service.toMarkdown(list);

		assertThat(md).contains("# Películas buscadas");
		assertThat(md).contains("| Título | Año | Formato |");
		assertThat(md).contains("| The Matrix | 1999 | DVD |");
		assertThat(md).contains("| Arrival | 2016 | BluRay |");
	}

	@Test
	@DisplayName("Genera ODS válido con content.xml y manifest")
	void shouldGenerateOds() throws Exception {
		List<FilmWanted> list = sample();

		byte[] ods = service.toOds(list);

		assertThat(ods).isNotNull();
		assertThat(ods.length).isGreaterThan(100);

		String contentXml = extractZipEntry(ods, "content.xml");
		String manifestXml = extractZipEntry(ods, "META-INF/manifest.xml");

		assertThat(contentXml).contains("Películas buscadas");
		assertThat(contentXml).contains("The Matrix");
		assertThat(contentXml).contains("DVD");
		assertThat(manifestXml).contains("application/vnd.oasis.opendocument.spreadsheet");
	}

	@Test
	@DisplayName("Genera PDF válido")
	void shouldGeneratePdf() throws IOException {
		List<FilmWanted> list = sample();

		byte[] pdf = service.toPdf(list);

		assertThat(pdf).isNotNull();
		assertThat(pdf.length).isGreaterThan(200);
		String header = new String(pdf, 0, 4, StandardCharsets.US_ASCII);
		assertThat(header).isEqualTo("%PDF");
	}

	private List<FilmWanted> sample() {
		return List.of(
				FilmWanted.builder().titulo("The Matrix").anyo(1999).formato("DVD").build(),
				FilmWanted.builder().titulo("Arrival").anyo(2016).formato("BluRay").build());
	}

	private String extractZipEntry(byte[] zip, String entryName) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip), StandardCharsets.UTF_8)) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entryName.equals(entry.getName())) {
					return new String(zis.readAllBytes(), StandardCharsets.UTF_8);
				}
			}
		}
		return "";
	}
}
