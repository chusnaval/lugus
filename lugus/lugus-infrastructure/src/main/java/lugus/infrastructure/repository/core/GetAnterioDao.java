package lugus.infrastructure.repository.core;

import java.sql.Types;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lugus.repository.core.GetPreviousRepository;



@Component
public class GetAnterioDao implements GetPreviousRepository {

	private final JdbcTemplate jdbcTemplate;

	public GetAnterioDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public String getPrevious(String codigo) {

		String sql = "SELECT lugus.loc_anterior(?)";
		Object[] args = new Object[] { codigo };
		int[] argTypes = new int[] { Types.VARCHAR };

		return jdbcTemplate.queryForObject(sql, args, argTypes, String.class);
	}

}