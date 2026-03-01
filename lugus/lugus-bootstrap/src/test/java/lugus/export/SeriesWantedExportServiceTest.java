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

import lugus.model.series.SerWanted;

class SeriesWantedExportServiceTest {

	private SeriesWantedExportService service;

	@BeforeEach
	void setUp() {
		service = new SeriesWantedExportService();
	}

	@Test
	@DisplayName("Genera Markdown con cabecera y filas")
	void shouldGenerateMarkdown() {
		List<SerWanted> list = sample();

		String md = service.toMarkdown(list);

		assertThat(md).contains("# Series buscadas");
		assertThat(md).contains("| Título | Año inicio | Temporada |");
		assertThat(md).contains("| The Wire | 2002 | T1 - Piloto |");
		assertThat(md).contains("| Lost | 2004 | T2 |");
	}

	@Test
	@DisplayName("Genera ODS válido con content.xml y manifest")
	void shouldGenerateOds() throws Exception {
		List<SerWanted> list = sample();

		byte[] ods = service.toOds(list);

		assertThat(ods).isNotNull();
		assertThat(ods.length).isGreaterThan(100);

		String contentXml = extractZipEntry(ods, "content.xml");
		String manifestXml = extractZipEntry(ods, "META-INF/manifest.xml");

		assertThat(contentXml).contains("Series buscadas");
		assertThat(contentXml).contains("The Wire");
		assertThat(contentXml).contains("T1 - Piloto");
		assertThat(manifestXml).contains("application/vnd.oasis.opendocument.spreadsheet");
	}

	@Test
	@DisplayName("Genera PDF válido")
	void shouldGeneratePdf() throws IOException {
		List<SerWanted> list = sample();

		byte[] pdf = service.toPdf(list);

		assertThat(pdf).isNotNull();
		assertThat(pdf.length).isGreaterThan(200);
		String header = new String(pdf, 0, 4, StandardCharsets.US_ASCII);
		assertThat(header).isEqualTo("%PDF");
	}

	private List<SerWanted> sample() {
		return List.of(
				SerWanted.builder().titulo("The Wire").anyoInicio(2002).seasonDesc("T1 - Piloto").build(),
				SerWanted.builder().titulo("Lost").anyoInicio(2004).seasonDesc("T2").build());
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