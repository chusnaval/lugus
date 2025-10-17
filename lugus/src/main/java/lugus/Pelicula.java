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
import jakarta.validation.constraints.NotNull;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotBlank(message = "El título es obligatorio")
	private String titulo;

	@NotNull(message = "El formato es obligatorio")
	@Enumerated(EnumType.ORDINAL)
	private Formato formato;

	/* ---------- 3. Many‑to‑One (Localizacion) ---------- */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "localizacion_codigo", nullable = true) // FK → localizaciones.codigo
	private Localizacion localizacion;

	@Min(1890)
	private int anyo;

	/* ---------- 2. Many‑to‑One (Genero) ---------- */
	@NotNull(message = "El género es obligatorio")
	@Enumerated(EnumType.STRING)
	private Genero genero;

	@NotBlank
	private String codigo;

	private String notas;

	@OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Pelicula> peliculasPack = new HashSet<>();

	private boolean steelbook;

	private boolean funda;
	
	private boolean comprado;

	@ManyToOne
	@JoinColumn(name = "padre_id")
	@ToString.Exclude
	private Pelicula padre;

	public void calcularCodigo() {
		// Eliminar artículos del título
		String procesado = titulo.replaceAll("(?i)\\b(un|the|a|an|el|la|los|las)\\b\\s*", "");

		// Obtener los primeros tres caracteres del título procesado
		String prefijo = procesado.length() >= 3 ? procesado.substring(0, 3).toUpperCase() : procesado.toUpperCase();

		// Obtener la etiqueta del género
		String parteCodigo = genero.getCodigo();

		codigo = prefijo + "-" + parteCodigo + "-" + anyo;

	}

	/**
	 * Vincula las peliculas
	 * 
	 * @param hijo
	 */
	public void addHijo(Pelicula hijo) {
		this.peliculasPack.add(hijo);
		hijo.setPadre(this);

	}
}
