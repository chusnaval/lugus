package lugus.infrastructure.repository.core;

import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import lugus.repository.core.GetNextRepository;



@Component
public class GetPosteriorDao implements GetNextRepository {

	private SimpleJdbcCall call;

	public GetPosteriorDao(@NonNull DataSource dataSource) {
		this.call = new SimpleJdbcCall(dataSource)
				.withSchemaName("lugus").withFunctionName("loc_posterior")
				.withoutProcedureColumnMetaDataAccess()
				.declareParameters(new SqlParameter("code", Types.VARCHAR))
				.declareParameters(new SqlOutParameter("loc_pos", Types.VARCHAR));

	}

	@Override
	public String getNext(String codigo) {

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("code", codigo, Types.VARCHAR);

		return call.executeFunction(String.class, params);
	}

}