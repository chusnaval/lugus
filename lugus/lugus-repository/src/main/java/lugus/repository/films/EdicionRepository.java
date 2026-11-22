package lugus.repository.films;

import java.time.Instant;

import lugus.model.films.Edition;
import lugus.model.values.Format;

public interface EdicionRepository {

	boolean existsByCodigoAndIdNot(String codigo, int id);

	int countByTsAltaAfter(Instant date);

	int countByComprado(final boolean value);

	int countByFormatoAndComprado(Format format, boolean b);

	Edition findById(int id);

	Edition save(Edition edicion);

}
