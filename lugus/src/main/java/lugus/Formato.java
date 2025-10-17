package lugus;

import lombok.Getter;

public enum Formato {

	VHS((short) 0), DVD((short)1), BLURAY((short)2), ULTRAHD((short)3);
	
	@Getter
	private short id;
	
	/**
	 * Constructor por defecto
	 * @param i
	 */
	private Formato(short i) {
		this.id = i;
	}
	
	
}
