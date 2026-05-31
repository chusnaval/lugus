package lugus.service.core;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRegistry sessionRegistry;

    public void expireUserSessions(String username) {
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            if (principal instanceof UserDetails user && user.getUsername().equals(username)) {
                sessionRegistry.getAllSessions(principal, false)
                        .forEach(SessionInformation::expireNow);
            }
        });
    }
}
