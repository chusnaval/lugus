package lugus;

import lombok.Getter;

public enum Categoria {

	ARTE_ENTRETENIMIENTO(1, "AMARILLO"), LITERATURA_NARRATIVA(2, "AZUL"), CIENCIA_FICCION(3, "PLATEADO"),
	ACCION(4, "ROJO"), MISTERIO(5, "MORADO"), TERROR(6, "NEGRO"), CONFLICTO(7, "NARANJA"), DOCUMENTAL(8, "VERDE");

	@Getter
	private int codigo;

	@Getter
	private String color;

	private Categoria(int codigo, String color) {
		this.codigo = codigo;
		this.color = color;
	}
}
