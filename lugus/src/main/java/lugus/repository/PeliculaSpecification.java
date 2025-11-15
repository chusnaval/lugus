package lugus.repository;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lugus.model.Localizacion;
import lugus.model.Pelicula;
import lugus.model.PeliculaFoto;

public class PeliculaSpecification {

	public static Specification<Pelicula> porTitulo(String titulo) {
		return (root, query, cb) -> (titulo == null || titulo.isBlank()) ? null
				: cb.like(cb.lower(root.get("titulo")), "%" + titulo.toLowerCase() + "%");
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

	public static Specification<Pelicula> porTieneCaratula(Boolean tieneCaratula) {
		return (root, query, cb) -> {
			if (tieneCaratula == null) {
				return null;
			}

			Join<Pelicula, PeliculaFoto> fotosJoin = root.join("peliculaFotos", JoinType.LEFT);
			return cb.isNull(fotosJoin.get("id"));
		};
	}
}
