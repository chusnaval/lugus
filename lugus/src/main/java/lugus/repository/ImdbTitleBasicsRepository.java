package lugus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.ImdbTitleBasics;

public interface ImdbTitleBasicsRepository extends JpaRepository<ImdbTitleBasics, String> {

}
