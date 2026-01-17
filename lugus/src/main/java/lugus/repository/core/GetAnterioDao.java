package lugus.repository.core;

import java.sql.Types;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GetAnterioDao implements GetAnteriorRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	

    @Override
    public String getAnterior(String codigo) {

    	String sql = "SELECT lugus.loc_anterior(?)";
        // fuerza uso de setString pasando argTypes
        Object[] args = new Object[] { codigo };
        int[] argTypes = new int[] { Types.VARCHAR };

        return jdbcTemplate.queryForObject(sql, args, argTypes, String.class);
    }

}
