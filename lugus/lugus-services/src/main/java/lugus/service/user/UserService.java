package lugus.service.user;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.user.User;
import lugus.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository usuarioRepository;

	public Optional<User> findByLogin(final String login) {
		return usuarioRepository.findById(login);
	}

	public void save(final User user) {
		usuarioRepository.save(user);
	}
	
	public void changePassword(final String login, final String current, final String newPass) {
	    User user = findByLogin(login).orElseThrow(() -> new RuntimeException("Not founded user"));
		PasswordEncoder encoder = new BCryptPasswordEncoder();
	    if (!encoder.matches(current, user.getPassword())) {
	        throw new RuntimeException("Incorrect password");
	    }

	    user.setPassword(encoder.encode(newPass));
	    usuarioRepository.save(user);
	}

}
