package lugus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CrudSpringbootApplication  extends SpringBootServletInitializer {


	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return customizerBuilder(builder);
	}
	
	public static void main(String[] args) {
		   SpringApplication.run(CrudSpringbootApplication.class, args);
	}


	private static SpringApplicationBuilder customizerBuilder(SpringApplicationBuilder builder) {
		return builder.sources(CrudSpringbootApplication.class).bannerMode(org.springframework.boot.Banner.Mode.OFF);
	}
	
}
