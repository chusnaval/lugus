package lugus.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerieCreateDto {

	private int id;

	@NotBlank(message = "El título es obligatorio")
	private String titulo;

	@NotBlank(message = "El título es obligatorio")
	private String tituloGest;

	@NotNull
	@Min(1888)
	@Max(2100)
	private Integer anyoInicio;

	@Min(1888)
	@Max(2100)
	private Integer anyoFin;

	@NotNull(message = "El formato es obligatorio")
	private Short formatoCodigo;

	@NotNull(message = "El género es obligatorio")
	private String generoCodigo;

	// location is optional
	private String locationCode;

	private boolean comprado;
	
	private boolean completa;

	private String notas;

	private String url;

	private Integer source;
}