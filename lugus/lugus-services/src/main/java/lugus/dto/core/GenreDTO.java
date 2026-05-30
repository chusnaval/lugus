package lugus.dto.core;

import lombok.Data;


@Data
public class GenreDTO {
	
	private String codigo;

	private String descripcion;
	
	public GenreDTO(String codigo, String descripcion) {
		this.codigo = codigo;
		this.descripcion = descripcion;
	}
	
	public GenreDTO() {
	}
	
	public GenreDTO(String codigo) {
		this.codigo = codigo;
	}
}
