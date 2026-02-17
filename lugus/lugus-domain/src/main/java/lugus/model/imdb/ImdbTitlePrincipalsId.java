package lugus.model.imdb;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ImdbTitlePrincipalsId {

	@Column
	private String tconst;
	
	@Column
	private String nconst;
	
	@Column
	private int ordering;
	
	@Column
	private String category;


	
}
