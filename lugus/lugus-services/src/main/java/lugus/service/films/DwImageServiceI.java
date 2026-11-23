package lugus.service.films;

import java.io.IOException;
import java.net.URISyntaxException;

public interface DwImageServiceI {

	byte[] download(final Integer source, final String url) throws IOException, URISyntaxException;

}
