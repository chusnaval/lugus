package lugus.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltrosDto {

	private String titulo;

	private Integer fromAnyo;

	private Integer toAnyo;

	private Boolean pack;

	private Integer formato;

	private Boolean steelbook;

	private Boolean funda;

	private Boolean comprado;
	
	private Boolean tieneCaratula;

	private String genero;

	private String localizacion;

	private String notas;

	private int pagina;

	private String orden;

	private String direccion;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();

		if (titulo != null && !titulo.isBlank())
			map.put("titulo", titulo);
		if (genero != null && !genero.isBlank())
			map.put("genero", genero);
		if (localizacion != null && !localizacion.isBlank())
			map.put("localizacion", localizacion);
		if (notas != null && !notas.isBlank())
			map.put("notas", notas);
		if (orden != null && !orden.isBlank())
			map.put("orden", orden);
		if (direccion != null && !direccion.isBlank())
			map.put("direccion", direccion);

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
		if(tieneCaratula!=null)
			map.put("tieneCaratula",tieneCaratula);
		
		if (pagina >= 0)
			map.put("pagina", pagina);

		return map;
	}

}
