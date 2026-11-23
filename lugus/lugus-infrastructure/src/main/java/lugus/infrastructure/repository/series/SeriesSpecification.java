package lugus.infrastructure.repository.series;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lugus.model.core.Location;
import lugus.model.series.Series;

public class SeriesSpecification {

	public static Specification<Series> byTitle(final String title) {
		return (root, query, cb) -> (title == null || title.isBlank()) ? null
				: cb.like(cb.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");
	}


	public static Specification<Series> byPurchased(final Boolean purchased) {
		return (root, query, cb) -> (purchased == null) ? null : cb.equal(root.get("purchased"), purchased);
	}
	
	public static Specification<Series> byComplete(final Boolean complete) {
		return (root, query, cb) -> (complete == null) ? null : cb.equal(root.get("complete"), complete);
	}


	public static Specification<Series> byFormat(final Integer format) {
		return (root, query, cb) -> (format == null) ? null : cb.equal(root.get("format"), format);
	}

	public static Specification<Series> byGenre(final String genre) {
		return (root, query, cb) -> (genre == null || genre.isBlank()) ? null : cb.equal(root.get("genre"), genre);
	}

	public static Specification<Series> byNotes(final String notes) {
		return (root, query, cb) -> (notes == null || notes.isBlank()) ? null
				: cb.like(cb.lower(root.get("notes")), "%" + notes.toLowerCase() + "%");
	}

	public static Specification<Series> byLocation(final String location) {
		return (root, query, cb) -> {
			if (location == null || location.isBlank()) {
				return null;
			}

			Join<Series, Location> generoJoin = root.join("location", JoinType.INNER);
			return cb.equal(generoJoin.get("location"), location);
		};
	}


	
}
