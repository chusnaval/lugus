package lugus.repository.inf;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.inf.InfLocations;
import lugus.model.inf.InfLocationsId;

public interface InfLocationsRepository extends JpaRepository<InfLocations, InfLocationsId> {

	public List<InfLocations> findAllById_GeneroAndId_FormatoAndId_FundaAndId_SteelbookAndContadorGreaterThan(final String genero,
			final int formato, final boolean funda, final boolean steelbook, final int contador);
}
