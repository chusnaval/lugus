package lugus;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Caratula {
	private String titulo;
	private String id;
	private String url;
	private int pelicula_id;
}
