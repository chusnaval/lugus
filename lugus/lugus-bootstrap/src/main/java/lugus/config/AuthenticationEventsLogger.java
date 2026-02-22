package lugus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventsLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEventsLogger.class);

	@EventListener
	public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
		String username = event.getAuthentication() != null
				? String.valueOf(event.getAuthentication().getPrincipal())
				: "unknown";
		String reason = event.getException() != null ? event.getException().getMessage() : "unknown";
		LOGGER.warn("Login fallido para usuario='{}' motivo='{}'", username, reason);
	}

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		if (event.getAuthentication() != null) {
			LOGGER.info("Login correcto para usuario='{}'", event.getAuthentication().getName());
		}
	}
}
