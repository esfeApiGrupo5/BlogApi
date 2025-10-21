// UsuarioApiService.java
package org.esfe.servicios.externos;

import org.esfe.dtos.blog.UsuarioInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UsuarioApiService {

    @Value("${usuario.api.url:https://api-gateway-8wvg.onrender.com}")
    private String usuarioApiUrl;

    private final RestTemplate restTemplate;

    public UsuarioApiService() {
        this.restTemplate = new RestTemplate();
    }

    public UsuarioInfoDto obtenerUsuario(Long usuarioId, String jwtToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<UsuarioInfoDto> response = restTemplate.exchange(
                    usuarioApiUrl + "/api/usuarios/" + usuarioId,
                    HttpMethod.GET,
                    entity,
                    UsuarioInfoDto.class
            );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            return null;
        }
    }

    public boolean validarToken(String jwtToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    usuarioApiUrl + "/api/auth/validate",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error al validar token: " + e.getMessage());
            return false;
        }
    }
}