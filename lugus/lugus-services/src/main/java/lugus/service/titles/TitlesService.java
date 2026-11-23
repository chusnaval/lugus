package lugus.service.titles;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.titles.Title;
import lugus.model.values.TitleType;
import lugus.repository.titles.TitleRepository;

@Service
@RequiredArgsConstructor
public class TitlesService {

	   private final TitleRepository titleRepo;
	   
	   public List<Title> searchTitles(String query) {
	        return titleRepo.search(query);
	    }

	   public Optional<Title> findById(Long titleId) {
		return titleRepo.findById(titleId);
	   }

	   public Optional<Title> findByFilmId(Integer id) {
		   return titleRepo.findByFilm_Id(id);
	   }
	   
	   public Optional<Title> findBySerieId(Integer id) {
		   return titleRepo.findBySeries_Id(id);
	   }
	   
	   public Optional<Title> findByImdbId(String id) {
		   return titleRepo.findByImdb_Tconst(id);
	   }

	   public Title save(Title title) {
		return titleRepo.save(title);
		
	   }

	   public List<Title> findAll() {
		return titleRepo.findAll();
	   }

	   public List<Title> findByType(TitleType external) {
		return titleRepo.findByType(external);
	   }

	   public void delete(Long id) {
		titleRepo.deleteById(id);
		
	   }
}
