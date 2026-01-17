package lugus.repository.inf;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.inf.InfLocalizaciones;
import lugus.model.inf.InfLocalizacionesId;

public interface InfLocalizacionesRepository extends JpaRepository<InfLocalizaciones, InfLocalizacionesId> {

	public List<InfLocalizaciones> findAllById_GeneroAndId_FormatoAndId_FundaAndId_SteelbookAndContadorGreaterThan(final String genero,
			final int formato, final boolean funda, final boolean steelbook, final int contador);
}
