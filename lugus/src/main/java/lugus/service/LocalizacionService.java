package lugus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.core.Localizacion;
import lugus.model.core.TiposUbicacion;
import lugus.repository.LocalizacionRepository;

@Service
@RequiredArgsConstructor
public class LocalizacionService {

	private final LocalizacionRepository locRepo;
	
	public List<Localizacion> findAll() {
		return locRepo.findAll();
	}

	public Optional<Localizacion> findById(String id) {
		return locRepo.findById(id);
	}

	public List<Localizacion> findAllOrderByDescripcion(TiposUbicacion tipoUbicacion) {
		return locRepo.findAllByTiposUbicacion(tipoUbicacion, Sort.by("descripcion").ascending());
	}

	public List<Localizacion> findAllOrderByDescripcion() {
		return locRepo.findAll(Sort.by("descripcion").ascending());
	}

}
