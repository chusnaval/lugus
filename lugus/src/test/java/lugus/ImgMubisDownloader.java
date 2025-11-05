package lugus;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImgMubisDownloader {

	private static final int MUBIS = 6;
	private static String url = "jdbc:postgresql://localhost:5432/lugus_dev";
	private static String user = "lugus_usr";
	private static String password = "lugus_pass";
	static Logger lgr = Logger.getLogger(ImgMubisDownloader.class.getName());

	public static void main(String[] args) throws IOException {
		// PostgreSQL connection details
		Path path = Paths.get("src/test/resources/tmp_caratulas.csv");
		List<Caratula> resultado = Files.lines(path).skip(1) // saltar cabecera, si la hay
				.map(line -> line.split(";"))
				.map(values -> new Caratula(values[0].trim(), values[1].trim(), values[2].trim(), -1))
				.collect(Collectors.toList());

		try (Connection con = DriverManager.getConnection(url, user, password)) {
			resultado.forEach(t -> {
				try {
					boolean existeEnBaseDatos = buscarUrlCaratula(t.getUrl(), con);
					if (!existeEnBaseDatos) {
						t.setPelicula_id(localizarPeliculaId(t.getTitulo(), con));
						imprimirEInsertar(t, con);
					} else {
						System.out.println("La url de " + t.getTitulo() + " ya ha sido insertada ");
					}
				} catch (IOException | SQLException e) {
					lgr.log(Level.SEVERE, e.getMessage(), e);
				}
			});
		} catch (SQLException ex) {
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}

	}

	private static boolean buscarUrlCaratula(String url, Connection con) throws SQLException {

		try (Statement st = con.createStatement()) {
			String sql = "select pelicula_id FROM public.peliculas_fotos where url = '" + url + "'";
			ResultSet rs = st.executeQuery(sql);

			return rs.next();

		}
	}

	private static int localizarPeliculaId(String params, Connection con) throws SQLException {

		List<String> longestWords = Arrays.asList(params.split(" ")).stream()
				.sorted((w1, w2) -> Integer.compare(w2.length(), w1.length())).limit(3) // Take top 3
				.collect(Collectors.toList());
		boolean correct = false;
		int peliculaIdEncontrado = -1;
		try (Statement st = con.createStatement()) {
			String sql = " SELECT id, titulo, formato, anyo FROM public.peliculas where formato = 2 and upper(titulo) similar to upper('%("
					+ String.join("|", longestWords)
					+ ")%') and id not in (select pelicula_id FROM public.peliculas_fotos) ORDER BY id ASC  ";

			// System.out.println("Buscamos SQL: " + sql);

			ResultSet rs = st.executeQuery(sql);

			System.out.println("\n\n===============================");
			System.out.println("Buscando pelicula: " + params);
			System.out.println("Peliculas encontradas: ");
			List<Integer> encontrados = new ArrayList<Integer>();
			while (rs.next()) {
				int id = rs.getInt("id");
				String titulo = rs.getString("titulo");
				int formato = rs.getInt("formato");
				int anyo = rs.getInt("anyo");

				System.out.println("Id: " + id + " titulo: " + titulo + " formato: " + formato + " anyo: " + anyo);
				encontrados.add(id);
			}

			if (encontrados.isEmpty()) {
				System.out.println("Ninguna");
			} else {

				encontrados.add(-1);// para poder seleccionar que no encuentra

				Scanner myObj = new Scanner(System.in);
				try {
					while (!correct) {
						System.out.println("Enter id localizado (-1 si no esta listado): ");
						String userId = myObj.next();
						peliculaIdEncontrado = Integer.parseInt(userId);
						correct = encontrados.contains(peliculaIdEncontrado);
					}
				} catch (Exception e) {
					System.out.println("Error " + e);
				}
			}
		}
		return peliculaIdEncontrado;
	}

	private static void insertar(Caratula caratula, Connection con)
			throws SQLException, MalformedURLException, IOException {
		try (PreparedStatement pst = con.prepareStatement(
				"INSERT INTO public.peliculas_fotos ( pelicula_id, url, fuente_id, foto) VALUES ( ?, ?, ?, ?)")) {

			URL imgUrl = new URL(caratula.getUrl());
			InputStream in = imgUrl.openStream();
			pst.setInt(1, caratula.getPelicula_id());
			pst.setString(2, caratula.getUrl());
			pst.setInt(3, MUBIS);
			pst.setBinaryStream(4, in);
			pst.executeUpdate();
			pst.close();
			in.close();
		}
	}

	private static void imprimirEInsertar(Caratula caratula, Connection con)
			throws MalformedURLException, IOException, SQLException {

		if (caratula.getPelicula_id() > 0) {
			insertar(caratula, con);
			System.out.println("Insertada pelicula: " + caratula.getTitulo());
		} else {
			System.out.println("Pelicula no encontrada: " + caratula.getTitulo());
		}

	}

}
