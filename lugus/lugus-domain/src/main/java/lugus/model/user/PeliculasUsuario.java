package lugus.model.user;

import jakarta.persistence.*;
import lombok.*;
import lugus.model.films.Pelicula;

@Entity
@Table(name = "peliculas_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculasUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_login", referencedColumnName = "login")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pelicula_id", referencedColumnName = "id")
    private Pelicula pelicula;

    @Column(name = "fecha_agregado")
    private java.time.Instant fechaAgregado;
}
