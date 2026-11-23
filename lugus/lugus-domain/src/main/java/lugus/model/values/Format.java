package lugus.model.values;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public enum Format{

	VHS((short) 0), DVD((short) 1), BLURAY((short) 2), ULTRAHD((short) 3), DIGITAL((short) 4);

	@Getter
	private short id;

	/**
	 * Constructor por defecto
	 * 
	 * @param i
	 */
	private Format(final short i) {
		this.id = i;
	}

	/**
	 * Obtenemos el equivalente
	 * 
	 * @param i
	 * @return
	 */
	public static Format getById(final short i) {
		for (Format aux : values()) {
			if (aux.getId() == i) {
				return aux;
			}
		}

		throw new IllegalArgumentException("Not recognized format");
	}
	
	/**
	 * Obtenemos el equivalente
	 * 
	 * @param i
	 * @return
	 */
	public static Format getByName(final String value) {
		for (Format aux : values()) {
			if (aux.name().equalsIgnoreCase(value)) {
				return aux;
			}
		}

		throw new IllegalArgumentException("Not recognized format");
	}

	public static List<Format> orderedValues() {
		List<Format> lista = new ArrayList<>();
		lista.add(DVD);
		lista.add(BLURAY);
		lista.add(ULTRAHD);
		lista.add(DIGITAL);
		lista.add(VHS);
		return lista;
	}

	/**
	 * Los DVDs van en carpetas y el resto juntos en estanterias
	 * 
	 * @return
	 */
	public int getIdToLocations() {
		if(this.id == 0) {
			return 2;
		}else if (this.id == 1) {
			return 1;
		} else	if (this.id == 2) {
			return 2;
		}else if (this.id == 3) {
			return 2;
		} else if (this.id == 4) {
			return 3;
		}
		return id;
	}

	public static List<Format> prioritizedFormats() {
		List<Format> lista = new ArrayList<>();
		lista.add(ULTRAHD);
		lista.add(BLURAY);
		lista.add(DVD);
		lista.add(DIGITAL);
		lista.add(VHS);
		return lista;
	}

	/*
	 * 1 "Carpetas" 2 "Estanterias" 3 "Otros" 4 "Cajas"
	 */
}
