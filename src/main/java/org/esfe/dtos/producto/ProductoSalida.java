package org.esfe.dtos.producto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class ProductoSalida implements Serializable {
    private Integer id;

    private String nombre;

    private Double precio;

    private String descripcion;

    private Integer stock = 0;

    private String categoria;

    private String urlImagen;
}
