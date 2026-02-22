package lugus.infrastructure.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lugus.service.user.PasswordRecoveryNotifier;

@Component
@RequiredArgsConstructor
public class SmtpPasswordRecoveryNotifier implements PasswordRecoveryNotifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmtpPasswordRecoveryNotifier.class);

	private final ObjectProvider<JavaMailSender> mailSenderProvider;

	@Value("${lugus.recovery.mail.enabled:false}")
	private boolean mailEnabled;

	@Value("${lugus.recovery.mail.from:no-reply@lugus.local}")
	private String from;

	@Value("${lugus.recovery.mail.subject:Recuperacion de contraseña}")
	private String subject;

	@Override
	public void sendRecoveryToken(String login, String email, String token, long expiresInSeconds) {
		if (!mailEnabled) {
			LOGGER.info("Password recovery email disabled. login={}", login);
			return;
		}

		JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
		if (mailSender == null) {
			LOGGER.warn("Password recovery email enabled but JavaMailSender is not configured. login={}", login);
			return;
		}

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(from);
			message.setTo(email);
			message.setSubject(subject);
			message.setText(buildBody(login, token, expiresInSeconds));
			mailSender.send(message);
		} catch (Exception ex) {
			LOGGER.error("Error sending recovery token email for login={}", login, ex);
		}
	}

	private String buildBody(String login, String token, long expiresInSeconds) {
		long minutes = Math.max(1, expiresInSeconds / 60);
		return "Hola " + login + ",\n\n"
				+ "Hemos recibido una solicitud de recuperación de contraseña.\n"
				+ "Tu token de recuperación es:\n\n"
				+ token + "\n\n"
				+ "Este token caduca en " + minutes + " minutos.\n"
				+ "Si no has solicitado este cambio, ignora este correo.\n";
	}

}