
// UsuarioInfoDto.java - DTO para la información del usuario
package org.esfe.dtos.blog;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class UsuarioInfoDto implements Serializable {
    private Long id;
    private String nombre;
    private String correo;
    private String rolNombre;
}