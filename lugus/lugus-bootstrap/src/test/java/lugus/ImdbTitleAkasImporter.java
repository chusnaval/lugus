/**
 * 
 */
package lugus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Importa el fichero title.akas.tsv.gz del dataset de IMDb a la tabla
 * lugus.imdb_title_akas de PostgreSQL.
 *
 * Uso típico: ImdbTitleAkasImporter importer = new ImdbTitleAkasImporter(
 * "jdbc:postgresql://host:5432/dbname", "db_user", "db_password",
 * "/path/to/title.akas.tsv.gz"); importer.importData();
 */
public class ImdbTitleAkasImporter {

	private static final String COPY_SQL = "COPY lugus.imdb_title_basics ("
			+ "tconst, titletype, primarytitle, originaltitle, isadult, startyear, endyear, runtimeminutes, genres) FROM STDIN WITH (FORMAT csv, DELIMITER E'\\t', NULL '\\N')";

	private final String jdbcUrl;
	private final String dbUser;
	private final String dbPassword;
	private final File tsvGzFile;

	/**
	 * @param jdbcUrl    URL JDBC de PostgreSQL (p.ej.
	 *                   jdbc:postgresql://localhost:5432/imdb)
	 * @param dbUser     Usuario de la base
	 * @param dbPassword Contraseña del usuario
	 * @param tsvGzPath  Ruta absoluta o relativa al archivo title.akas.tsv.gz
	 */
	public ImdbTitleAkasImporter(String jdbcUrl, String dbUser, String dbPassword, String tsvGzPath) {
		this.jdbcUrl = jdbcUrl;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.tsvGzFile = new File(tsvGzPath);
		if (!tsvGzFile.exists() || !tsvGzFile.isFile()) {
			throw new IllegalArgumentException("Archivo no encontrado: " + tsvGzPath);
		}
	}

	/**
	 * Ejecuta la importación completa.
	 *
	 * @throws SQLException si ocurre algún error en la base de datos
	 * @throws IOException  si hay problemas leyendo el archivo
	 */
	public void importData() throws SQLException, IOException {
		System.out.println("Conectando a la base de datos...");
		try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
			// Necesitamos el API de CopyManager del driver PostgreSQL
			org.postgresql.PGConnection pgConn = conn.unwrap(org.postgresql.PGConnection.class);
			org.postgresql.copy.CopyManager copyManager = pgConn.getCopyAPI();

			System.out.println("Abriendo archivo GZIP: " + tsvGzFile.getAbsolutePath());
			try (InputStream fileIn = new FileInputStream(tsvGzFile);
					InputStream gzipIn = new GZIPInputStream(fileIn);
					BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIn, StandardCharsets.UTF_8))) {
				String header = reader.readLine(); // <-- la descartamos
				if (header == null) {
					throw new IOException("El archivo está vacío.");
				}
				System.out.println("Cabecera detectada y omitida: " + header);
				// Creamos un pipe para alimentar a COPY sin cargar todo en memoria
				PipedOutputStream pipeOut = new PipedOutputStream();
				PipedInputStream pipeIn = new PipedInputStream(pipeOut);

				// Hilo que escribe en el pipe (el productor)
				Thread producer = new Thread(() -> {
					try (BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(pipeOut, StandardCharsets.UTF_8))) {

						String line;
						long count = 0;
						while ((line = reader.readLine()) != null) {
							// Cada línea del TSV original ya está separada por tabuladores.
							// Sólo necesitamos transformar los campos array y el booleano.
							String transformed = transformLine(line);
							writer.write(transformed);
							writer.newLine(); // COPY espera saltos de línea
							count++;

							if (count % 1_000_000 == 0) {
								System.out.printf("%,d filas procesadas...%n", count);
							}
						}
						writer.flush();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}, "TSV‑Producer");

				producer.start();

				// Consumidor: COPY lee directamente del pipe
				System.out.println("Iniciando COPY...");
				long copiedRows = copyManager.copyIn(COPY_SQL, pipeIn);
				System.out.printf("COPY finalizado: %d filas insertadas.%n", copiedRows);

				// Esperamos que el hilo productor termine (debe haberlo hecho antes de que COPY
				// cierre el pipe)
				producer.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("Importación interrumpida", e);
			}
		}
	}

	/**
	 * Transforma una línea del TSV original a la forma esperada por COPY:
	 * <ul>
	 * <li>Los campos array (types, attributes) pasan de "val1,val2" a
	 * "{val1,val2}"</li>
	 * <li>El campo booleano (isOriginalTitle) pasa de "0"/"1" a "0"/"1" (COPY lo
	 * interpreta como BIT)</li>
	 * <li>Los valores nulos representados por "\N" se dejan tal cual.</li>
	 * </ul>
	 *
	 * @param raw línea tal cual del fichero descomprimido
	 * @return línea lista para COPY
	 */
	private String transformLine(String raw) {
		// El fichero tiene 8 columnas separadas por TAB.
		// Usaremos split limit=8 para preservar posibles tabs dentro de los campos
		// (aunque no deberían existir).
		String[] cols = raw.split("\t", -1); // -1 mantiene los vacíos al final
		if (cols.length != 9) {
			// Si la línea está corrupta, la devolvemos tal cual; COPY fallará y nos dará
			// contexto.
			return raw;
		}

		// 0 titleId, 1 ordering, 2 title, 3 region, 4 language,
		// 5 types (coma‑separado), 6 attributes (coma‑separado), 7 isOriginalTitle
		// 1️⃣ Escapamos comillas dobles en **todos** los campos (incluido el título)
		for (int i = 0; i < cols.length; i++) {
			cols[i] = escapeCsvQuotes(cols[i]);
			cols[i] = escapeSqlSingleQuotes(cols[i]);
		}

		// Convertir tipos y atributos a la sintaxis de array de PostgreSQL
		cols[8] = toPgArray(cols[8]);
		// cols[2] = toPgArray(cols[2]);

		// El campo isOriginalTitle ya está como 0/1; COPY lo interpreta como BIT(1)
		// Si fuera "\N" lo dejamos sin cambios (NULL).

		// Reconstruir la línea con TAB como delimitador
		return String.join("\t", cols);
	}

	private String escapeCsvQuotes(String value) {
		if ("\\N".equals(value) || value.isEmpty()) {
			return value; // NULL o vacío → lo dejamos tal cual
		}
		// Duplicamos cada " que encontremos
		return value.replace("\"", "\"\"");
	}

	private String escapeSqlSingleQuotes(String value) {
		if ("\\N".equals(value) || value.isEmpty()) {
			return value; // NULL o vacío → lo dejamos tal cual
		}
		return value.replace("'", "''"); // ' → ''
	}

	/**
	 * Convierte una cadena separada por comas a la representación de array de
	 * PostgreSQL. Si el valor es "\N" (NULL) o vacío, devuelve "\N".
	 *
	 * @param csv coma‑separado o "\N"
	 * @return cadena del tipo {val1,val2}
	 */
	private String toPgArray(String csv) {
		if (csv == null || csv.isEmpty() || "\\N".equals(csv)) {
			return "\\N"; // NULL para COPY
		}
		// Escapamos comillas dobles y backslashes según la regla de PostgreSQL
		String[] elems = csv.split(",");
		List<String> escaped = new ArrayList<>(elems.length);
		for (String e : elems) {
			// Reemplazamos \ y " por sus versiones escapadas
			String esc = e.replace("\\", "\\\\").replace("\"", "\\\"");
			escaped.add("\"" + esc + "\""); // Cada elemento entre comillas
		}
		return "{" + String.join(",", escaped) + "}";
	}

	/* ------------------------------------------------------------------ */
	/* Main de prueba (puedes eliminarlo cuando integres la clase en tu */
	/* proyecto). */
	/* ------------------------------------------------------------------ */
	public static void main(String[] args) {

		String jdbc = "jdbc:postgresql://localhost:5432/lugus_dev";
		String user = "lugus_usr";
		String pass = "lugus_pass";
		String path = "D:\\lugus\\title.basics.tsv.gz";

		try {
			ImdbTitleAkasImporter importer = new ImdbTitleAkasImporter(jdbc, user, pass, path);
			importer.importData();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
}