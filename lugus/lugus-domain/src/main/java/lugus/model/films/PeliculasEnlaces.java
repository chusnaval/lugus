package lugus.model.films;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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


@Entity
@Table(name = "peliculas_enlaces")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PeliculasEnlaces {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "pelicula_id", nullable = true) // FK → locations.code
	private Pelicula pelicula;
	
	@Column(name = "fuenteId")
	private int fuenteId;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "usr_alta")
	private String usrAlta;

	@Column(name = "ts_alta", nullable = false, columnDefinition = "TIMESTAMP")
	private Instant tsAlta;

	@Column(name = "usr_modif")
	private String usrModif;

	@Column(name = "ts_modif", columnDefinition = "TIMESTAMP")
	private Instant tsModif;
        
	@Column
	private boolean trailer;
}
