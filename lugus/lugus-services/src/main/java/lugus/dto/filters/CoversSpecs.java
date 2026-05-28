package lugus.dto.filters;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lugus.model.core.Source;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;

public class CoversSpecs {

	public static Specification<Pelicula> withFilters(CoversFilter f) {
		return Specification.where(titleContains(f.title())).and(missingCover(f.missing()))
				.and(sourceEquals(f.source()));
	}

	private static Specification<Pelicula> titleContains(String title) {
		return (root, query, cb) -> title == null || title.isBlank() ? null
				: cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
	}

	private static Specification<Pelicula> missingCover(String missing) {
		return (root, query, cb) -> {
			if (missing == null)
				return null;

			Join<Pelicula, PeliculaFoto> fotos = root.join("peliculaFotos", JoinType.LEFT);

			if (missing.equals("yes")) {
				return cb.isNull(fotos.get("id"));
			} else {
				return cb.isNotNull(fotos.get("id"));
			}
		};
	}

	private static Specification<Pelicula> sourceEquals(String source) {
		return (root, query, cb) -> {
			if (source == null || source.isBlank())
				return null;

			Join<Pelicula, PeliculaFoto> fotos = root.join("peliculaFotos", JoinType.LEFT);
			Join<PeliculaFoto, Source> sourceJoin = fotos.join("source", JoinType.LEFT);
			return cb.equal(sourceJoin.get("id"), Integer.valueOf(source));
		};
	}
}
