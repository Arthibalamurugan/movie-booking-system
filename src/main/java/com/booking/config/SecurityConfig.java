package com.booking.config;

import com.booking.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
            // Enable CORS — must be first
            .cors(cors -> cors
                .configurationSource(corsConfigurationSource()))

            // Disable CSRF — we use JWT not sessions
            .csrf(AbstractHttpConfigurer::disable)

            // Route access rules
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers("/api/movies/**").permitAll()
            	    .requestMatchers("/api/shows/**").permitAll()
            	    .requestMatchers("/qrcodes/**").permitAll()
            	    .requestMatchers("/posters/**").permitAll()

            	    .requestMatchers("/api/admin/**")
            	    .hasAuthority("ROLE_ADMIN")

            	    .requestMatchers("/api/bookings/**")
            	    .authenticated()

            	    .anyRequest()
            	    .authenticated()
            	)

            // No sessions — stateless JWT
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authenticationProvider(authenticationProvider())

            // JWT filter runs before Spring's auth filter
            .addFilterBefore(jwtFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from React frontend
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        // Allow these HTTP methods
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers including Authorization
        config.setAllowedHeaders(List.of("*"));

        // Allow cookies and Authorization header
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Apply to all routes
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}