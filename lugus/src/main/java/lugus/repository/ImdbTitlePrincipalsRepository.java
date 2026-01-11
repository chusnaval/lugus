package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.model.imdb.ImdbTitlePrincipalsId;

public interface ImdbTitlePrincipalsRepository extends JpaRepository<ImdbTitlePrincipals, ImdbTitlePrincipalsId> {

	@Query("SELECT i FROM ImdbTitlePrincipals i WHERE i.id.nconst = :nConst and i.id.category != 'self' and i.id.category != 'producer' and i.id.category != 'editor' and i.id.category != 'archive_footage'")
	List<ImdbTitlePrincipals> findAllByIdNconst(@Param("nConst") String nConst);

}
