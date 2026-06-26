package lugus.repository.films;

import java.time.Instant;

import lugus.model.films.Edicion;
import lugus.model.values.Formato;

public interface EdicionRepository {

	boolean existsByCodigo(String codigo);

	int countByTsAltaAfter(Instant date);

	int countByComprado(final boolean value);

	int countByFormatoAndComprado(Formato format, boolean b);

	Edicion findById(int id);

}
