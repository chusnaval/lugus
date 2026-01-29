package lugus.repository.series;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lugus.model.core.Location;
import lugus.model.series.Serie;

public class SerieSpecification {

	public static Specification<Serie> porTitulo(String titulo) {
		return (root, query, cb) -> (titulo == null || titulo.isBlank()) ? null
				: cb.like(cb.lower(root.get("titulo")), "%" + titulo.trim().toLowerCase() + "%");
	}


	public static Specification<Serie> porComprado(Boolean comprado) {
		return (root, query, cb) -> (comprado == null) ? null : cb.equal(root.get("comprado"), comprado);
	}

	public static Specification<Serie> porFormato(Integer formato) {
		return (root, query, cb) -> (formato == null) ? null : cb.equal(root.get("formato"), formato);
	}

	public static Specification<Serie> porGenero(String genero) {
		return (root, query, cb) -> (genero == null || genero.isBlank()) ? null : cb.equal(root.get("genero"), genero);
	}

	public static Specification<Serie> porNotas(String notas) {
		return (root, query, cb) -> (notas == null || notas.isBlank()) ? null
				: cb.like(cb.lower(root.get("notas")), "%" + notas.toLowerCase() + "%");
	}

	public static Specification<Serie> byLocation(String location) {
		return (root, query, cb) -> {
			if (location == null || location.isBlank()) {
				return null;
			}

			Join<Serie, Location> generoJoin = root.join("localizacion", JoinType.INNER);
			return cb.equal(generoJoin.get("localizacion"), location);
		};
	}


	
}
