package lugus.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritosUsuarioDto {
    private Long id;
    private Integer peliculaId;
    private String usuarioLogin;
    private String fechaAgregado;
}
