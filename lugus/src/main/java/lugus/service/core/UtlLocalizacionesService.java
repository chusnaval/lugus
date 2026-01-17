package lugus.service.core;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.core.GetAnteriorRepository;
import lugus.repository.core.GetPosteriorRepository;

@Service
@RequiredArgsConstructor
public class UtlLocalizacionesService {

	private final GetAnteriorRepository getAnteriorRepository;
	
	private final GetPosteriorRepository getPosteriorRepository;
	

	public String getAnterior(final String codigo) {
		return getAnteriorRepository.getAnterior(codigo);
	}
	
	public String getPosterior(final String codigo) {
		return getPosteriorRepository.getPosterior(codigo);
	}
}
