package lugus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lugus.model.Localizacion;

public interface LocalizacionRepository extends JpaRepository<Localizacion, String> {

}
