package lugus.model.people;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "personas")
@Data 
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class People {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="nombre")
	private String name;
	
	@OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private Set<Staff> filmStaff = new HashSet<>();
	
	@OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private Set<SeriesPersonal> seriesStaff = new HashSet<>();
	
	@Column
	private String nconst;
	
	@Column(name="nacimiento")
	private Integer birth;
	
	@Column(name="fallecimiento")
	private Integer dead;
	
}
