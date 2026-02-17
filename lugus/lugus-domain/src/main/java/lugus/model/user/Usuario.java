package lugus.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
	
}
