package lugus.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.TiposUbicacionRepository;

@Service
@RequiredArgsConstructor
public class TiposUbicacionService {

	private final TiposUbicacionRepository repository;
}
