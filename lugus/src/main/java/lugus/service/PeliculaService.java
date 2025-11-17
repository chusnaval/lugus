package lugus.service;

import lombok.RequiredArgsConstructor;
import lugus.dto.FiltrosDto;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Formato;
import lugus.model.Genero;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.repository.PeliculaRepository;
import lugus.repository.PeliculaSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculaService {

	private final PeliculaRepository peliculaRepo;
	private final LocalizacionService locService;

	public List<Pelicula> findAll() {
		return peliculaRepo.findAll();
	}

	public Optional<Pelicula> findById(Integer id) {
		return peliculaRepo.findById(id);
	}

	@Transactional
	public Pelicula crear(PeliculaCreateDto dto) {
		Localizacion loc = null;
		if (dto.getLocalizacionCodigo() != null && !dto.getLocalizacionCodigo().isBlank()) {
			loc = locService.findById(dto.getLocalizacionCodigo())
					.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
		}

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());

		Pelicula p = Pelicula.builder().titulo(dto.getTitulo()).anyo(dto.getAnyo()).formato(formato).genero(genero)
				.localizacion(loc).build();
		p.calcularCodigo();
		return peliculaRepo.save(p);
	}

	@Transactional
	public void delete(Integer id) {
		peliculaRepo.deleteById(id);
	}

	@Transactional
	public Pelicula addChild(Integer padreId, @Valid PeliculaCreateDto dto) {
		Pelicula padre = peliculaRepo.findById(padreId)
				.orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));
		Pelicula hijo = crear(dto);
		hijo.setPadre(padre);
		padre.getPeliculasPack().add(hijo);
		peliculaRepo.save(padre); 
		return hijo;
	}

	public boolean existsById(Integer id) {
		return peliculaRepo.existsById(id);
	}

	public List<Pelicula> findAllById(List<Integer> idsPeliculas) {
		return peliculaRepo.findAllById(idsPeliculas);
	}

	public Pelicula save(Pelicula pelicula) {
		return peliculaRepo.save(pelicula);

	}

	public long contarTodas() {
		return peliculaRepo.count();
	}


	public Page<Pelicula> findAllBy(FiltrosDto filtro, final int pagina, final String campo, final String direccion) {
		
		Pageable pageable = PageRequest.of(pagina, 30, Sort.by(Direction.fromString(direccion), campo));
		
		 Specification<Pelicula> spec = Specification.where(null);
		 spec = spec.and(PeliculaSpecification.porTitulo(filtro.getTitulo()));
		 spec = spec.and(PeliculaSpecification.porFromAnyo(filtro.getFromAnyo()));
		 spec = spec.and(PeliculaSpecification.porToAnyo(filtro.getToAnyo()));
		 spec = spec.and(PeliculaSpecification.porPack(filtro.getPack()));
		 spec = spec.and(PeliculaSpecification.porSteelbook(filtro.getSteelbook()));
		 spec = spec.and(PeliculaSpecification.porFunda(filtro.getFunda()));
		 spec = spec.and(PeliculaSpecification.porFormato(filtro.getFormato()));
		 spec = spec.and(PeliculaSpecification.porComprado(filtro.getComprado()));
		 spec = spec.and(PeliculaSpecification.porGenero(filtro.getGenero()));
		 spec = spec.and(PeliculaSpecification.porLocalizacion(filtro.getLocalizacion()));
		 spec = spec.and(PeliculaSpecification.porNotas(filtro.getNotas()));
		 spec = spec.and(PeliculaSpecification.porTieneCaratula(filtro.getTieneCaratula()));
		 
		 spec = spec.and(PeliculaSpecification.porActor(filtro.getActor()));
		 spec = spec.and(PeliculaSpecification.porDirector(filtro.getDirector()));
		 
		 
		  return peliculaRepo.findAll(spec, pageable);
	}
}
