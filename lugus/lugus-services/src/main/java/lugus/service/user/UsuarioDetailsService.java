package lugus.service.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;


import lugus.repository.user.RolRepository;
import lugus.repository.user.UsuarioRepository;

import java.util.List;


@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;

    public UsuarioDetailsService(UsuarioRepository usuarioRepo, RolRepository rolRepo) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
    }

    @Override
    public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
        lugus.model.user.User usuario = usuarioRepo.findById(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + login));

        List<SimpleGrantedAuthority> roles = rolRepo.findByLogin(login)
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getRole())).toList();

        return new org.springframework.security.core.userdetails.User(usuario.getLogin(), usuario.getPassword(), true, true, true, true, roles);
    }
}
