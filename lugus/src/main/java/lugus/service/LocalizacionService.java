package lugus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.Localizacion;
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

}
