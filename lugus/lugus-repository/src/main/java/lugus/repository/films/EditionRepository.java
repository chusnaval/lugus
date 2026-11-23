package lugus.repository.films;

import java.time.Instant;

import lugus.model.films.Edition;
import lugus.model.values.Format;

public interface EditionRepository {

	boolean existsByCodeAndIdNot(final String code, int id);

	int countByTsCreationAfter(final Instant date);

	int countByPurchased(final boolean value);

	int countByFormatAndPurchased(final Format format, final boolean b);

	Edition findById(final int id);

	Edition save(final Edition edition);

}
