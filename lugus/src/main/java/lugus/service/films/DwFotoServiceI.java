package lugus.service.films;

import java.io.IOException;

public interface DwFotoServiceI {

	byte[] descargar(Integer fuente, String url) throws IOException;

}
