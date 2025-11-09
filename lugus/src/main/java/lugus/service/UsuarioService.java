package lugus.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	
	private UsuarioRepository usuarioRepository;
	

}
