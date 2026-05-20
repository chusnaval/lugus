package lugus.service.titles;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.titles.Title;
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
}
