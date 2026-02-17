package lugus;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashMain {

	public static void main(String[] args) {
		if (args.length == 0 || args[0] == null || args[0].isBlank()) {
			System.out.println("Uso: PasswordHashMain <password-en-texto-plano>");
			return;
		}

		String rawPassword = args[0];
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String hash = encoder.encode(rawPassword);

		System.out.println("Password plana: " + rawPassword);
		System.out.println("BCrypt hash    : " + hash);
	}
}