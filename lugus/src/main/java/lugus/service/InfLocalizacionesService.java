package lugus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.inf.InfLocalizaciones;
import lugus.repository.InfLocalizacionesRepository;

@Service
@RequiredArgsConstructor
public class InfLocalizacionesService {

	private final InfLocalizacionesRepository repo;

	public List<InfLocalizaciones> findAllByGeneroAndFormato(final String genero, final int formato,
			final boolean funda, final boolean steelbook) {
		return repo.findAllById_GeneroAndId_FormatoAndId_FundaAndId_SteelbookAndContadorGreaterThan(genero, formato, funda,
				steelbook, 0);
	}
}
