package lugus.dto.core;


import lombok.Data;

@Data
public class LocationDTO {

	private String codigo;

	private String descripcion;
	
	private LocationTypeDTO locationType;
	
	private int count;
	
}
