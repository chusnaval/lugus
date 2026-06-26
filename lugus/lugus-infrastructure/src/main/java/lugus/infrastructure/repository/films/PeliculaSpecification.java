package lugus.infrastructure.repository.films;

import org.springframework.data.jpa.domain.Specification;
import java.time.Instant;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lugus.model.films.Edicion;
import lugus.model.films.Pelicula;
import lugus.model.films.PeliculaFoto;
import lugus.model.people.Actor;
import lugus.model.people.Director;

public class PeliculaSpecification {

	public static Specification<Pelicula> porTitulo(String titulo) {
		return (root, query, cb) -> (titulo == null || titulo.isBlank()) ? null
				: cb.like(cb.lower(root.get("titulo")), "%" + titulo.trim().toLowerCase() + "%");
	}

	public static Specification<Pelicula> porFromAnyo(Integer fromAnyo) {
		return (root, query, cb) -> (fromAnyo == null) ? null : cb.ge(root.get("anyo"), fromAnyo);
	}

	public static Specification<Pelicula> porToAnyo(Integer toAnyo) {
		return (root, query, cb) -> (toAnyo == null) ? null : cb.ge(root.get("anyo"), toAnyo);
	}

	public static Specification<Pelicula> porGenero(String genero) {
		return (root, query, cb) -> (genero == null || genero.isBlank()) ? null : cb.equal(root.get("genero"), genero);
	}

	
	public static Specification<Pelicula> porNotas(String notas) {
	    return (root, query, cb) -> {
	        if (notas == null || notas.isBlank()) return null;

	        query.distinct(true);
	        Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);

	        return cb.like(cb.lower(ed.get("notas")), "%" + notas.toLowerCase() + "%");
	    };
	}

	public static Specification<Pelicula> porPack(Boolean pack) {
	    return (root, query, cb) -> {
	        if (pack == null) return null;

	        query.distinct(true);
	        Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);

	        return pack
	            ? cb.isNotNull(ed.get("pack"))
	            : cb.isNull(ed.get("pack"));
	    };
	}


	public static Specification<Pelicula> porSteelbook(Boolean steelbook) {
	    return (root, query, cb) -> {
	        if (steelbook == null) return null;

	        query.distinct(true);
	        Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);

	        return cb.equal(ed.get("steelbook"), steelbook);
	    };
	}


	public static Specification<Pelicula> porFunda(Boolean funda) {
	    return (root, query, cb) -> {
	        if (funda == null) return null;

	        query.distinct(true);
	        Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);

	        return cb.equal(ed.get("funda"), funda);
	    };
	}

	public static Specification<Pelicula> porComprado(Boolean comprado) {
	    return (root, query, cb) -> {
	        if (comprado == null) return null;

	        query.distinct(true);
	        Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);

	        return cb.equal(ed.get("comprado"), comprado);
	    };
	}


	public static Specification<Pelicula> porFormato(Integer formato) {
	    return (root, query, cb) -> {
	        if (formato == null) return null;

	        query.distinct(true);
	        Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);

	        return cb.equal(ed.get("formato"), formato);
	    };
	}

	
	public static Specification<Pelicula> vigentes() {
		return (root, query, cb) -> {
			
			Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);
			return cb.isNull(ed.get("tsBaja"));
		};
	}

	public static Specification<Pelicula> porLocalizacion(String codigo) {
	    return (root, query, cb) -> {
	        if (codigo == null || codigo.isBlank()) return null;

	        query.distinct(true);
	        Join<Pelicula, Edicion> ed = root.join("editions", JoinType.LEFT);

	        return cb.equal(ed.get("location").get("codigo"), codigo);
	    };
	}

	public static Specification<Pelicula> porActor(String actor) {
		return (root, query, cb) -> {
			if (actor == null || actor.isBlank()) {
				return null;
			}
			query.distinct(true);
			String patron = "%" + actor.trim().toLowerCase() + "%";

			Subquery<Integer> subDir = query.subquery(Integer.class);
			Root<Actor> dirRoot = subDir.from(Actor.class);
			subDir.select(dirRoot.get("pelicula").get("id"));
			Predicate dirNombre = cb.like(cb.lower(dirRoot.get("nombre")), patron);
			subDir.where(dirNombre);

			Predicate enActores = root.get("id").in(subDir);
			return cb.or(enActores);
		};
	}

	public static Specification<Pelicula> porDirector(String director) {
		return (root, query, cb) -> {
			if (director == null || director.isBlank()) {
				return null;
			}

			query.distinct(true);
			String patron = "%" + director.trim().toLowerCase() + "%";

			Subquery<Integer> subDir = query.subquery(Integer.class);
			Root<Director> dirRoot = subDir.from(Director.class);
			subDir.select(dirRoot.get("pelicula").get("id"));
			Predicate dirNombre = cb.like(cb.lower(dirRoot.get("nombre")), patron);
			subDir.where(dirNombre);

			Predicate enDirectores = root.get("id").in(subDir);
			return cb.or(enDirectores);
		};
	}

	
	public static Specification<Pelicula> tieneCaratula(boolean tieneCaratula) {
	    return (root, query, cb) -> {

	        // Subquery para PeliculaFoto
	        Subquery<PeliculaFoto> sub = query.subquery(PeliculaFoto.class);
	        Root<PeliculaFoto> foto = sub.from(PeliculaFoto.class);

	        sub.select(foto)
	           .where(
	               cb.equal(foto.get("pelicula"), root),
	               cb.isTrue(foto.get("caratula"))
	           );

	        if (tieneCaratula) {
	            // Películas que SÍ tienen carátula
	            return cb.exists(sub);
	        } else {
	            // Películas que NO tienen ninguna carátula
	            return cb.not(cb.exists(sub));
	        }
	    };
	}
	
	public static Specification<Pelicula> ordenarPorUltimaCompra() {
	    return (root, query, cb) -> {

	    	Join<Pelicula, Edicion> ed = root.join("ediciones", JoinType.LEFT);

	    	query.groupBy(root.get("id"));

	    	Expression<Instant> ultimaCompra = cb.greatest(ed.get("tsCompra").as(Instant.class));
	    	Expression<Instant> ultimaModif  = cb.greatest(ed.get("tsModif").as(Instant.class));
	    	Expression<Instant> ultimaAlta   = cb.greatest(ed.get("tsAlta").as(Instant.class));

	    	if (query.getResultType() != Long.class) {
	    	    query.orderBy(
	    	        cb.desc(ultimaCompra),
	    	        cb.desc(ultimaModif),
	    	        cb.desc(ultimaAlta)
	    	    );
	    	}

	    	return cb.conjunction();

	    };
	}

}
