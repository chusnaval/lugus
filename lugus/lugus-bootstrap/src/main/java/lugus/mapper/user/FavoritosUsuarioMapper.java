package lugus.mapper.user;

import lugus.dto.user.FavoritosUsuarioDto;
import lugus.model.user.FavoritosUsuario;
import org.springframework.stereotype.Component;

@Component
public class FavoritosUsuarioMapper {
    public FavoritosUsuarioDto toDto(FavoritosUsuario entity) {
        FavoritosUsuarioDto dto = new FavoritosUsuarioDto();
        dto.setId(entity.getId());
        dto.setPeliculaId(entity.getPelicula() != null ? entity.getPelicula().getId() : null);
        dto.setUsuarioLogin(entity.getUsuario() != null ? entity.getUsuario().getLogin() : null);
        dto.setFechaAgregado(entity.getFechaAgregado() != null ? entity.getFechaAgregado().toString() : null);
        return dto;
    }
}
