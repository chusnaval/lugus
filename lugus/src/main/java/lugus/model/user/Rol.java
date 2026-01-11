package lugus.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@EqualsAndHashCode(of = "role")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

	@Id
	@GeneratedValue
	private Long id;

	private String role;

	private String login;

}
