package org.esfe.dtos.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class ProductoModificar implements Serializable {
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El precio no puede estar vacío")
    private Double precio;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;

    @NotBlank(message = "El stock no puede estar vacío")
    private Integer stock = 0;

    @NotBlank(message = "La categoría no puede estar vacía")
    @Size(max = 50, message = "La categoría no puede exceder los 50 caracteres")
    private String categoria;

    @NotBlank(message = "La URL de la imagen no puede estar vacía")
    private String urlImagen;

}
