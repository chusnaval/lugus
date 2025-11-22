package lugus.model;

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
public class Persona {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	private String nombre;
	
	@OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private Set<PeliculasPersonal> peliculasPersonal = new HashSet<>();
	
	@Column
	private String nconst;
	
	@Column
	private Integer nacimiento;
	
	@Column
	private Integer fallecimiento;
	
}
