// BlogSalida.java
package org.esfe.dtos.blog;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class BlogSalida implements Serializable {
    private Integer id;
    private String titulo;
    private String descripcion;
    private String autor;
    private Long usuarioId;
    private LocalDateTime fechaPublicacion;

    // Información del usuario (obtenida desde UsuarioApi)
    private UsuarioInfoDto usuarioInfo;
}
//se añadio comentario para poder subir el commit y relacionarlo con jira ya que sino no deja vincularlo.