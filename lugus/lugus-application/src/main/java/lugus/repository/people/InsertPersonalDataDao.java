package lugus.repository.people;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
public class InsertPersonalDataDao implements InsertPersonalDataRepository {
	@Autowired
	private DataSource dataSource;

	@Override
	public void insert(int peliculaId, String imdb) {
		try (Connection conn = dataSource.getConnection();
				CallableStatement stmt = conn.prepareCall("CALL lugus.insertar_datos_personales(?, ?)")) {

			stmt.setInt(1, peliculaId);
			stmt.setString(2, imdb);
			stmt.execute();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

}
