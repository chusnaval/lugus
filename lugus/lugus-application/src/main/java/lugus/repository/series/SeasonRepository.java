package lugus.repository.series;

import java.util.List;
import java.util.Optional;

import lugus.model.series.Season;


public interface SeasonRepository {

	List<Season> findAll();

	Optional<Season> findById(Integer id);

	Season save(Season season);

	void deleteById(Integer id);

}
