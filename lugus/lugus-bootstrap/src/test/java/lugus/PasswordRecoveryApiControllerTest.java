package lugus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import lugus.model.user.Usuario;
import lugus.repository.user.UsuarioRepository;
import lugus.service.user.PasswordRecoveryNotifier;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
		"spring.profiles.active=test",
		"spring.datasource.url=jdbc:h2:mem:lugus_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.hikari.jdbc-url=jdbc:h2:mem:lugus_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"storage.nfs-root=C:/temp"
})
class PasswordRecoveryApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockBean
	private PasswordRecoveryNotifier passwordRecoveryNotifier;

	private static final String LOGIN = "sara";
	private static final String EMAIL = "sara@example.com";
	private static final String OLD_PASSWORD = "SaraEnero2026";
	private static final String NEW_PASSWORD = "SaraFebrero26";

	private final AtomicReference<String> tokenRef = new AtomicReference<>();

	@BeforeEach
	void setUp() {
		usuarioRepository.deleteById(LOGIN);

		Usuario usuario = Usuario.builder()
				.login(LOGIN)
				.email(EMAIL)
				.password(passwordEncoder.encode(OLD_PASSWORD))
				.admin(false)
				.build();
		usuarioRepository.save(usuario);

		tokenRef.set(null);
		doAnswer(invocation -> {
			tokenRef.set(invocation.getArgument(2));
			return null;
		}).when(passwordRecoveryNotifier).sendRecoveryToken(anyString(), anyString(), anyString(), anyLong());
	}

	@Test
	@DisplayName("Debe permitir recuperar y cambiar contraseña usando los endpoints")
	void shouldResetPasswordUsingRecoveryEndpoints() throws Exception {
		mockMvc.perform(post("/api/auth/password-recovery/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"login\":\"" + LOGIN + "\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").exists());

		String token = tokenRef.get();
		assertThat(token).isNotBlank();

		mockMvc.perform(post("/api/auth/password-recovery/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"login\":\"" + LOGIN + "\",\"token\":\"" + token + "\",\"newPassword\":\"" + NEW_PASSWORD + "\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Contraseña actualizada correctamente."));

		Usuario updated = usuarioRepository.findById(LOGIN).orElseThrow();
		assertThat(passwordEncoder.matches(NEW_PASSWORD, updated.getPassword())).isTrue();
	}
}