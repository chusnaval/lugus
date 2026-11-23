package lugus.service.films;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class DwImageService implements DwImageServiceI {

	@Override
	public byte[] download(Integer source, String url) throws IOException {
		URL imgUrl = URI.create(url).toURL();
		try (InputStream in = imgUrl.openStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[4096];
			int readedBytes;
			while ((readedBytes = in.read(buffer)) != -1) {
				out.write(buffer, 0, readedBytes);
			}
			return out.toByteArray();

		}
	}

}
