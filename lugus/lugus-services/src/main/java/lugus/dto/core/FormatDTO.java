package lugus.dto.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FormatDTO {

	private String codigo;

	private String descripcion;

	public FormatDTO(String codigo) {
		super();
		this.codigo = codigo;
	}
	
}
