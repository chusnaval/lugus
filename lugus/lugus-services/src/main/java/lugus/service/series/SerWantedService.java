package lugus.service.series;

import lugus.model.series.SerWanted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SerWantedService {
    private final JdbcTemplate jdbcTemplate;

    public List<SerWanted> findAllOrdered() {
        String sql = """
                SELECT titulo, anyo_inicio, season_desc
                FROM lugus.ser_wanted
                ORDER BY titulo, anyo_inicio, season_desc
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> SerWanted.builder()
                .titulo(rs.getString("titulo"))
                .anyoInicio(rs.getInt("anyo_inicio"))
                .seasonDesc(rs.getString("season_desc"))
                .build());
    }
}
