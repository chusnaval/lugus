package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.InfLocalizaciones;

public interface InfLocalizacionesRepository extends JpaRepository<InfLocalizaciones, String> {

	public List<InfLocalizaciones> findAllByGeneroAndFormatoAndContadorGreaterThan(final String genero, final int formato, final int contador);
}
