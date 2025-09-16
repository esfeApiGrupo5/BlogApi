package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    private String descripcion;

    private String autor;

    // NUEVA COLUMNA: Referencia al usuario por ID
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    private LocalDateTime fechaPublicacion;
}