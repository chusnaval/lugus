package lugus.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.model.Usuario;
import lugus.repository.UsuarioRepository;

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
