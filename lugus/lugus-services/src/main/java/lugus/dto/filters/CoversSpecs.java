package lugus.dto.filters;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lugus.model.core.Source;
import lugus.model.films.Film;
import lugus.model.films.FilmImage;

public class CoversSpecs {

	public static Specification<Film> withFilters(CoversFilter f) {
		return Specification.where(titleContains(f.title())).and(missingCover(f.missing()))
				.and(sourceEquals(f.source()));
	}

	private static Specification<Film> titleContains(String title) {
		return (root, query, cb) -> title == null || title.isBlank() ? null
				: cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
	}

	private static Specification<Film> missingCover(String missing) {
		return (root, query, cb) -> {
			if (missing == null)
				return null;

			Join<Film, FilmImage> fotos = root.join("peliculaFotos", JoinType.LEFT);

			if (missing.equals("yes")) {
				return cb.isNull(fotos.get("id"));
			} else {
				return cb.isNotNull(fotos.get("id"));
			}
		};
	}

	private static Specification<Film> sourceEquals(String source) {
		return (root, query, cb) -> {
			if (source == null || source.isBlank())
				return null;

			Join<Film, FilmImage> fotos = root.join("peliculaFotos", JoinType.LEFT);
			Join<FilmImage, Source> sourceJoin = fotos.join("source", JoinType.LEFT);
			return cb.equal(sourceJoin.get("id"), Integer.valueOf(source));
		};
	}
}
