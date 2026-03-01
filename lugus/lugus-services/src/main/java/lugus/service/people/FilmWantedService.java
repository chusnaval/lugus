package lugus.service.people;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import lugus.model.films.FilmWanted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilmWantedService {

    private final JdbcTemplate jdbcTemplate;

    public List<FilmWanted> findAllOrdered() {
        String sql = """
                SELECT titulo, anyo, formato
                FROM lugus.pel_wanted
                ORDER BY titulo, anyo, formato
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> FilmWanted.builder()
                .titulo(rs.getString("titulo"))
                .anyo(rs.getInt("anyo"))
                .formato(rs.getString("formato"))
                .build());
    }
}
