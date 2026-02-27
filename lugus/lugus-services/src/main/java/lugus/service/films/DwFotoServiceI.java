package lugus.service.films;

import java.io.IOException;
import java.net.URISyntaxException;

public interface DwFotoServiceI {

	byte[] descargar(Integer source, String url) throws IOException, URISyntaxException;

}
