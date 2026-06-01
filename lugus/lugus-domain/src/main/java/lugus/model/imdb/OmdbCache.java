package lugus.model.imdb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "omdb_cache", schema = "lugus")
@Getter
@Setter
@EqualsAndHashCode(of = "imdbId")
public class OmdbCache {
    @Id
    @Column(name = "imdb_id")
    private String imdbId;

    @Lob
    private String json;
}
