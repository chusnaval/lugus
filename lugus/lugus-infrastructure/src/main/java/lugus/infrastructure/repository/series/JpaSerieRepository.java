package lugus.infrastructure.repository.series;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lugus.model.series.Serie;
import lugus.repository.series.SerieRepository;

public interface JpaSerieRepository extends SerieRepository, JpaRepository<Serie, Integer>, JpaSpecificationExecutor<Serie> {

}