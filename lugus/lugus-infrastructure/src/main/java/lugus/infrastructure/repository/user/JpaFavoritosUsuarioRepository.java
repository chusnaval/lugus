package lugus.infrastructure.repository.user;

import lugus.model.user.FavoritosUsuario;

import lugus.repository.user.FavoritosUsuarioRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaFavoritosUsuarioRepository
        extends FavoritosUsuarioRepository, JpaRepository<FavoritosUsuario, Long> {

}
