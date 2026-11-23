package lugus.repository.series;

import java.util.List;
import java.util.Optional;

import lugus.model.series.Season;


public interface SeasonRepository {

	List<Season> findAll();

	Optional<Season> findById(final Integer id);

	Season save(final Season season);

	void deleteById(final Integer id);

}
