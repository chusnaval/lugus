package lugus.infrastructure.repository.people;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.people.SeriesPersonal;
import lugus.repository.people.SeriesPersonalRepository;


public interface JpaSeriesPersonalRepository extends SeriesPersonalRepository, JpaRepository<SeriesPersonal, Integer> {

}