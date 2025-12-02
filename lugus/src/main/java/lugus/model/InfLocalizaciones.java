package lugus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inf_localizaciones")
@Data 
@EqualsAndHashCode(of = "codigo")
@NoArgsConstructor
@AllArgsConstructor
public class InfLocalizaciones {
	
	@Id
	@NotBlank  
	private String codigo;
	
	@Column
	private String genero;
	
	@Column
	private int contador;
	
	@Column
	private int formato;

}
