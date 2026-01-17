package lugus.service.imdb;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.imdb.ImdbTitlePrincipals;
import lugus.repository.imdb.ImdbTitlePrincipalsRepository;

@Service
@RequiredArgsConstructor
public class ImdbTitlePrincipalsService {
 
	private final ImdbTitlePrincipalsRepository imdbTitlePrincipalsRepository;
	
	public List<ImdbTitlePrincipals> findAllByIdNconst(final String nConst){
		return imdbTitlePrincipalsRepository.findAllByIdNconst(nConst);
	}
}
