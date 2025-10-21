package lugus.model;

import lombok.Getter;

public enum Formato {

	VHS((short) 0), DVD((short) 1), BLURAY((short) 2), ULTRAHD((short) 3);

	@Getter
	private short id;

	/**
	 * Constructor por defecto
	 * 
	 * @param i
	 */
	private Formato(short i) {
		this.id = i;
	}

	/**
	 * Obtenemos el equivalente
	 * @param i
	 * @return
	 */
	public static Formato getById(short i) {
		for(Formato aux : values()) {
			if(aux.getId() == i) {
				return aux;
			}
		}
		
		throw new IllegalArgumentException("No existe el formato");
	}
}
