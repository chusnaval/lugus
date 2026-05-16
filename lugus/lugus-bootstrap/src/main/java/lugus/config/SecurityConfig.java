package lugus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
	        // 1) CORS SIEMPRE PRIMERO
	        .cors(Customizer.withDefaults())

	        // 2) CSRF desactivado para API REST
	        .csrf(csrf -> csrf.disable())

	        // 3) Autorizaciones
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/login",
	                "/css/**",
	                "/js/**",
	                "/public/**",
	                "/api/auth/password-recovery/**",
	                "/api/auth/login",      //  login REST
	                "/actuator/health",
	                "/actuator/health/**",
	                "/actuator/info"
	            ).permitAll()
	            .anyRequest().authenticated()
	        )

	        // 4) Login clásico (solo para web server-side)
	        .formLogin(form -> form
	            .loginPage("/login")
	            .loginProcessingUrl("/perform_login")
	            .defaultSuccessUrl("/guardarUsuario", true)
	            .failureHandler(loginFailureHandler)
	            .permitAll()
	        )

	        // 5) Logout
	        .logout(logout -> logout
	            .logoutUrl("/logout")
	            .logoutSuccessUrl("/login?logout=true")
	            .invalidateHttpSession(true)
	            .deleteCookies("JSESSIONID")
	            .clearAuthentication(true)
	            .permitAll()
	        );

	    return http.build();
	}


	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
}
