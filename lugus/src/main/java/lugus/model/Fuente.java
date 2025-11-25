package lugus.model;

import java.util.HashSet;
import java.util.Set;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "fuentes")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fuente {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
    @Column
	private String descripcion;
    
	@OneToMany(mappedBy = "fuente")
	@ToString.Exclude
	private final Set<PeliculaFoto> peliculaFotos = new HashSet<>();
}
