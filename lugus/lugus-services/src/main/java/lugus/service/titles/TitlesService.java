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

	   public Optional<Title> findByPelicula_Id(Integer id) {
		   return titleRepo.findByPelicula_Id(id);
	   }
	   
	   public Optional<Title> findBySerie_Id(Integer id) {
		   return titleRepo.findBySerie_Id(id);
	   }
	   
	   public Optional<Title> findByImdb_Id(String id) {
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
