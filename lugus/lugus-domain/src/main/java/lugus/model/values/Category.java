package lugus.model.values;

import lombok.Getter;

public enum Category {

	ARTE_ENTRETENIMIENTO(1, "AMARILLO"), LITERATURA_NARRATIVA(2, "AZUL"), CIENCIA_FICCION(3, "PLATEADO"),
	ACCION(4, "ROJO"), MISTERIO(5, "MORADO"), TERROR(6, "NEGRO"), CONFLICTO(7, "NARANJA"), DOCUMENTAL(8, "VERDE");

	@Getter
	private int code;

	@Getter
	private String color;

	private Category(int codigo, String color) {
		this.code = codigo;
		this.color = color;
	}
}
