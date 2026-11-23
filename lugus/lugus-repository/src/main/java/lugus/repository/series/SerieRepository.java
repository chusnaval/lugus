package lugus.repository.series;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import lugus.model.series.Series;


public interface SerieRepository {

	List<Series> findAll();

	Optional<Series> findById(final Integer id);

	Series save(Series serie);

	void deleteById(Integer id);

	long count();

	Page<Series> findAll(Specification<Series> spec, Pageable pageable);

	Collection<? extends Series> findByTitleAndStartYear(final String title, final Integer year);

	Collection<? extends Series> findByMgmtTitleAndStartYear(final String title, final Integer year);

	List<Series> findAllByOrderByMgmtTitleAscStartYearAsc();

	int countByTsCreationAfter(final Instant limit);

	int countByPurchased(final boolean value);

	Optional<Series> findByImdbId(final String imdbId);

	Iterable<Series> searchByTitleContainingIgnoreCase(final String query);

	void flush();
}
