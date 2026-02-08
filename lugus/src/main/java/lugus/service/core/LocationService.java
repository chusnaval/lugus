package lugus.service.core;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FiltrosDto;
import lugus.model.core.Location;
import lugus.model.core.LocationType;
import lugus.repository.core.LocationRepository;

@Service
@RequiredArgsConstructor
public class LocationService {

	private static final String DESCRIPCION = "descripcion";
	private final LocationRepository locRepo;
	
	public List<Location> findAll() {
		return locRepo.findAll();
	}

	public Optional<Location> findById(String id) {
		return locRepo.findById(id);
	}

	public List<Location> findAllOrderByDescripcion(LocationType locationType) {
		return locRepo.findAllByLocationType(locationType, Sort.by(DESCRIPCION).ascending());
	}

	public List<Location> findAllOrderByDescripcion() {
		return locRepo.findAll(Sort.by(DESCRIPCION).ascending());
	}

	public Location save(Location loc) {
		return locRepo.save(loc);
	}

	public void deleteById(String id) {
		locRepo.deleteById(id);
	}

	public Page<Location> findAllBy(FiltrosDto filter) {
		Sort sort = Sort.by(Direction.ASC, DESCRIPCION);

		Pageable pageable = PageRequest.of(filter.getPagina().get(), 50, sort);

		Specification<Location> spec = Specification.where(null);

		return locRepo.findAll(spec, pageable);
	}

}
