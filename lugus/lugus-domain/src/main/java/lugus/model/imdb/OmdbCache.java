package lugus.model.imdb;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "json", columnDefinition = "jsonb")
    private JsonNode  json;
}
