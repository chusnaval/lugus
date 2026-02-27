package lugus.repository.inf;

import java.util.List;
import java.util.Optional;

import lugus.model.inf.InfLocations;
import lugus.model.inf.InfLocationsId;

public interface InfLocationsRepository {

	Optional<InfLocations> findById(InfLocationsId id);

	InfLocations save(InfLocations infLocations);

	void deleteById(InfLocationsId id);

	public List<InfLocations> findAllById_GeneroAndId_FormatoAndId_FundaAndId_SteelbookAndContadorGreaterThan(final String genero,
			final int formato, final boolean funda, final boolean steelbook, final int contador);
}
