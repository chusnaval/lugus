package lugus.model.user;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lugus.converter.GeneroConverter;
import lugus.model.values.Genero;

@Entity
@Table(name = "usuarios")
@Data
@EqualsAndHashCode(of = "login")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
	
	@Id
	private String login;
	
	@Column
	private String password;

	@Column
	private String email;
	
	@Column
	private boolean admin;
	
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "usuario_favoritos",
        joinColumns = @JoinColumn(name = "login")
    )
    @Column(name = "genero")
	@Convert(converter = GeneroConverter.class)
    private List<Genero> favoritos = new ArrayList<>();
	
}
