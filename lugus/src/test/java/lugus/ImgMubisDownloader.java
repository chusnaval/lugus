package lugus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImgMubisDownloader {

	public static void main(String[] args) throws IOException {
	    // PostgreSQL connection details
        String url = "jdbc:postgresql://localhost:5432/lugus_dev";
        String user = "lugus_usr";
        String password = "lugus_pass";
        
        try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pst = con.prepareStatement("INSERT INTO public.peliculas_fotos (id, pelicula_id, url, fuente_id, foto) VALUES (?, ?, ?, ?, ?)")) {
    		URL imgUrl = new URL("https://www.mubis.es/media/releases/2512/5605/13-fantasmas-blu-ray-l_cover.jpg");
    		InputStream in = imgUrl.openStream();
    		pst.setInt(1, 1);
    		pst.setInt(2, 5011);
    		pst.setString(3, "https://www.mubis.es/media/releases/2512/5605/13-fantasmas-blu-ray-l_cover.jpg");
    		pst.setInt(4, 6);
    		pst.setBinaryStream(5, in);
    		pst.executeUpdate();
    		pst.close();
    		in.close();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ImgMubisDownloader.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

	}
}	
