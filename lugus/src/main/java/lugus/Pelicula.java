package lugus;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "peliculas")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {

	@Id
	@NotBlank  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotBlank  
	private String titulo;
	
	@NotBlank  
	@Enumerated(EnumType.ORDINAL)
	private Formato formato;


    /* ---------- 3. Many‑to‑One (Localizacion) ---------- */
	@NotBlank  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_codigo") // FK → localizaciones.codigo
	private Localizacion localizacion;

	@Min(1890)
	private int anyo;

	 /* ---------- 2. Many‑to‑One (Genero) ---------- */
	@NotBlank  
    @ManyToOne(fetch = FetchType.LAZY)    // carga perezosa (optimiza consultas)
    @JoinColumn(name = "genero_codigo")   // FK → generos.codigo
	private Genero genero;

	@NotBlank  
	private String codigo;

	private String notas;
	
	@OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Pelicula> peliculasPack = new HashSet<>();

	private boolean steelbook;
	
	private boolean funda;
	
    @ManyToOne
    @JoinColumn(name = "padre_id")
    @ToString.Exclude
    private Pelicula padre;
}
