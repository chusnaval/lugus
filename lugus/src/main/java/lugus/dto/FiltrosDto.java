package lugus.dto;

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
	
	private String genero;
	
	private String localizacion;

	private String notas;

	private int pagina;

	private String orden;

	private String direccion;

}
