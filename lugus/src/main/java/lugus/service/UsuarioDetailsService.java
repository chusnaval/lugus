package lugus.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import lugus.model.Usuario;
import lugus.repository.UsuarioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;

    public UsuarioDetailsService(UsuarioRepository usuarioRepo, RolRepository rolRepo) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findById(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + login));

        List<SimpleGrantedAuthority> roles = rolRepo.findByLogin(login)
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getRole()))
                .collect(Collectors.toList());

        return new User(usuario.getLogin(), usuario.getPassword(), true, true, true, true, roles);
    }
}
