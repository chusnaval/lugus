package lugus.infrastructure.repository.titles;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.titles.Title;
import lugus.repository.titles.TitleRepository;

public interface JpaTitleRepository extends TitleRepository, JpaRepository<Title, Long> {

}
