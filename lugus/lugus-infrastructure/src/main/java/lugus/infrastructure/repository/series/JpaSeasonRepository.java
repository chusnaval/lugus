package lugus.infrastructure.repository.series;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.series.Season;
import lugus.repository.series.SeasonRepository;

public interface JpaSeasonRepository extends SeasonRepository, JpaRepository<Season, Integer> {

}