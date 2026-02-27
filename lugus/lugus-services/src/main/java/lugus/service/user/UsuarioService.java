package lugus.service.user;

import java.util.Optional;

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
	

}
