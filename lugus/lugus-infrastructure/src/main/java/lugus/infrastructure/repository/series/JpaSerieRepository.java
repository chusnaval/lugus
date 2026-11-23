package lugus.infrastructure.repository.series;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.series.Series;
import lugus.repository.series.SerieRepository;


public interface JpaSerieRepository extends SerieRepository, JpaRepository<Series, Integer>, JpaSpecificationExecutor<Series> {

}