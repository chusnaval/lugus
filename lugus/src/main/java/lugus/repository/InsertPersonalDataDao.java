package lugus.repository;

import java.sql.Types;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
@Component
public class InsertPersonalDataDao implements InsertPersonalDataRepository {
	private final SimpleJdbcCall insertCall;

	public InsertPersonalDataDao(DataSource ds) {
		this.insertCall = new SimpleJdbcCall(ds).withProcedureName("insertar_datos_personales").declareParameters(
				new SqlParameter("p_id", Types.NUMERIC), new SqlParameter("p_imdb", Types.VARCHAR));
	}

	@Override
	public void insert(int peliculaId, String imdb) {
		insertCall.execute(peliculaId, imdb);

	}

}
