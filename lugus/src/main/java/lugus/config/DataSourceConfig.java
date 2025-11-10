package lugus.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    DataSource getDataSource() {
		return DataSourceBuilder.create().driverClassName("org.postgresql.Driver")
				.url("jdbc:postgresql://192.168.1.61:5432/lugus?currentSchema=lugus").username("lugus_usr").password("lugusNov2015").build();
	}
}