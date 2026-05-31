package lugus.service.user;


import java.util.ArrayList;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lugus.dto.user.UserPreferencesDTO;
import lugus.model.user.Usuario;
import lugus.repository.user.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

	private final UsuarioRepository usuarioRepository;

	private final GenreMapper genreMapper;
	private final int MAX_FAVORITOS = 4;
	
	
    public UserPreferencesDTO getPreferences(String username) {
    	Usuario user =findCurrentUser(username);
        return new UserPreferencesDTO(user.getFavoritos().stream().map(genreMapper::toDTO).toList());
    }

	public UserPreferencesDTO savePreferences(String username, UserPreferencesDTO dto) {
		if (dto.favoritos().size() > MAX_FAVORITOS) {
			throw new IllegalArgumentException("Solo puedes elegir 4 géneros favoritos");
		}

		Usuario user = findCurrentUser(username);
		user.setFavoritos( new ArrayList<>(dto.favoritos().stream().map(genreMapper::toEntity).toList()));
		usuarioRepository.save(user);

		return dto;
	}
    

    public Usuario findCurrentUser(String username) {
  

        return usuarioRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }
}
