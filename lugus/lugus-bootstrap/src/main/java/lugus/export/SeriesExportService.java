package lugus.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import lugus.model.series.Season;
import lugus.model.series.Serie;
@Service
public class SeriesExportService {

	public String toMarkdown(List<Serie> list) {
		StringBuilder md = new StringBuilder();
		md.append("# Series \n\n");
		md.append("| Título | Año | Temporada |  Formato | Situación | Notas |\n");
		md.append("|---|---:|---|---|---|---|---|\n");

		for (Serie item : list) {
		
			// quiero cada temporada en una fila diferente, con el mismo título , pero con la temporada en la columna de temporada
				for (Season temp : item.getSeasons()) {
					temp.calcularSituacion();
					md.append("| ")
							.append(escapeMd(item.getTitulo()))
							.append(" | ")
							.append(temp.getYear())
							.append(" | ")
							.append(escapeMd(String.valueOf(temp.getOrder())))
							.append(" | ")
							.append(escapeMd(item.getFormato().name()))
							.append(" | ")
							.append(escapeMd(temp.getSituacion()))
							.append(" | ")
							.append(escapeMd(item.getNotas()))
							.append(" |\n");
				}
		}

		return md.toString();
	}

	public byte[] toOds(List<Serie> list) throws IOException {
		String contentXml = buildOdsContentXml(list);
		String manifestXml = """
				<?xml version="1.0" encoding="UTF-8"?>
				<manifest:manifest xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0">
				  <manifest:file-entry manifest:full-path="/" manifest:media-type="application/vnd.oasis.opendocument.spreadsheet"/>
				  <manifest:file-entry manifest:full-path="content.xml" manifest:media-type="text/xml"/>
				</manifest:manifest>
				""";

		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(out)) {
			ZipEntry mimetype = new ZipEntry("mimetype");
			zos.putNextEntry(mimetype);
			zos.write("application/vnd.oasis.opendocument.spreadsheet".getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();

			ZipEntry manifest = new ZipEntry("META-INF/manifest.xml");
			zos.putNextEntry(manifest);
			zos.write(manifestXml.getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();

			ZipEntry content = new ZipEntry("content.xml");
			zos.putNextEntry(content);
			zos.write(contentXml.getBytes(StandardCharsets.UTF_8));
			zos.closeEntry();

			zos.finish();
			return out.toByteArray();
		}
	}

	public byte[] toPdf(List<Serie> list) throws IOException {
		try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			PDPage page = new PDPage();
			doc.addPage(page);

			float margin = 50;
			float y = 760;
			float leading = 16;

			try (PDPageContentStream content = new PDPageContentStream(doc, page)) {
				content.beginText();
				content.setFont(PDType1Font.HELVETICA_BOLD, 16);
				content.newLineAtOffset(margin, y);
				content.showText("Lugus - Series");
				content.endText();

				y -= 28;
				content.beginText();
				content.setFont(PDType1Font.HELVETICA_BOLD, 11);
				content.newLineAtOffset(margin, y);
				content.showText("Titulo");
				content.newLineAtOffset(260, 0);
				content.showText("Año");
				content.newLineAtOffset(70, 0);
				content.showText("Temporada");
				content.newLineAtOffset(70, 0);
				content.showText("Formato");
				content.newLineAtOffset(70, 0);
				content.showText("Situación");
				content.newLineAtOffset(70, 0);
				content.showText("Notas");
				content.endText();

				y -= leading;
				for (Serie item : list) {
					for (Season temp : item.getSeasons()) {
						temp.calcularSituacion();
						if (y < 60) {
							break;
						}
	
						content.beginText();
						content.setFont(PDType1Font.HELVETICA, 10);
						content.newLineAtOffset(margin, y);
						content.showText(trimForPdf(item.getTitulo(), 42));
						content.newLineAtOffset(260, 0);
						content.showText(String.valueOf(temp.getYear()));
						content.newLineAtOffset(70, 0);
						content.showText(trimForPdf(String.valueOf(temp.getOrder()), 34));
						content.newLineAtOffset(70, 0);
						content.showText(trimForPdf(item.getFormato().name(), 34));
						content.newLineAtOffset(70, 0);
						content.showText(trimForPdf(temp.getSituacion(), 42));
						content.newLineAtOffset(70, 0);
						content.showText(trimForPdf(item.getNotas(), 70));
						content.endText();
	
						y -= leading;
					}
				}
			}

			doc.save(out);
			return out.toByteArray();
		}
	}

	private String nullSafe(String value) {
		return value == null ? "" : value;
	}

	private String escapeMd(String value) {
		return nullSafe(value).replace("|", "\\|");
	}

	private String escapeXml(String value) {
		return nullSafe(value)
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	private String buildOdsContentXml(List<Serie> list) {
		StringBuilder rows = new StringBuilder();

		appendOdsRow(rows, "Título", "Año", "Temporada", "Formato", "Situación", "Notas");
		for (Serie item : list) {
			for (Season temp : item.getSeasons()) {
				temp.calcularSituacion();
			appendOdsRow(rows,
					nullSafe(item.getTitulo()),
					String.valueOf(temp.getYear()),
					nullSafe(String.valueOf(temp.getOrder())),
					nullSafe(item.getFormato().name()),
					nullSafe(temp.getSituacion()),
					nullSafe(item.getNotas())
					);
			}
		}

		return """
				<?xml version="1.0" encoding="UTF-8"?>
				<office:document-content
				  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
				  xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
				  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
				  office:version="1.2">
				  <office:body>
					<office:spreadsheet>
					  <table:table table:name="Series buscadas">
						%s
					  </table:table>
					</office:spreadsheet>
				  </office:body>
				</office:document-content>
				""".formatted(rows);
	}

	private void appendOdsRow(StringBuilder rows, String col1, String col2, String col3, String col4, String col5, String col6) {
		rows.append("<table:table-row>")
				.append(odsCell(col1))
				.append(odsCell(col2))
				.append(odsCell(col3))
				.append(odsCell(col4))
				.append(odsCell(col5))
				.append(odsCell(col6))
				.append("</table:table-row>");
	}

	private String odsCell(String value) {
		return "<table:table-cell office:value-type=\"string\"><text:p>"
				+ escapeXml(value)
				+ "</text:p></table:table-cell>";
	}

	private String trimForPdf(String value, int maxLength) {
		String safe = nullSafe(value);
		if (safe.length() <= maxLength) {
			return safe;
		}
		return safe.substring(0, maxLength - 1) + "…";
	}
}
