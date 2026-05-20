package lugus.repository.titles;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import lugus.model.titles.Title;

@Repository
public interface TitleRepository {

	Optional<Title> findById(Long titleId);
	
	Optional<Title> findByImdb_Tconst(String imdbId);

	Title save(Title title);
	
	List<Title> searchByTitleContainingIgnoreCase(String query);

	@Query("""
			    SELECT t FROM Title t
			    WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%'))
			""")
	List<Title> search(@Param("query") String query);
}
