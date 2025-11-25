package lugus.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
	
	private Boolean tieneCaratula;

	private String genero;

	private String localizacion;

	private String notas;
	
	private String director;
	
	private String actor;

	private int pagina;

	private String orden;

	private String direccion;
	
	private boolean initFilter = true;
	

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();

		if (titulo != null && !texto.isBlank())
			map.put("texto", texto);
		
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
		if(tieneCaratula!=null)
			map.put("tieneCaratula",tieneCaratula);
		
		if (pagina >= 0)
			map.put("pagina", pagina);

		return map;
	}


	/**
	 * @param texto the texto to set
	 */
	public final void setTexto(String texto) {
		this.texto = texto;
		setInitFilter(false);
	}


	/**
	 * @param titulo the titulo to set
	 */
	public final void setTitulo(String titulo) {
		this.titulo = titulo;
		setInitFilter(false);
	}


	/**
	 * @param fromAnyo the fromAnyo to set
	 */
	public final void setFromAnyo(Integer fromAnyo) {
		this.fromAnyo = fromAnyo;
		setInitFilter(false);
	}


	/**
	 * @param toAnyo the toAnyo to set
	 */
	public final void setToAnyo(Integer toAnyo) {
		this.toAnyo = toAnyo;
		setInitFilter(false);
	}


	/**
	 * @param pack the pack to set
	 */
	public final void setPack(Boolean pack) {
		this.pack = pack;
		setInitFilter(false);
	}


	/**
	 * @param formato the formato to set
	 */
	public final void setFormato(Integer formato) {
		this.formato = formato;
		setInitFilter(false);
	}


	/**
	 * @param steelbook the steelbook to set
	 */
	public final void setSteelbook(Boolean steelbook) {
		this.steelbook = steelbook;
		setInitFilter(false);
	}


	/**
	 * @param funda the funda to set
	 */
	public final void setFunda(Boolean funda) {
		this.funda = funda;
		setInitFilter(false);
	}


	/**
	 * @param comprado the comprado to set
	 */
	public final void setComprado(Boolean comprado) {
		this.comprado = comprado;
		setInitFilter(false);
	}


	/**
	 * @param tieneCaratula the tieneCaratula to set
	 */
	public final void setTieneCaratula(Boolean tieneCaratula) {
		this.tieneCaratula = tieneCaratula;
		setInitFilter(false);
	}


	/**
	 * @param genero the genero to set
	 */
	public final void setGenero(String genero) {
		this.genero = genero;
		setInitFilter(false);
	}


	/**
	 * @param localizacion the localizacion to set
	 */
	public final void setLocalizacion(String localizacion) {
		this.localizacion = localizacion;
		setInitFilter(false);
	}


	/**
	 * @param notas the notas to set
	 */
	public final void setNotas(String notas) {
		this.notas = notas;
		setInitFilter(false);
	}


	/**
	 * @param director the director to set
	 */
	public final void setDirector(String director) {
		this.director = director;
		setInitFilter(false);
	}


	/**
	 * @param actor the actor to set
	 */
	public final void setActor(String actor) {
		this.actor = actor;
		setInitFilter(false);
	}


	/**
	 * @param pagina the pagina to set
	 */
	public final void setPagina(int pagina) {
		this.pagina = pagina;
		setInitFilter(false);
	}


	/**
	 * @param orden the orden to set
	 */
	public final void setOrden(String orden) {
		this.orden = orden;
		setInitFilter(false);
	}


	/**
	 * @param direccion the direccion to set
	 */
	public final void setDireccion(String direccion) {
		this.direccion = direccion;
		setInitFilter(false);
	}


	/**
	 * @param initFilter the initFilter to set
	 */
	public final void setInitFilter(boolean initFilter) {
		this.initFilter = initFilter;
	}
	

}
