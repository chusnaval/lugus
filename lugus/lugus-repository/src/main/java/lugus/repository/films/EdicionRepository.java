package lugus.repository.films;

import java.time.Instant;

import lugus.model.films.Edicion;
import lugus.model.values.Formato;

public interface EdicionRepository {

	boolean existsByCodigoAndIdNot(String codigo, int id);

	int countByTsAltaAfter(Instant date);

	int countByComprado(final boolean value);

	int countByFormatoAndComprado(Formato format, boolean b);

	Edicion findById(int id);

	Edicion save(Edicion edicion);

}
