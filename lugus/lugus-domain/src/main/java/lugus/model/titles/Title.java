package lugus.model.titles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lugus.model.films.Pelicula;
import lugus.model.imdb.ImdbTitleBasics;
import lugus.model.series.Serie;
import lugus.model.values.TitleType;

@Entity
@Table(name = "titles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del título (película, serie o externo)
    @Column(nullable = false)
    private String title;

    // Año de estreno
    @Column(nullable = true)
    private Integer year;

    // Tipo de título: MOVIE, SERIES, EXTERNAL
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TitleType type;

    // Si es una película de tu colección
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pelicula_id")
    private Pelicula pelicula;

    // Si es una serie de tu colección
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    // Si es un título externo (IMDB)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imdb_id")
    private ImdbTitleBasics imdb;

    // URL de portada (opcional)
    @Column(nullable = true)
    private String posterUrl;
    

}
