package lugus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Localizacion;

public interface LocalizacionRepository extends JpaRepository<Localizacion, String> {

	List<Localizacion> findAllByOrderByDescripcionAsc();

}
