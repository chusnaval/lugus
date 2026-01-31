package lugus.dto.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltrosDto {

	private String texto;

	private String titulo;

	private Integer fromAnyo;

	private Integer toAnyo;

	private Boolean pack;

	private Integer formato;

	private Boolean steelbook;

	private Boolean funda;

	private Boolean comprado;
	
	private Boolean completa;

	private Boolean tieneCaratula;

	private String genero;

	private String location;

	private String notas;

	private String director;

	private String actor;

	private Optional<Integer> pagina = Optional.of(0);

	private Optional<String> orden = Optional.empty();

	private Optional<String> direccion = Optional.empty();

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();

		if (titulo != null && !texto.isBlank())
			map.put("texto", texto);

		if (titulo != null && !titulo.isBlank())
			map.put("titulo", titulo);
		if (genero != null && !genero.isBlank())
			map.put("genero", genero);
		if (location != null && !location.isBlank())
			map.put("location", location);
		if (notas != null && !notas.isBlank())
			map.put("notas", notas);
		if (orden.isPresent() && !orden.get().isBlank())
			map.put("orden", orden);
		if (direccion.isPresent() && !direccion.get().isBlank())
			map.put("direccion", direccion);

		if (director != null && !director.isBlank())
			map.put("director", director);

		if (actor != null && !actor.isBlank())
			map.put("actor", actor);

		if (fromAnyo != null)
			map.put("fromAnyo", fromAnyo);
		if (toAnyo != null)
			map.put("toAnyo", toAnyo);
		if (formato != null)
			map.put("formato", formato);
		if (pack != null)
			map.put("pack", pack);
		if (steelbook != null)
			map.put("steelbook", steelbook);
		if (funda != null)
			map.put("funda", funda);
		if (comprado != null)
			map.put("comprado", comprado);
		if (completa != null)
			map.put("completa", completa);
		if (tieneCaratula != null)
			map.put("tieneCaratula", tieneCaratula);

		if (pagina.get() >= 0)
			map.put("pagina", pagina);

		return map;
	}

}
