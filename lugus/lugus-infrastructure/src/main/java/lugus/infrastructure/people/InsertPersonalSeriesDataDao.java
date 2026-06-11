package lugus.infrastructure.people;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import lugus.repository.people.InsertPersonalSeriesDataRepository;

@Component
public class InsertPersonalSeriesDataDao implements InsertPersonalSeriesDataRepository {
	private final DataSource dataSource;

	public InsertPersonalSeriesDataDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void insert(int seriesId, String imdb) {
		try (Connection conn = dataSource.getConnection();
				CallableStatement stmt = conn.prepareCall("CALL lugus.insertar_datos_personales_series(?, ?)")) {

			stmt.setInt(1, seriesId);
			stmt.setString(2, imdb);
			stmt.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

}
