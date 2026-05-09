package lugus.model.imdb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imdbBasics", schema = "lugus")
@Getter
@Setter
@EqualsAndHashCode(of = "titleid")
@NoArgsConstructor
@AllArgsConstructor
public class ImdbBasics {

	@Id
	private String titleid;
	
	@Column
	private String title;

	@Column
	private String region;
	
	@Column
	private String language;
	
	@Column
	private String titletype;
	
	@Column
	private String startyear;
}
