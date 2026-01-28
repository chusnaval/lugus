package lugus.service.films;

import java.io.IOException;

public interface DwFotoServiceI {

	byte[] descargar(Integer source, String url) throws IOException;

}
