package lugus.service.core;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.core.GetPreviousRepository;
import lugus.repository.core.GetNextRepository;


@Service
@RequiredArgsConstructor
public class UtlLocationService {

	private final GetPreviousRepository getAnteriorRepository;
	
	private final GetNextRepository getPosteriorRepository;
	

	public String getAnterior(final String codigo) {
		return getAnteriorRepository.getPrevious(codigo);
	}
	
	public String getPosterior(final String codigo) {
		return getPosteriorRepository.getNext(codigo);
	}
}
