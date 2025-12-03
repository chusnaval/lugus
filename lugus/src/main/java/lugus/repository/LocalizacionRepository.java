package lugus.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Localizacion;
import lugus.model.TiposUbicacion;

public interface LocalizacionRepository extends JpaRepository<Localizacion, String> {

	List<Localizacion> findAll();

	List<Localizacion> findAllByTiposUbicacion(TiposUbicacion tipoObj, Sort sort);

}
