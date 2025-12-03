package lugus.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class InfLocalizacionesId implements Serializable {

    private static final long serialVersionUID = 1L;
	
	@Column(name="genero")
	private String genero;
	
	@Column(name="codigo")
	private String codigo;
	
	@Column(name="funda")
	private boolean funda;
	
	@Column(name="steelbook")
	private boolean steelbook;
	
	@Column(name="formato")
	private int formato;
}
