package lugus.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PeliculaCreateDto {

	@NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotNull
    @Min(1888) @Max(2100)
    private Integer anyo;

    @NotNull(message = "El formato es obligatorio")
    private Short formatoCodigo;

	@NotNull(message = "El género es obligatorio")
    private String generoCodigo;        

    // localizacion es opcional 
    private String localizacionCodigo;    
    
	private boolean steelbook;

	private boolean funda;
	
	private boolean comprado;
	
	private String notas;
}