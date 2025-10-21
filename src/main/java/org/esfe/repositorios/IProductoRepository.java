package org.esfe.repositorios;

import org.esfe.modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface IProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByDescripcionContainingIgnoreCase(String descripcion);
}
