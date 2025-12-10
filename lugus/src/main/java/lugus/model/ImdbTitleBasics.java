package lugus.model;

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
@Table(name = "imdb_title_basics", schema = "imdb")
@Getter
@Setter
@EqualsAndHashCode(of = "tconst")
@NoArgsConstructor
@AllArgsConstructor
public class ImdbTitleBasics {

	@Id
	private String tconst;
	
	@Column
	private String titletype;
	
	@Column
	private String primarytitle;
	
	@Column
	private String originaltitle;
	
	@Column
	private Integer isadult;
	
	@Column
	private String startyear;
	
	@Column
	private String endyear;
	
	@Column
	private Integer runtimeminutes;
	
	@Column
	private String[] genres;
	
}
