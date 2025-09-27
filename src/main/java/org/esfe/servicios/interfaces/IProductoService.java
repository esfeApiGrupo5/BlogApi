package org.esfe.servicios.interfaces;

import org.esfe.dtos.producto.ProductoGuardar;
import org.esfe.dtos.producto.ProductoModificar;
import org.esfe.dtos.producto.ProductoSalida;
import org.esfe.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductoService {
    List<ProductoSalida> obtenerTodos();

    Page<ProductoSalida> obtenerTodosPaginados(Pageable pageable);

    ProductoSalida obtenerPorId(Integer id);

    ProductoSalida crear(ProductoGuardar productoGuardar);

    ProductoSalida editar(ProductoModificar productoModificar);

    void eliminarPorId(Integer id);

    List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);

}
