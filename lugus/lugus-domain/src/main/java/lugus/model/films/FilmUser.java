package lugus.model.films;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lugus.model.user.User;

@Entity
@Table(name = "peliculas_usuario")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class FilmUser {

  
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

    /** Relacion con el usuario */
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_login", nullable = false)
    private User user;

    /** Relacion con pelicula */
    @ManyToOne(optional = false)
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Film film;
    
    @Column (name="fecha_agregado")
    private java.time.LocalDateTime additionDate;

    @Column(name = "lbrating")
    private Float lbRating;

    @Column (name="vista")
    private boolean seen;
    
    @Column (name="favorita")
    private boolean favorite;

    @Column (name ="fecha_vista")
    private  java.time.LocalDateTime seenDate;
}
