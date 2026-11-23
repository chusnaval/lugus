package lugus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashMain {

	private static final Logger LOGGER = LoggerFactory.getLogger(PasswordHashMain.class);

	public static void main(String[] args) {
		if (args.length == 0 || args[0] == null || args[0].isBlank()) {
			LOGGER.error("Uso: PasswordHashMain <password-en-texto-plano>");
			return;
		}

		String rawPassword = args[0];
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String hash = encoder.encode(rawPassword);

		LOGGER.error("Password plana: {} ", rawPassword);
		LOGGER.error("BCrypt hash: {} ", hash);
	}
}