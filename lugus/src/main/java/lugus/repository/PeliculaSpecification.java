package lugus.repository;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lugus.model.Actor;
import lugus.model.Director;
import lugus.model.Localizacion;
import lugus.model.Pelicula;

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

	public static Specification<Pelicula> porPack(Boolean pack) {
		return (root, query, cb) -> (pack == null) ? null : cb.equal(root.get("pack"), pack);
	}

	public static Specification<Pelicula> porSteelbook(Boolean steelbook) {
		return (root, query, cb) -> (steelbook == null) ? null : cb.equal(root.get("steelbook"), steelbook);
	}

	public static Specification<Pelicula> porFunda(Boolean funda) {
		return (root, query, cb) -> (funda == null) ? null : cb.equal(root.get("funda"), funda);
	}

	public static Specification<Pelicula> porComprado(Boolean comprado) {
		return (root, query, cb) -> (comprado == null) ? null : cb.equal(root.get("comprado"), comprado);
	}

	public static Specification<Pelicula> porFormato(Integer formato) {
		return (root, query, cb) -> (formato == null) ? null : cb.equal(root.get("formato"), formato);
	}

	public static Specification<Pelicula> porGenero(String genero) {
		return (root, query, cb) -> (genero == null || genero.isBlank()) ? null : cb.equal(root.get("genero"), genero);
	}

	public static Specification<Pelicula> porNotas(String notas) {
		return (root, query, cb) -> (notas == null || notas.isBlank()) ? null
				: cb.like(cb.lower(root.get("notas")), "%" + notas.toLowerCase() + "%");
	}

	public static Specification<Pelicula> porLocalizacion(String localizacion) {
		return (root, query, cb) -> {
			if (localizacion == null || localizacion.isBlank()) {
				return null;
			}

			Join<Pelicula, Localizacion> generoJoin = root.join("localizacion", JoinType.INNER);
			return cb.equal(generoJoin.get("localizacion"), localizacion);
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
}
