package lugus.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.GetAnteriorRepository;
import lugus.repository.GetPosteriorRepository;

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
