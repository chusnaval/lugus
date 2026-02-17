package lugus.model.values;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public enum Formato {

	VHS((short) 0), DVD((short) 1), BLURAY((short) 2), ULTRAHD((short) 3), DIGITAL((short) 4);

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
	
	public static List<Formato> valoresOrdenados() {
		List<Formato> lista = new ArrayList<>();
		lista.add(DVD);
		lista.add(BLURAY);
		lista.add(ULTRAHD);
		lista.add(DIGITAL);
		lista.add(VHS);
		return lista;
	}

	/**
	 * Los DVDs van en carpetas y el resto juntos en estanterias
	 * @return
	 */
	public int getIdParaUbicaciones() {
		if(this.id>2) {
			return 2;
		}
		return id;
	}
}
