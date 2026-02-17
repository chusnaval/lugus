package lugus.service.user;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.user.Usuario;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

	private static final long TOKEN_TTL_MINUTES = 15;

	private final UsuarioService usuarioService;
	private final PasswordEncoder passwordEncoder;
	private final PasswordRecoveryNotifier passwordRecoveryNotifier;
	private final ConcurrentHashMap<String, RecoveryToken> tokenStore = new ConcurrentHashMap<>();
	private final SecureRandom secureRandom = new SecureRandom();

	public void createRecoveryToken(String login) {
		clearExpiredTokens();

		Optional<Usuario> usuarioOpt = usuarioService.findByLogin(login);
		if (usuarioOpt.isEmpty()) {
			return;
		}

		Usuario usuario = usuarioOpt.get();
		if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
			return;
		}

		String token = generateToken();
		Instant expiresAt = Instant.now().plus(TOKEN_TTL_MINUTES, ChronoUnit.MINUTES);
		tokenStore.put(token, new RecoveryToken(login, expiresAt));
		passwordRecoveryNotifier.sendRecoveryToken(login, usuario.getEmail(), token, TOKEN_TTL_MINUTES * 60);
	}

	public boolean resetPassword(String login, String token, String newPassword) {
		clearExpiredTokens();

		RecoveryToken recoveryToken = tokenStore.get(token);
		if (recoveryToken == null || !recoveryToken.login().equals(login) || recoveryToken.expiresAt().isBefore(Instant.now())) {
			return false;
		}

		Optional<Usuario> usuarioOpt = usuarioService.findByLogin(login);
		if (usuarioOpt.isEmpty()) {
			return false;
		}

		Usuario usuario = usuarioOpt.get();
		usuario.setPassword(passwordEncoder.encode(newPassword));
		usuarioService.save(usuario);
		tokenStore.remove(token);

		return true;
	}

	private void clearExpiredTokens() {
		Instant now = Instant.now();
		tokenStore.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
	}

	private String generateToken() {
		byte[] randomBytes = new byte[24];
		secureRandom.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}

	private record RecoveryToken(String login, Instant expiresAt) {
	}
}