package lugus.service.series;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lugus.dto.core.FilterDto;
import lugus.dto.films.SerieDto;
import lugus.dto.series.SeasonDto;
import lugus.infrastructure.repository.series.SeriesSpecification;
import lugus.model.core.Location;
import lugus.model.core.Source;
import lugus.model.series.Season;
import lugus.model.series.Series;
import lugus.model.series.SeriesImage;
import lugus.model.values.Format;
import lugus.model.values.Genre;
import lugus.model.values.LangVersion;
import lugus.repository.series.SerieRepository;
import lugus.service.core.LocationService;
import lugus.service.core.SourceService;
import lugus.service.films.DwImageService;
import lugus.service.films.DwImageServiceI;

@Service
@RequiredArgsConstructor
public class SeriesService {

	private static final int NUM_ELEMENTS_PER_HOME = 5;
	private final SerieRepository seriesRepo;
	private final LocationService locService;
	private final SourceService sourceService;

	public List<Series> findAll() {
		return seriesRepo.findAll();
	}

	public Optional<Series> findById(Integer id) {
		return seriesRepo.findById(id);
	}

	/**
	 * Save films
	 * 
	 * @param films
	 * @return
	 */
	public Series save(Series film) {
		return seriesRepo.save(film);

	}

	/**
	 * Complete movies search with all filters available
	 * 
	 * @param filter
	 * @param page
	 * @param field
	 * @param direction
	 * @return
	 */
	public Page<Series> findAllBy(FilterDto filter) {
		Sort sort = buildSort(filter.getOrden(), filter.getDireccion());

		Pageable pageable = PageRequest.of(filter.getPagina().get(), filter.getPageSize(), sort);

		Specification<Series> spec = Specification.where(null);

		spec = spec.and(SeriesSpecification.byPurchased(filter.getComprado()));
		spec = spec.and(SeriesSpecification.byFormat(filter.getFormato()));
		spec = spec.and(SeriesSpecification.byGenre(filter.getGenero()));
		spec = spec.and(SeriesSpecification.byLocation(filter.getLocation()));
		spec = spec.and(SeriesSpecification.byNotes(filter.getNotas()));
		spec = spec.and(SeriesSpecification.byTitle(filter.getTitulo()));
		spec = spec.and(SeriesSpecification.byComplete(filter.getCompleta()));

		return seriesRepo.findAll(spec, pageable);
	}

	/**
	 * Complete movies search with all filters available
	 * 
	 * @param filter
	 * @param page
	 * @param field
	 * @param direction
	 * @return
	 */
	public Page<Series> wanted() {
		Sort sort = Sort.by(Direction.fromString("ASC"), "mgmtTitle");

		Pageable pageable = PageRequest.of(0, 1000, sort);

		Specification<Series> spec = Specification.where(null);

		spec = spec.or(SeriesSpecification.byPurchased(false));
		spec = spec.or(SeriesSpecification.byComplete(false));

		return seriesRepo.findAll(spec, pageable);
	}

	private Sort buildSort(Optional<String> field, Optional<String> direction) {
		Sort sort;
		if (field.isPresent() && "compra".equals(field.get())) {
			if (direction.isPresent() && "ASC".equalsIgnoreCase(direction.get())) {
				sort = Sort.by(Sort.Order.desc("tsModif").with(Sort.DEFAULT_DIRECTION),
						Sort.Order.desc("tsCreation"));
			} else {

				sort = Sort.by(Sort.Order.asc("tsModif").with(Sort.DEFAULT_DIRECTION), Sort.Order.asc("tsCreation"));
			}
		} else {
			sort = Sort.by(Direction.fromString(direction.orElse("ASC")), field.orElse("mgmtTitle"));
		}
		return sort;
	}

	@Transactional
	public Series update(Integer id, SerieDto dto) throws IOException, URISyntaxException {

		Series p = seriesRepo.findById(id).orElseThrow(() -> new RuntimeException("Pelicula no encontrada"));
		Optional<Location> loc = locService.findById(dto.getLocation());

		p.setTitle(dto.getTitle());
		p.setMgmtTitle(dto.getTitleMgmt());
		p.setStartYear(dto.getStartYear());
		p.setEndYear(dto.getFinishYear());
		p.setTsModif(Instant.now());
		if (dto.getFormat() != null) {
			p.setFormat(Format.getById(Short.valueOf(dto.getFormat())));
		}
		p.setGenre(Genre.getById(dto.getGenreCode()));
		if (loc.isPresent()) {
			p.setLocation(loc.get());
		}
		p.setCode(dto.getMgmtCode());
		p.setPurchased(dto.isOwned());
		p.setNotes(dto.getNotes());
		addCaratula(p, dto.getCoverSrc());
		updateSeasons(p, dto.getSeasons());
		seriesRepo.save(p);

		return p;
	}
	
	private void updateSeasons(Series serie, List<SeasonDto> seasonDtos) {

	    // 1) clear actual seasons (orphanRemoval)
	    serie.getSeasons().clear();
	    
	    seriesRepo.flush();

	    // 2) add new seasons
	    for (SeasonDto dto : seasonDtos) {
	        Season s = new Season();
	        s.setOrder(dto.getOrdinal());
	        s.setDesc(dto.getDesc());
	        s.setPurchased(dto.isPurchased());
	        s.setWanted(dto.isWanted());
	        s.setPublishedVersion(LangVersion.VE);
	        s.setPurchasedVersion(LangVersion.VE);
	        s.setSeries(serie);

	        serie.getSeasons().add(s);
	    }
	}


	public void addCaratula(Series serie, String url) throws IOException, URISyntaxException {

		final DwImageServiceI dwFotoService = new DwImageService();
		Optional<Source> sourceObj = sourceService.findById(sourceService.calcularIdSource(url));
		if (sourceObj.isPresent() && url!=null) {
			SeriesImage pf = new SeriesImage();
			pf.setUrl(url);
			pf.setSource(sourceObj.get());
			pf.setContent(dwFotoService.download(sourceObj.get().getId(), url));
			pf.setCover(true);

			serie.getImages().clear();
			serie.addCover(pf);
		}
	}

	public Page<Series> findForHome() {
		Sort sort = buildSort(Optional.of("compra"), Optional.of("ASC"));

		Pageable pageable = PageRequest.of(0, NUM_ELEMENTS_PER_HOME, sort);

		Specification<Series> spec = Specification.where(null);

		spec = spec.and(SeriesSpecification.byPurchased(true));

		return seriesRepo.findAll(spec, pageable);
	}

	public List<Series> findByTitlesInYear(String title, Integer year) {
		List<Series> result = new ArrayList<>();
		result.addAll(seriesRepo.findByTitleAndStartYear(title, year));
		result.addAll(seriesRepo.findByMgmtTitleAndStartYear(title, year));

		return result;
	}

	public List<Series> findAllOrdered() {
		return seriesRepo.findAllByOrderByMgmtTitleAscStartYearAsc();
	}

	public long contarTodas() {
		return seriesRepo.count();
	}

	public int addedInLastDays(int days) {
		Instant limit = Instant.now().minus(days, ChronoUnit.DAYS);
		return seriesRepo.countByTsCreationAfter(limit);
	}

	public int countByPurchased(boolean value) {
		return seriesRepo.countByPurchased(value);
	}

	public boolean isFilmRegistered(String tconst) {
		return findByImdbId(tconst).isPresent();
	}

	public Optional<Series> findByImdbId(String tconst) {
		return seriesRepo.findByImdbId(tconst);
	}

}
