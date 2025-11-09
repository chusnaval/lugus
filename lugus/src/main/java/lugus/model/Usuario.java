package lugus.model;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
	
}
