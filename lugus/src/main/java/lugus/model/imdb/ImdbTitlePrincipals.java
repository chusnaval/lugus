package lugus.model.imdb;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imdb_title_principals", schema = "imdb")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class ImdbTitlePrincipals {

	@Id
	@Embedded
	private ImdbTitlePrincipalsId id;
	
	@Column
	private String job;
	
	@Column
	private String characters;
}
