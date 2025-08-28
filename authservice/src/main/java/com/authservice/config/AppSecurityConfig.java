package com.authservice.config;

//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.authservice.service.CustomerUserDetailsService;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/* Configure how to keep:
 * 1. url open and
 * 2. url to authenticate
 * 3. Authorization
 */
@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

	@Autowired
	private CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired
	private JwtFilter filter;
	
	private String[] openUrl = { 
			"/api/v1/auth/register",
			"/api/v1/auth/login", 
			"/v3/api-docs/**", 
			"/swagger-ui/**", 
			"/swagger-ui.html",
			"/swagger-resources/**", 
			"/webjars/**" };

	
	@Bean
	public PasswordEncoder getEncodedPassword() {
		return new BCryptPasswordEncoder();
	}

	
	@Bean
	public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	
	@Bean
	public AuthenticationProvider authProvider() {

		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(customerUserDetailsService);
		authProvider.setPasswordEncoder(getEncodedPassword());

		return authProvider;
	}

	
	@Bean
	public SecurityFilterChain securityConfig(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Disable CSRF
				.authorizeHttpRequests(req -> {
					req.requestMatchers(openUrl).permitAll()
					.requestMatchers("/api/v1/welcome/message").hasAnyRole("USER","ADMIN")
					.anyRequest().authenticated();
				}) .authenticationProvider(authProvider())
		        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		return http.build();

	}
	
//	@Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOriginPatterns(List.of("http://localhost:3000")); // ✅ React origin
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        config.setAllowedHeaders(List.of("*"));
//        config.setAllowCredentials(true); // ✅ Important for cookies/token
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
	
}
