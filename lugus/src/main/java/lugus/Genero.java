package lugus;

import lombok.Getter;

public enum Genero {

	ANIMACION("ANI", Categoria.ARTE_ENTRETENIMIENTO, 1), ANIME("ANM", Categoria.ARTE_ENTRETENIMIENTO, 2),
	INFANTIL("INF", Categoria.ARTE_ENTRETENIMIENTO, 3), MUSICAL("MUS", Categoria.ARTE_ENTRETENIMIENTO, 4),
	NAVIDENA("NAV", Categoria.ARTE_ENTRETENIMIENTO, 5), DRAMA("DRA", Categoria.LITERATURA_NARRATIVA, 1),
	ROMANTICA("DRA", Categoria.LITERATURA_NARRATIVA, 2), COMEDIA("COM", Categoria.LITERATURA_NARRATIVA, 3),
	CIENCIA_FICCION("CIF", Categoria.CIENCIA_FICCION, 1), ACCION("ACC", Categoria.ACCION, 1),
	AVENTURA("AVE", Categoria.ACCION, 2), FANTASIA("FAN", Categoria.ACCION, 3), THRILLER("THR", Categoria.MISTERIO, 1),
	MISTERIO("MIS", Categoria.MISTERIO, 2), CRIMEN("CRI", Categoria.MISTERIO, 3), TERROR("TER", Categoria.TERROR, 1),
	BELICO("BEL", Categoria.CONFLICTO, 1), WESTERN("WES", Categoria.CONFLICTO, 2),
	DOCUMENTAL("DOC", Categoria.DOCUMENTAL, 1), DEPORTES("DEP", Categoria.DOCUMENTAL, 2);

	// Codigo usado identificarlo
	@Getter
	private String codigo;

	// Indica la categoria a la que pertenece
	@Getter
	private Categoria categoria;

	// orden dentro de la categoria
	@Getter
	private int orden;

	private Genero(String codigo, Categoria categoria, int orden) {
		this.codigo = codigo;
		this.categoria = categoria;
		this.orden = orden;
	}
}
