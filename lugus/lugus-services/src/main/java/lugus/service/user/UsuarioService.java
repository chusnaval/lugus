package lugus.service.user;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.user.Usuario;
import lugus.repository.user.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	
	private final UsuarioRepository usuarioRepository;

	public Optional<Usuario> findByLogin(String login) {
		return usuarioRepository.findById(login);
	}

	public void save(Usuario usuario) {
		usuarioRepository.save(usuario);
	}
	
	public void changePassword(String login, String current, String newPass) {
	    Usuario user = findByLogin(login).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		PasswordEncoder encoder = new BCryptPasswordEncoder();
	    if (!encoder.matches(current, user.getPassword())) {
	        throw new RuntimeException("La contraseña actual no es correcta");
	    }

	    user.setPassword(encoder.encode(newPass));
	    usuarioRepository.save(user);
	}

}
