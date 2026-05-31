package lugus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletResponse;

@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Configuration
public class SecurityConfig {

	@SuppressWarnings("unused")
	private final UserDetailsService userDetailsService;

	public SecurityConfig(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, LoginFailureHandler loginFailureHandler) throws Exception {

		 http
         .csrf(csrf -> csrf.disable())
         .cors(cors -> {})
         .securityContext(security -> security
                 .securityContextRepository(new HttpSessionSecurityContextRepository()) 
             )
         .authorizeHttpRequests(auth -> auth
             .requestMatchers("/lugus/api/auth/login").permitAll()
             .requestMatchers("/lugus/api/auth/me").authenticated()
             .requestMatchers("/lugus/api/**").authenticated()
             .anyRequest().permitAll()
         )
         .formLogin(form -> form.disable())
         .logout(logout -> logout.disable())
         .exceptionHandling(ex -> ex
             .authenticationEntryPoint((req, res, e) -> {
                 res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             })
         )
         .sessionManagement(sess -> sess
        				    .maximumSessions(-1)
        				    .sessionRegistry(sessionRegistry())
        				);

        

	    return http.build();
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
	    return new WebMvcConfigurer() {
	        @Override
	        public void addCorsMappings(@NonNull CorsRegistry registry) {
	            registry.addMapping("/**")
	                .allowedOrigins("http://localhost:5173","http://localhost:9000")
	                .allowedMethods("*")
	                .allowCredentials(true);
	        }
	    };
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public SessionRegistry sessionRegistry() {
	    return new SessionRegistryImpl();
	}
}
