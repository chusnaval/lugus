package lugus.service.core;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.core.Location;
import lugus.model.core.TiposUbicacion;
import lugus.repository.core.LocationRepository;

@Service
@RequiredArgsConstructor
public class LocationService {

	private final LocationRepository locRepo;
	
	public List<Location> findAll() {
		return locRepo.findAll();
	}

	public Optional<Location> findById(String id) {
		return locRepo.findById(id);
	}

	public List<Location> findAllOrderByDescripcion(TiposUbicacion tipoUbicacion) {
		return locRepo.findAllByTiposUbicacion(tipoUbicacion, Sort.by("descripcion").ascending());
	}

	public List<Location> findAllOrderByDescripcion() {
		return locRepo.findAll(Sort.by("descripcion").ascending());
	}

}
