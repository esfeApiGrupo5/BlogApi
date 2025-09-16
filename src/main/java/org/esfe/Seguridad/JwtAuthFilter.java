package org.esfe.Seguridad;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.esfe.Utilidades.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String username = null;

        // Verificar si la cabecera de autorización existe y tiene el prefijo "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token
        jwt = authHeader.substring(7);

        try {
            // Validar el token
            if (jwtUtil.validateToken(jwt)) {
                username = jwtUtil.extractUsername(jwt);
                Long userId = jwtUtil.extractUserId(jwt);
                List<String> roles = jwtUtil.extractRoles(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Convertir roles a GrantedAuthority
                    List<GrantedAuthority> authorities = roles != null ?
                            roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList()) :
                            new ArrayList<>();

                    // Crear autenticación personalizada que incluye userId
                    BlogUserAuthentication authToken = new BlogUserAuthentication(
                            username, userId, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer la autenticación en el contexto de Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // Clase personalizada para autenticación que incluye userId
    public static class BlogUserAuthentication extends UsernamePasswordAuthenticationToken {
        private final Long userId;

        public BlogUserAuthentication(String principal, Long userId, Object credentials,
                                      List<GrantedAuthority> authorities) {
            super(principal, credentials, authorities);
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }
}