package lugus.service.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;


import lugus.repository.user.RoleRepository;
import lugus.repository.user.UserRepository;

import java.util.List;


@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UserRepository usuarioRepo;
    private final RoleRepository rolRepo;

    public UsuarioDetailsService(UserRepository usuarioRepo, RoleRepository rolRepo) {
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
