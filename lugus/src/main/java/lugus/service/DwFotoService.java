package lugus.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DwFotoService implements DwFotoServiceI {

	@Override
	public byte[] descargar(Integer fuente, String url) throws IOException {
		URL imgUrl = new URL(url);
		try (InputStream in = imgUrl.openStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[4096];
			int bytesLeidos;
			while ((bytesLeidos = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesLeidos);
			}
			return out.toByteArray();

		}
	}

}
