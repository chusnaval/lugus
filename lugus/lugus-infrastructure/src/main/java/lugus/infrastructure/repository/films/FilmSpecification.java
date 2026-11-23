package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.domain.Specification;
import java.time.Instant;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lugus.model.films.Edition;
import lugus.model.films.Film;
import lugus.model.films.FilmImage;
import lugus.model.people.Actor;
import lugus.model.people.Director;

public class FilmSpecification {

	private static final String EDITIONS = "editions";

	public static Specification<Film> byTitle(final String title) {
		return (root, query, cb) -> (title == null || title.isBlank()) ? null
				: cb.like(cb.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");
	}

	public static Specification<Film> byFromYear(final Integer fromYear) {
		return (root, query, cb) -> (fromYear == null) ? null : cb.ge(root.get("year"), fromYear);
	}

	public static Specification<Film> byToYear(final Integer toAnyo) {
		return (root, query, cb) -> (toAnyo == null) ? null : cb.ge(root.get("year"), toAnyo);
	}

	public static Specification<Film> byGenre(final String genre) {
		return (root, query, cb) -> (genre == null || genre.isBlank()) ? null : cb.equal(root.get("genre"), genre);
	}

	
	public static Specification<Film> byNotes(final String notes) {
	    return (root, query, cb) -> {
	        if (notes == null || notes.isBlank()) return null;

	        query.distinct(true);
	        Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	        return cb.like(cb.lower(ed.get("notes")), "%" + notes.toLowerCase() + "%");
	    };
	}

	public static Specification<Film> byPack(final Boolean pack) {
	    return (root, query, cb) -> {
	        if (pack == null) return null;

	        query.distinct(true);
	        Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	        return pack
	            ? cb.isNotNull(ed.get("pack"))
	            : cb.isNull(ed.get("pack"));
	    };
	}


	public static Specification<Film> bySteelbook(final Boolean steelbook) {
	    return (root, query, cb) -> {
	        if (steelbook == null) return null;

	        query.distinct(true);
	        Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	        return cb.equal(ed.get("steelbook"), steelbook);
	    };
	}


	public static Specification<Film> bySlipCover(final Boolean slipCover) {
	    return (root, query, cb) -> {
	        if (slipCover == null) return null;

	        query.distinct(true);
	        Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	        return cb.equal(ed.get("slipCover"), slipCover);
	    };
	}

	public static Specification<Film> byPurchased(final Boolean purchased) {
	    return (root, query, cb) -> {
	    	query.distinct(true);
	    	Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	    	if (purchased == null) {
	        	return null;
	        }

	        return cb.equal(ed.get("purchased"), purchased);
	    };
	}


	public static Specification<Film> byFormat(final Integer format) {
	    return (root, query, cb) -> {
	        if (format == null) return null;

	        query.distinct(true);
	        Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	        return cb.equal(ed.get("format"), format);
	    };
	}

	
	public static Specification<Film> vigentes() {
		return (root, query, cb) -> {
			
			Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);
			return cb.isNull(ed.get("tsDeletion"));
		};
	}

	public static Specification<Film> byLocation(final String code) {
	    return (root, query, cb) -> {
	        if (code == null || code.isBlank()) return null;

	        query.distinct(true);
	        Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	        return cb.equal(ed.get("location").get("code"), code);
	    };
	}

	public static Specification<Film> byActor(final String actor) {
		return (root, query, cb) -> {
			if (actor == null || actor.isBlank()) {
				return null;
			}
			query.distinct(true);
			String patron = "%" + actor.trim().toLowerCase() + "%";

			Subquery<Integer> subDir = query.subquery(Integer.class);
			Root<Actor> dirRoot = subDir.from(Actor.class);
			subDir.select(dirRoot.get("film").get("id"));
			Predicate dirNombre = cb.like(cb.lower(dirRoot.get("name")), patron);
			subDir.where(dirNombre);

			Predicate enActores = root.get("id").in(subDir);
			return cb.or(enActores);
		};
	}

	public static Specification<Film> byDirector(final String director) {
		return (root, query, cb) -> {
			if (director == null || director.isBlank()) {
				return null;
			}

			query.distinct(true);
			String patron = "%" + director.trim().toLowerCase() + "%";

			Subquery<Integer> subDir = query.subquery(Integer.class);
			Root<Director> dirRoot = subDir.from(Director.class);
			subDir.select(dirRoot.get("film").get("id"));
			Predicate dirNombre = cb.like(cb.lower(dirRoot.get("name")), patron);
			subDir.where(dirNombre);

			Predicate enDirectores = root.get("id").in(subDir);
			return cb.or(enDirectores);
		};
	}

	
	public static Specification<Film> hasCover(final boolean hasCover) {
	    return (root, query, cb) -> {

	        // Subquery para PeliculaFoto
	        Subquery<FilmImage> sub = query.subquery(FilmImage.class);
	        Root<FilmImage> image = sub.from(FilmImage.class);

	        sub.select(image)
	           .where(
	               cb.equal(image.get("film"), root),
	               cb.isTrue(image.get("cover"))
	           );

	        if (hasCover) {
	            // Películas que SÍ tienen carátula
	            return cb.exists(sub);
	        } else {
	            // Películas que NO tienen ninguna carátula
	            return cb.not(cb.exists(sub));
	        }
	    };
	}
	
	public static Specification<Film> orderbyLastPurchase() {
	    return (root, query, cb) -> {

	    	Join<Film, Edition> ed = root.join(EDITIONS, JoinType.LEFT);

	    	query.groupBy(root.get("id"));

	    	Expression<Instant> lastPurchase = cb.greatest(ed.get("tsPurchase").as(Instant.class));
	    	Expression<Instant> lastModif  = cb.greatest(ed.get("tsModif").as(Instant.class));
	    	Expression<Instant> lastCreation   = cb.greatest(ed.get("tsCreation").as(Instant.class));

	    	if (query.getResultType() != Long.class) {
	    	    query.orderBy(
	    	        cb.desc(lastPurchase),
	    	        cb.desc(lastModif),
	    	        cb.desc(lastCreation)
	    	    );
	    	}

	    	return cb.conjunction();

	    };
	}

}
