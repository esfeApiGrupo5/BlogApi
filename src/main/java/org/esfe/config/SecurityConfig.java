package org.esfe.config;

import org.esfe.Seguridad.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF para APIs REST
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir operaciones de lectura (GET) sin autenticación
                        .requestMatchers(HttpMethod.GET, "/api/blogs/**").permitAll()

                        // Permitir crear blogs a cualquier usuario autenticado
                        .requestMatchers(HttpMethod.POST, "/api/blogs/**").authenticated()

                        // Solo administradores pueden editar y eliminar blogs
                        .requestMatchers(HttpMethod.PUT, "/api/blogs/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/blogs/**").hasRole("ADMINISTRADOR")

                        // Cualquier otro endpoint público
                        .anyRequest().permitAll()
                )
                // Agrega tu filtro de autenticación JWT antes del filtro de autenticación de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}