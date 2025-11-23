package lugus.service;

import lombok.RequiredArgsConstructor;
import lugus.controller.DwFotoService;
import lugus.dto.FiltrosDto;
import lugus.dto.PeliculaChildDto;
import lugus.dto.PeliculaCreateDto;
import lugus.model.Formato;
import lugus.model.Fuente;
import lugus.model.Genero;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.model.PeliculaFoto;
import lugus.repository.PeliculaRepository;
import lugus.repository.PeliculaSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Order;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeliculaService {

	private final PeliculaRepository peliculaRepo;
	private final LocalizacionService locService;
	private final FuenteService fuenteService;

	public List<Pelicula> findAll() {
		return peliculaRepo.findAll();
	}

	public Optional<Pelicula> findById(Integer id) {
		return peliculaRepo.findById(id);
	}

	@Transactional
	public Pelicula crear(PeliculaCreateDto dto, HttpSession session) throws IOException {
		Localizacion loc = null;
		if (dto.getLocalizacionCodigo() != null && !dto.getLocalizacionCodigo().isBlank()) {
			loc = locService.findById(dto.getLocalizacionCodigo())
					.orElseThrow(() -> new IllegalArgumentException("Localización no encontrada"));
		}

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());
		String user = (String) session.getAttribute("usuarioConectado");

		Pelicula p = Pelicula.builder().titulo(dto.getTitulo()).tituloGest(dto.getTituloGest()).anyo(dto.getAnyo())
				.formato(formato).genero(genero).pack(dto.isPack()).steelbook(dto.isSteelbook()).funda(dto.isFunda())
				.comprado(dto.isComprado()).notas(dto.getNotas()).localizacion(loc).usrAlta(user).tsAlta(Instant.now())
				.build();
		p.calcularCodigo();
		Pelicula saved = peliculaRepo.save(p);

		if (dto.getUrl() != null && !dto.getUrl().isEmpty()) {
			final DwFotoServiceI dwFotoService = new DwFotoService();
			Optional<Fuente> fuenteObj = fuenteService.findById(dto.getFuente());
			PeliculaFoto pf = new PeliculaFoto();
			pf.setUrl(dto.getUrl());
			pf.setFuente(fuenteObj.get());
			pf.setFoto(dwFotoService.descargar(dto.getFuente(), dto.getUrl()));
			pf.setCaratula(true);

			saved.addCaratula(pf);
			saved = save(saved);
		}

		return saved;
	}

	@Transactional
	public void delete(Integer id) {
		peliculaRepo.deleteById(id);
	}

	@Transactional
	public Pelicula addChild(Integer padreId, @Valid PeliculaChildDto dto, HttpSession session) throws IOException {
		Pelicula padre = peliculaRepo.findById(padreId)
				.orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));
		Pelicula hijo = add(padre, dto, session);
		hijo.setPadre(padre);
		padre.getPeliculasPack().add(hijo);
		peliculaRepo.save(padre);
		return hijo;
	}

	private Pelicula add(Pelicula padre, PeliculaChildDto dto, HttpSession session) {

		Formato formato = Formato.getById(dto.getFormatoCodigo());
		Genero genero = Genero.getById(dto.getGeneroCodigo());
		String user = (String) session.getAttribute("usuarioConectado");

		Pelicula p = Pelicula.builder().titulo(dto.getTitulo()).tituloGest(dto.getTitulo()).anyo(dto.getAnyo())
				.formato(formato).genero(genero).pack(false).steelbook(padre.isSteelbook()).funda(padre.isFunda())
				.comprado(padre.isComprado()).notas(padre.getNotas()).localizacion(padre.getLocalizacion()).usrAlta(user).tsAlta(Instant.now())
				.build();
		p.calcularCodigo();
		Pelicula saved = peliculaRepo.save(p);

		return saved;
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
		Sort sort;
		if ("compra".equals(campo)) {
			if ("ASC".equalsIgnoreCase(direccion)) {
				sort = Sort.by(Sort.Order.desc("tsModif").with(Sort.NullHandling.NULLS_LAST),
						Sort.Order.desc("tsAlta"));
			} else {
				// Orden ascendente (NULLs al principio)
				sort = Sort.by(Sort.Order.asc("tsModif").with(Sort.NullHandling.NULLS_FIRST), Sort.Order.asc("tsAlta"));
			}
		} else {
			sort = Sort.by(Direction.fromString(direccion), campo);
		}

		Pageable pageable = PageRequest.of(pagina, 30, sort);

		Specification<Pelicula> spec = Specification.where(null);
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

		spec = spec.or(PeliculaSpecification.porActor(filtro.getTexto()));
		spec = spec.or(PeliculaSpecification.porDirector(filtro.getTexto()));
		spec = spec.or(PeliculaSpecification.porTitulo(filtro.getTexto()));

		return peliculaRepo.findAll(spec, pageable);
	}
}
