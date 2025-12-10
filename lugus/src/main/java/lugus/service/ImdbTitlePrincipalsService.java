package lugus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.ImdbTitlePrincipals;
import lugus.repository.ImdbTitlePrincipalsRepository;

@Service
@RequiredArgsConstructor
public class ImdbTitlePrincipalsService {
 
	private final ImdbTitlePrincipalsRepository imdbTitlePrincipalsRepository;
	
	public List<ImdbTitlePrincipals> findAllByIdNconst(final String nConst){
		return imdbTitlePrincipalsRepository.findAllByIdNconst(nConst);
	}
}
