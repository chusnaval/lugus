package lugus.dto.core;

import lombok.Data;

@Data
public class LocationTypeDTO {
	
	private int id;

	private String description;
	
	private int count;
	
	public LocationTypeDTO(int id2, String description2, int size) {
		this.id = id2;
		this.description = description2;
		this.count = size;
	}

	public LocationTypeDTO() {
	}
}
