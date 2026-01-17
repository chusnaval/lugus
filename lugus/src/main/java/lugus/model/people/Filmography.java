package lugus.model.people;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Filmography {

	private int id;

	private String nconst;

	private Integer peliculaId;

	private String title;

	private Integer startyear;

	private String category;

	private String job;

	private String tconst;

	private String fcharacters;

	private boolean comprado;

	private boolean buscado;

	/**
	 * Appends a new category
	 * @param category2
	 */
	public void appendCategory(String category2) {
		this.setCategory(this.category + " - " + category2);
	}

	/**
	 * Has this category previously
	 * @param category2
	 * @return
	 */
	public boolean hasCategory(String category2) {
		return this.category != null && !this.category.contains(category2);
	}
}
