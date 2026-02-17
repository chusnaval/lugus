package lugus.infrastructure.repository.core;

import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import lugus.repository.core.GetPosteriorRepository;

@Component
public class GetPosteriorDao implements GetPosteriorRepository {

	private SimpleJdbcCall call;

	public GetPosteriorDao(DataSource dataSource) {
		this.call = new SimpleJdbcCall(dataSource)
				.withSchemaName("lugus").withFunctionName("loc_posterior")
				.withoutProcedureColumnMetaDataAccess()
				.declareParameters(new SqlParameter("code", Types.VARCHAR))
				.declareParameters(new SqlOutParameter("loc_pos", Types.VARCHAR));

	}

	@Override
	public String getPosterior(String codigo) {

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("code", codigo, Types.VARCHAR);

		return call.executeFunction(String.class, params);
	}

}