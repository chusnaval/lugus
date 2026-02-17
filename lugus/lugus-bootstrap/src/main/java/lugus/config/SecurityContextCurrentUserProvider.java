package lugus.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lugus.service.user.CurrentUserProvider;

@Component
public class SecurityContextCurrentUserProvider implements CurrentUserProvider {

	@Override
	public String currentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			throw new IllegalStateException("No hay usuario autenticado en el contexto de seguridad");
		}
		return authentication.getName();
	}
}
