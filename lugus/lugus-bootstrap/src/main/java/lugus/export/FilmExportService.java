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

import lugus.model.films.Pelicula;

@Service
public class FilmExportService {

	public String toMarkdown(List<Pelicula> list) {
		StringBuilder md = new StringBuilder();
		md.append("# Películas\n\n");
		md.append("| Nº | Título | Año | Formato | Situación | Notas |\n");
		md.append("|---|---|---:|---|\n");
		int index = 1;
		for (Pelicula item : list) {
			item.calcularSituacion();
			md.append("| ")
					.append(escapeMd(index++ + ""))
					.append(" | ")
					.append(escapeMd(item.getTitulo()))
					.append(" | ")
					.append(item.getAnyo())
					.append(" | ")
					.append(escapeMd(item.getFormato().name()))
					.append(" | ")
					.append(escapeMd(item.getSituacion()))
					.append(" | ")
					.append(escapeMd(item.getNotas()))
					.append(" |\n");
		}

		return md.toString();
	}

	public byte[] toOds(List<Pelicula> list) throws IOException {
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

	public byte[] toPdf(List<Pelicula> list) throws IOException {
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
				content.showText("Lugus - Películas");
				content.endText();

				y -= 28;
				content.beginText();
				content.setFont(PDType1Font.HELVETICA_BOLD, 11);
				content.newLineAtOffset(margin, y);
				content.showText("Nº");
				content.newLineAtOffset(70, 0);
				content.showText("Titulo");
				content.newLineAtOffset(260, 0);
				content.showText("Anyo");
				content.newLineAtOffset(70, 0);
				content.showText("Formato");
				content.newLineAtOffset(70, 0);
				content.showText("Situación");
				content.newLineAtOffset(120, 0);
				content.showText("Notas");
				content.endText();
				
				int index = 1;
				y -= leading;
				for (Pelicula item : list) {
					if (y < 60) {
						break;
					}

					content.beginText();
					content.setFont(PDType1Font.HELVETICA, 10);
					content.newLineAtOffset(margin, y);
					content.showText(String.valueOf(index++));
					content.newLineAtOffset(70, 0);
					content.showText(trimForPdf(item.getTitulo(), 42));
					content.newLineAtOffset(260, 0);
					content.showText(String.valueOf(item.getAnyo()));
					content.newLineAtOffset(70, 0);
					content.showText(trimForPdf(item.getFormato().name(), 34));
					content.newLineAtOffset(70, 0);
					content.showText(trimForPdf(item.getSituacion(), 42));
					content.newLineAtOffset(120, 0);
					content.showText(trimForPdf(item.getNotas(), 50));
					content.endText();

					y -= leading;
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

	private String buildOdsContentXml(List<Pelicula> list) {
		StringBuilder rows = new StringBuilder();

		appendOdsRow(rows, "Nº", "Título", "Año", "Formato", "Situación", "Notas");
		int index = 1;
		for (Pelicula item : list) {
			item.calcularSituacion();
			appendOdsRow(rows,
					String.valueOf(index++),
					nullSafe(item.getTitulo()),
					String.valueOf(item.getAnyo()),
					nullSafe(item.getFormato().name()),
					nullSafe(item.getSituacion()),
					nullSafe(item.getNotas()))
			;
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
					  <table:table table:name="Películas buscadas">
						%s
					  </table:table>
					</office:spreadsheet>
				  </office:body>
				</office:document-content>
				""".formatted(rows);
	}

	private void appendOdsRow(StringBuilder rows, String col0, String col1, String col2, String col3, String col4, String col5) {
		rows.append("<table:table-row>")
				.append(odsCell(col0))
				.append(odsCell(col1))
				.append(odsCell(col2))
				.append(odsCell(col3))
				.append(odsCell(col4))
				.append(odsCell(col5))
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
