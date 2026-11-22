package lugus.model.values;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

public enum Genere {

	ANIMACION("ANI", Category.ARTE_ENTRETENIMIENTO, 1), ANIME("ANM", Category.ARTE_ENTRETENIMIENTO, 2),
	INFANTIL("INF", Category.ARTE_ENTRETENIMIENTO, 3), MUSICAL("MUS", Category.ARTE_ENTRETENIMIENTO, 4),
	NAVIDENA("NAV", Category.ARTE_ENTRETENIMIENTO, 5), DRAMA("DRA", Category.LITERATURA_NARRATIVA, 1),
	ROMANTICA("ROM", Category.LITERATURA_NARRATIVA, 2), COMEDIA("COM", Category.LITERATURA_NARRATIVA, 3),
	CIENCIA_FICCION("CIF", Category.CIENCIA_FICCION, 1), ACCION("ACC", Category.ACCION, 1),
	AVENTURA("AVE", Category.ACCION, 2), FANTASIA("FAN", Category.ACCION, 3), THRILLER("THR", Category.MISTERIO, 1),
	MISTERIO("MIS", Category.MISTERIO, 2), CRIMEN("CRI", Category.MISTERIO, 3), TERROR("TER", Category.TERROR, 1),
	BELICO("BEL", Category.CONFLICTO, 1), WESTERN("WES", Category.CONFLICTO, 2),
	DOCUMENTAL("DOC", Category.DOCUMENTAL, 1), DEPORTES("DEP", Category.DOCUMENTAL, 2);

	// Codigo usado identificarlo
	@Getter
	private String codigo;

	// Indica la categoria a la que pertenece
	@Getter
	private Category categoria;

	// orden dentro de la categoria
	@Getter
	private int orden;

	private Genere(String codigo, Category categoria, int orden) {
		this.codigo = codigo;
		this.categoria = categoria;
		this.orden = orden;
	}

	/**
	 * Obtenemos el equivalente
	 * 
	 * @param i
	 * @return
	 */
	public static Genere getById(String code) {
		for (Genere aux : values()) {
			if (aux.getCodigo().equals(code)) {
				return aux;
			}
		}

		throw new IllegalArgumentException("No existe el genero: " + code);
	}

	public String getDisplayName() {
		return Arrays.stream(this.name().toLowerCase().split("_"))
				.map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
	}

	public static List<Genere> valoresOrdenados() {
		return Arrays.stream(Genere.values()).sorted(Comparator.comparing(Genere::getDisplayName))
				.collect(Collectors.toList());

	}

}
