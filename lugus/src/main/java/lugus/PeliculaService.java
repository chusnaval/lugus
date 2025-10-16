package lugus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculaService {
	
	private final PeliculaRepository repo;

	public List<Pelicula> findAll() {
		return repo.findAll();
	}

	public Optional<Pelicula> findById(Integer id) {
		return repo.findById(id);
	}

	@Transactional
	public Pelicula save(Pelicula pelicula) {
		// Si el padre está presente, aseguramos la consistencia bidireccional
		if (pelicula.getPadre() != null) {
			pelicula.getPadre().getPeliculasPack().add(pelicula);
		}
		return repo.save(pelicula);
	}

	@Transactional
	public void delete(Integer id) {
		repo.deleteById(id);
	}

	@Transactional
	public Pelicula addChild(Integer padreId, Pelicula child) {
		Pelicula padre = repo.findById(padreId).orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));
		child.setPadre(padre);
		padre.getPeliculasPack().add(child);
		repo.save(padre); // cascada guardará al hijo
		return child;
	}

	public boolean existsById(Integer id) {
		return repo.existsById(id);
	}

	public List<Pelicula> findAllById(List<Integer> idsPeliculas) {
		return repo.findAllById(idsPeliculas);
	}
}
