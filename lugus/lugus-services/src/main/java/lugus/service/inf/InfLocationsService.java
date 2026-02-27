package lugus.service.inf;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.inf.InfLocations;
import lugus.repository.inf.InfLocationsRepository;

@Service
@RequiredArgsConstructor
public class InfLocationsService {

	private final InfLocationsRepository repo;

	public List<InfLocations> findAllByGeneroAndFormato(final String genero, final int formato,
			final boolean funda, final boolean steelbook) {
		return repo.findAllById_GeneroAndId_FormatoAndId_FundaAndId_SteelbookAndContadorGreaterThan(genero, formato, funda,
				steelbook, 0);
	}
}
