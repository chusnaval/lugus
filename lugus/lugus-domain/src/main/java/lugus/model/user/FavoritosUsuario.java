package lugus.model.user;

import jakarta.persistence.*;
import lombok.*;
import lugus.model.films.Pelicula;
import lugus.model.series.Serie;

@Entity
@Table(name = "favoritos_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritosUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_login", referencedColumnName = "login")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id")
    private Pelicula pelicula;

    @ManyToOne
    @JoinColumn(name = "serie_id", referencedColumnName = "id")
    private Serie serie;

    @Column(name = "fecha_agregado")
    private java.time.Instant fechaAgregado;
}
